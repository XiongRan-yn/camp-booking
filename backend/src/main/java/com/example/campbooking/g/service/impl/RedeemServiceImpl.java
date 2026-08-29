package com.example.campbooking.g.service.impl;

import com.example.campbooking.common.BusinessException;
import com.example.campbooking.entity.User;
import com.example.campbooking.g.dto.RedeemRequest;
import com.example.campbooking.g.entity.Coupon;
import com.example.campbooking.g.entity.PointsRecord;
import com.example.campbooking.g.entity.RedemptionCode;
import com.example.campbooking.g.entity.UserCoupon;
import com.example.campbooking.g.mapper.PointsRecordMapper;
import com.example.campbooking.g.mapper.RedeemMapper;
import com.example.campbooking.g.mapper.RedemptionCodeMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.RedeemService;
import com.example.campbooking.g.vo.RedeemResultVO;
import com.example.campbooking.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RedeemServiceImpl implements RedeemService {

    @Autowired
    private RedemptionCodeMapper redemptionCodeMapper;

    @Autowired
    private RedeemMapper redeemMapper;

    /** F 模块基础 Mapper（G 仅调用不修改）：用于累加用户积分余额 */
    @Autowired
    private UserMapper userMapper;

    /** G 模块自有：写入 points_records 积分流水 */
    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Override
    public RedeemResultVO redeem(RedeemRequest request) {
        String code = request.getCode();
        RedemptionCode rc = redemptionCodeMapper.selectByCode(code);
        if (rc == null) {
            throw new BusinessException(404, "兑换码不存在");
        }
        if (rc.getIsUsed() != null && rc.getIsUsed() == 1) {
            throw new BusinessException(409, "兑换码已被使用");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        switch (rc.getType()) {
            case "coupon":
            case "free_room":
                // code_value 存的是 coupons.id（WELCOME2024→1, CAMP2024→3, FREEROOM→5）
                Long couponId = parseCouponId(rc.getCodeValue());
                Coupon template = redeemMapper.selectCouponById(couponId);
                if (template == null) {
                    throw new BusinessException(404, "关联的优惠券不存在");
                }

                UserCoupon uc = new UserCoupon();
                uc.setUserId(userId);
                uc.setCouponId(template.getId());
                uc.setStatus("usable");   // F 模块只识别 usable
                int validDays = template.getValidDays() != null ? template.getValidDays() : 30;
                uc.setExpireAt(now.plusDays(validDays));
                uc.setCreatedAt(now);
                redeemMapper.insertUserCoupon(uc);

                redemptionCodeMapper.markUsed(rc.getId(), userId, now);
                String name = "free_room".equals(rc.getType()) ? "免费房券" : "优惠券";
                return RedeemResultVO.success(code, name, uc.getExpireAt());

            case "points":
                // SUMMER2024：code_value='200'，真正 +200 积分
                int gained = parsePoints(rc.getCodeValue());
                // 1) 累加用户积分余额（users.points_balance）
                User user = userMapper.selectById(userId);
                if (user != null) {
                    int bal = user.getPointsBalance() == null ? 0 : user.getPointsBalance();
                    user.setPointsBalance(bal + gained);
                    userMapper.updateById(user);
                }
                // 2) 写入积分流水 points_records
                PointsRecord pr = new PointsRecord();
                pr.setUserId(userId);
                pr.setAmount(gained);
                pr.setType("earn");
                pr.setSource(code);
                pr.setRemark("兑换码积分");
                pr.setCreatedAt(now);
                pointsRecordMapper.insert(pr);

                redemptionCodeMapper.markUsed(rc.getId(), userId, now);
                RedeemResultVO vo = new RedeemResultVO();
                vo.setCode(code);
                vo.setSuccess(true);
                vo.setMessage("积分兑换成功，获得 " + gained + " 积分");
                return vo;

            default:
                // 未知类型：抛 400，绝不消耗兑换码
                throw new BusinessException(400, "不支持的兑换码类型: " + rc.getType());
        }
    }

    private Long parseCouponId(String codeValue) {
        try {
            return Long.parseLong(codeValue.trim());
        } catch (Exception e) {
            throw new BusinessException(400, "兑换码绑定的优惠券无效");
        }
    }

    private int parsePoints(String codeValue) {
        if (codeValue != null) {
            try {
                return Integer.parseInt(codeValue.trim());
            } catch (Exception ignored) {
                // 解析失败回退
            }
        }
        return 200; // SUMMER2024 默认 +200
    }
}
