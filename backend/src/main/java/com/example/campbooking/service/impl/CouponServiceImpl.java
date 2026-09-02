package com.example.campbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.campbooking.common.BusinessException;
import com.example.campbooking.entity.Coupon;
import com.example.campbooking.entity.UserCoupon;
import com.example.campbooking.mapper.CouponMapper;
import com.example.campbooking.mapper.UserCouponMapper;
import com.example.campbooking.service.CouponService;
import com.example.campbooking.vo.CouponVO;
import com.example.campbooking.vo.UserCouponVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    public CouponServiceImpl(CouponMapper couponMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
    }

    @Override
    public List<CouponVO> listAvailable(Long userId) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, "on");
        wrapper.in(Coupon::getType, "cash", "discount");
        wrapper.le(Coupon::getStartTime, LocalDateTime.now());
        wrapper.ge(Coupon::getEndTime, LocalDateTime.now());
        wrapper.orderByAsc(Coupon::getId);
        List<Coupon> coupons = couponMapper.selectList(wrapper);

        List<CouponVO> result = new ArrayList<>();
        for (Coupon coupon : coupons) {
            CouponVO vo = new CouponVO();
            vo.setId(coupon.getId());
            vo.setTitle(coupon.getTitle());
            vo.setType(coupon.getType());
            vo.setDiscountValue(coupon.getDiscountValue());
            vo.setMinAmount(coupon.getMinAmount());
            vo.setTotalCount(coupon.getTotalCount());
            vo.setReceivedCount(coupon.getReceivedCount());
            vo.setRules(coupon.getRules());
            vo.setValidDays(coupon.getValidDays());
            vo.setStartTime(coupon.getStartTime());
            vo.setEndTime(coupon.getEndTime());
            vo.setHasReceived(userId != null && hasReceived(userId, coupon.getId()));
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional
    public UserCouponVO receive(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || !"on".equals(coupon.getStatus())) {
            throw new BusinessException(404, "优惠券不存在或已下架");
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartTime() != null && now.isBefore(coupon.getStartTime())) {
            throw new BusinessException(400, "优惠券尚未开始发放");
        }
        if (coupon.getEndTime() != null && now.isAfter(coupon.getEndTime())) {
            throw new BusinessException(400, "优惠券已结束发放");
        }
        if (hasReceived(userId, couponId)) {
            throw new BusinessException(400, "您已领取过该优惠券");
        }
        int received = coupon.getReceivedCount() != null ? coupon.getReceivedCount() : 0;
        int total = coupon.getTotalCount() != null ? coupon.getTotalCount() : 0;
        if (received >= total) {
            throw new BusinessException(400, "优惠券已被领完");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponTitle(coupon.getTitle());
        userCoupon.setStatus("usable");
        userCoupon.setReceivedAt(now);
        userCoupon.setExpireAt(now.plusDays(coupon.getValidDays() != null ? coupon.getValidDays() : 7));
        userCouponMapper.insert(userCoupon);

        coupon.setReceivedCount(received + 1);
        couponMapper.updateById(coupon);

        return toUserCouponVO(userCoupon, coupon);
    }

    @Override
    public List<UserCouponVO> myCoupons(Long userId, String status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getReceivedAt);
        List<UserCoupon> list = userCouponMapper.selectList(wrapper);

        List<UserCouponVO> result = new ArrayList<>();
        for (UserCoupon uc : list) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            result.add(toUserCouponVO(uc, coupon));
        }
        return result;
    }

    @Override
    public List<UserCouponVO> availableForOrder(Long userId, BigDecimal totalPrice) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.eq(UserCoupon::getStatus, "usable");
        wrapper.gt(UserCoupon::getExpireAt, LocalDateTime.now());
        List<UserCoupon> list = userCouponMapper.selectList(wrapper);

        List<UserCouponVO> result = new ArrayList<>();
        for (UserCoupon uc : list) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon == null) {
                continue;
            }
            if (!"cash".equals(coupon.getType()) && !"discount".equals(coupon.getType())) {
                continue;
            }
            if (totalPrice != null && coupon.getMinAmount() != null
                    && totalPrice.compareTo(coupon.getMinAmount()) < 0) {
                continue;
            }
            result.add(toUserCouponVO(uc, coupon));
        }
        return result;
    }

    private boolean hasReceived(Long userId, Long couponId) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.eq(UserCoupon::getCouponId, couponId);
        return userCouponMapper.selectCount(wrapper) > 0;
    }

    private UserCouponVO toUserCouponVO(UserCoupon uc, Coupon coupon) {
        UserCouponVO vo = new UserCouponVO();
        vo.setId(uc.getId());
        vo.setCouponId(uc.getCouponId());
        vo.setCouponTitle(uc.getCouponTitle());
        if (coupon != null) {
            vo.setType(coupon.getType());
            vo.setDiscountValue(coupon.getDiscountValue());
            vo.setMinAmount(coupon.getMinAmount());
        }
        vo.setStatus(uc.getStatus());
        vo.setReceivedAt(uc.getReceivedAt());
        vo.setUsedAt(uc.getUsedAt());
        vo.setExpireAt(uc.getExpireAt());
        return vo;
    }
}