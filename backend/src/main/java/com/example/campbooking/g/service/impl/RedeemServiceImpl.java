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

    @Override
    public RedeemResultVO redeem(RedeemRequest request) {
        String code = request.getCode();
        RedemptionCode rc = redemptionCodeMapper.selectByCode(code);
        if (rc == null) {
            throw new BusinessException(404, "兑换码不存在");
        }
        if (rc.getUsed() != null && rc.getUsed() == 1) {
            throw new BusinessException(409, "兑换码已被使用");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        switch (rc.getType()) {
            case "coupon":
            case "free_room":
                // 同一逻辑：查/建券模板 → 写入 user_coupons → 核销兑换码
                Coupon template = redeemMapper.selectCouponByCode(code);
                if (template == null) {
                    template = new Coupon();
                    template.setCode(code);
                    template.setType(rc.getType());
                    template.setName("free_room".equals(rc.getType()) ? "免费房券" : "优惠券");
                    template.setValue(rc.getValue() != null ? rc.getValue() : 0);
                    template.setValidDays("free_room".equals(rc.getType()) ? 365 : 30);
                    template.setCreatedAt(now);
                    redeemMapper.insertCoupon(template);
                }

                UserCoupon uc = new UserCoupon();
                uc.setUserId(userId);
                uc.setCouponId(template.getId());
                uc.setCode(code);
                uc.setStatus("unused");
                int validDays = template.getValidDays() != null ? template.getValidDays() : 30;
                uc.setExpireAt(now.plusDays(validDays));
                uc.setCreatedAt(now);
                redeemMapper.insertUserCoupon(uc);

                redemptionCodeMapper.markUsed(rc.getId(), userId, now);
                return RedeemResultVO.success(code, template.getName(), uc.getExpireAt());

            case "points":
                // TODO(G模块联调)：积分需写入 points_records 表并累加到用户积分账户。
                // 当前仅核销兑换码并返回获得积分数；积分落库待接入 PointsRecordMapper 后补齐。
                int gained = rc.getValue() != null ? rc.getValue() : 0;
                redemptionCodeMapper.markUsed(rc.getId(), userId, now);
                RedeemResultVO vo = new RedeemResultVO();
                vo.setCode(code);
                vo.setSuccess(true);
                vo.setMessage("积分兑换成功，获得 " + gained + " 积分（落库待接入 points_records）");
                return vo;

            default:
                // 未知类型：直接抛 400，绝不消耗兑换码
                throw new BusinessException(400, "不支持的兑换码类型: " + rc.getType());
        }
    }
}
