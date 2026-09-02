# G 模块修改清单（组长转给 G）

> 背景：你的代码已经合并进整合分支，能编译、能启动。但**兑换、收藏列表、出行人列表、评价、反馈**这些接口实测全部报 500。
> 根因是两类：① Mapper XML 没被加载；② 多处 SQL/实体用的列名和数据库表结构对不上。
> 下面是逐条修改要求，按顺序改完重启即可。数据库表结构以 `backend/src/main/resources/schema.sql` 为准，不要改表结构，改代码适配表结构。

---

## 1. XML 文件放错位置（导致所有自定义 SQL 报错）

**现象**：日志报 `Invalid bound statement (not found): ...g.mapper.xxx.selectByXxx`

**原因**：`application.yml` 配置的是 `mybatis-plus.mapper-locations: classpath*:/mapper/**/*.xml`（扫描 `mapper/` 子目录），
但你的 6 个 XML 放在 `backend/src/main/resources/` 根目录；而且根目录下误建了一个**空文件** `mapper`（本来应该是文件夹）。

**修改**：
1. 删除空文件：`backend/src/main/resources/mapper`（一个 0 字节的文件）
2. 新建文件夹：`backend/src/main/resources/mapper/`
3. 把下面 6 个 XML 移进该文件夹：
   - `FavoriteMapper.xml`
   - `FeedbackMapper.xml`
   - `RedeemMapper.xml`
   - `RedemptionCodeMapper.xml`
   - `ReviewMapper.xml`
   - `TravelerMapper.xml`
4. `application.yml` 不用改。

---

## 2. XML 里两处类名拼写错误（多了空格）

- `RedeemMapper.xml`：`type="com.example.camp booking.g.entity.Coupon"` → 去掉空格，改为 `com.example.campbooking.g.entity.Coupon`
- `TravelerMapper.xml`：`namespace="com.example.camp booking.g.mapper.TravelerMapper"` → 去掉空格，改为 `com.example.campbooking.g.mapper.TravelerMapper`

---

## 3. 兑换码表列名不一致（RedemptionCode）

**表 `redemption_codes` 的真实列**：`id, code, type, code_value, is_used, used_by, used_at, created_at`

你代码里用了 `value`、`used`、`expire_at`，这三个列都不存在。请改：

- `RedemptionCode.java`：
  - `private Integer value;` → `private Integer codeValue;`，并加注解 `@TableField("code_value")`
  - `used` 字段加注解 `@TableField("is_used")`
  - 删除 `expireAt` 字段（表里没有 `expire_at` 列）
- `RedemptionCodeMapper.xml`：
  - SELECT 改为：`SELECT id, code, type, code_value, is_used, used_by, used_at, created_at FROM redemption_codes WHERE code = #{code} LIMIT 1`
  - UPDATE 改为：`UPDATE redemption_codes SET is_used = 1, used_by = #{usedBy}, used_at = #{usedAt} WHERE id = #{id}`
  - resultMap 同步改成 `code_value → codeValue`、`is_used → used`，删掉 `expire_at` 行
- `RedeemServiceImpl.java`：`rc.getValue()` 全部改成 `rc.getCodeValue()`

---

## 4. 兑换 SQL 用了不存在的列（RedeemMapper.xml + RedeemServiceImpl）

**表 `coupons` 的真实列**：`id, title, type, discount_value, min_amount, total_count, received_count, valid_days, rules, status, start_time, end_time`（没有 `code/name/value`）
**表 `user_coupons` 的真实列**：`id, user_id, coupon_id, coupon_title, status, received_at, used_at, expire_at`（没有 `code` 列）

现有种子数据的含义：`redemption_codes.code_value` 存的就是**优惠券 id**：
- `WELCOME2024` → `1`，`CAMP2024` → `3`，`FREEROOM` → `5`，`SUMMER2024` → `200`（积分，见第 7 条）

**推荐改法（改代码适配表结构）**：
1. `RedeemMapper.xml`：删掉 `insertCoupon` 和 `selectCouponByCode`；新增：
   - `selectCouponById`：`SELECT id, title, type, discount_value, min_amount, total_count, received_count, valid_days, rules, status, start_time, end_time FROM coupons WHERE id = #{id}`
   - `insertUserCoupon`：`INSERT INTO user_coupons (user_id, coupon_id, coupon_title, status, received_at, expire_at) VALUES (#{userId}, #{couponId}, #{couponTitle}, #{status}, #{receivedAt}, #{expireAt})`
