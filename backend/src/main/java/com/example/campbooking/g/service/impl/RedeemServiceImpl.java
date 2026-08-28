package com.example.campbooking.g.service.impl;

import com.example.campbooking.common.BusinessException;
import com.example.campbooking.g.dto.RedeemRequest;
import com.example.campbooking.g.entity.Coupon;
import com.example.campbooking.g.entity.RedemptionCode;
import com.example.campbooking.g.entity.UserCoupon;
import com.example.campbooking.g.mapper.RedeemMapper;
import com.example.campbooking.g.mapper.RedemptionCodeMapper;
import com.example.campbooking.g.security.SecurityUtils;
import com.example.campbooking.g.service.RedeemService;
import com.example.campbooking.g.vo.RedeemResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RedeemServiceImpl implements RedeemService {

    @Autowired
    private RedemptionCodeMapper redemptionCodeMapper;

    @Autowired
    private RedeemMapper redeemMapper;

    // ↓↓↓ F 模块基础类（G 不改动，仅调用）。方法名按 F 真实签名调整 ↓↓↓
    @Autowired
    private com.example.campbooking.mapper.UserMapper userMapper;             // 假设：addPoints(Long userId, int points)
    @Autowired
    private com.example.campbooking.mapper.PointsRecordMapper pointsRecordMapper; // 假设：insert(PointsRecord)

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
                Long couponId = parseLong(rc.getCodeValue(), "兑换码绑定的优惠券无效");
                Coupon template = redeemMapper.selectCouponById(couponId);
                if (template == null) {
                    throw new BusinessException(404, "关联的优惠券不存在");
                }

                UserCoupon uc = new UserCoupon();
                uc.setUserId(userId);
                uc.setCouponId(template.getId());
                uc.setStatus("usable");   // 必须用 usable，F 模块才识别
                int validDays = template.getValidDays() != null ? template.getValidDays() : 30;
                uc.setExpireAt(now.plusDays(validDays));
                uc.setCreatedAt(now);
                redeemMapper.insertUserCoupon(uc);

                redemptionCodeMapper.markUsed(rc.getId(), userId, now);
                String name = "free_room".equals(rc.getType()) ? "免费房券" : "优惠券";
                return RedeemResultVO.success(code, name, uc.getExpireAt());

            case "points":
                // SUMMER2024：真正 +200 积分，落库到 points_records（调 F 的 Mapper）
                int gained = parsePoints(rc.getCodeValue());   // code_value 可解析则用，否则回退 200
                // TODO(F模块联调)：以下两行按 F 真实方法名 / 实体字段调整
                userMapper.addPoints(userId, gained);                          // ← 假设签名
                pointsRecordMapper.insert(buildPointsRecord(userId, gained, code, now)); // ← 假设签名
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

    private Long parseLong(String s, String err) {
        try {
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            throw new BusinessException(400, err);
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

    // TODO(F模块联调)：按 F 的 PointsRecord 实体字段构造，下面为示例占位
    private Object buildPointsRecord(Long userId, int gained, String code, LocalDateTime now) {
        // 例：com.example.campbooking.entity.PointsRecord r = new PointsRecord();
        //     r.setUserId(userId); r.setPoints(gained); r.setSource(code); r.setCreatedAt(now);
        // 请替换为 F 真实实体与字段后返回该对象
        return null;
    }
}