2. `g/entity/UserCoupon.java`：把 `code` 字段删掉，改成 `couponTitle`（String）；对应 XML resultMap 同步改
3. `RedeemServiceImpl.java` 的 `coupon`/`free_room` 分支重写为：
   - `Long couponId = Long.parseLong(rc.getCodeValue());`
   - `Coupon template = redeemMapper.selectCouponById(couponId);` 为空则抛 `BusinessException(404, "优惠券不存在")`
   - 写 `user_coupons`：`couponId=template.getId()`、`couponTitle=template.getTitle()`、`status="usable"`、`receivedAt=now`、`expireAt=now.plusDays(template.getValidDays())`
   - 最后 `redemptionCodeMapper.markUsed(...)`

---

## 5. user_coupons 的 status 必须写 "usable"

你原来写的是 `"unused"`，但 F 的卡券/订单模块只认 `usable` / `used`。
写成 `"unused"` 会导致：前端卡券包看不到、下单时选不到券。**一律写 `"usable"`。**

---

## 6. 评价模块列名不一致（Review）

**表 `reviews` 的真实列**：`id, user_id, product_id, order_id, rating, content, images, type, created_at`
（注意是 `product_id`，没有 `target_id`；`product_id` 和 `order_id` 都是 NOT NULL）

你的实体/XML 用了 `target_id`，且没给 `product_id` 赋值，插入必报错。请改：
- `Review.java`：`targetId` 改成 `productId`，映射 `product_id`
- `ReviewMapper.xml`：resultMap 和 `selectByTarget` 的 SQL 全部用 `product_id`；查询条件 `WHERE type = #{targetType} AND product_id = #{productId}`
- `ReviewServiceImpl.add`：设置 `productId`；`orderId`、`rating` 必填（前端要传真实值）
- 评价前需要先有 F 模块的订单；`order_id` 关联 `orders` 表，`product_id` 关联 `products` 表，别乱填
- 代码里"已支付才能评价"的 TODO 可以补上了：注入 `OrderMapper`，校验 `order.getUserId().equals(userId) && "paid".equals(order.getStatus())`

---

## 7. 积分兑换没真正加积分（TODO）

`SUMMER2024`（points 200）现在只是把码标记成已使用，积分没进账户。请补：
- 注入 F 模块的 `com.example.campbooking.mapper.UserMapper` 和 `com.example.campbooking.mapper.PointsRecordMapper`
- 兑换 `points` 时：
  1. 查出用户，`pointsBalance + gained` 后 `userMapper.updateById(user)`
  2. 插入一条 `points_records`：`amount=gained`、`type='earn'`、`source='redeem'`、`remark='兑换码兑换'`
  3. 再 `markUsed`

---

## 8. 反馈模块列名不一致（Feedback）

**表 `feedbacks` 的真实列**：`id, user_id, content, email, status, created_at`（没有 `contact` 列）

你的 `Feedback` 实体和 `FeedbackRequest` 用了 `contact`，插入会报"列不存在"。请改：
- `Feedback.java`：`contact` 字段改成 `email`，并加 `@TableField("email")`（或直接映射到 `email`）
- `FeedbackRequest.java`：`contact` → `email`
- `FeedbackServiceImpl.submit`：`feedback.setEmail(request.getEmail())`

---

## 9. 接口使用说明（给前端/组长确认，不是 bug）

- 收藏：`POST /api/favorites` 的 `targetType` 只接受 `hotel` 或 `camp`（对应商品 category），传别的会 400
- 评价：`POST /api/reviews` 需要 `orderId` 是真实订单；列表接口 `GET /api/reviews?targetType=hotel&targetId=xx`
- 你分支里带的 6 个前端页面（MinePage 等）和 C 的 `feature/mine` 内容一致，重复提交没问题，但注意别改坏 C 的文件

---

## 验收清单（改完自测）

1. 重启后端，确认能正常启动
2. 登录后兑换 `WELCOME2024` → 成功；`GET /api/coupons/my` 能看到 1 张可用券
3. 兑换 `SUMMER2024` → `GET /api/points/balance` 积分 +200
4. 收藏：`POST /api/favorites {"targetType":"camp","targetId":1}` → `GET /api/favorites` 有记录
5. 出行人：新增后 `GET /api/travelers` 能列出
6. 评价：先下一单并支付（F 模块），再 `POST /api/reviews`，`GET /api/reviews` 能查到
7. 反馈：`POST /api/feedbacks` 提交成功