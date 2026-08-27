import request from './request';

/**
 * 可领券列表
 * @param {Object} params - { page, pageSize }
 */
export function getCouponList(params = {}) {
    return request.get('/coupons', { params });
}

/**
 * 领取优惠券
 * @param {number|string} id - 优惠券模板ID
 */
export function receiveCoupon(id) {
    return request.post(`/coupons/${id}/receive`);
}

/**
 * 我的卡券包
 * @param {Object} params - { type, status }
 */
export function getMyCoupons(params = {}) {
    return request.get('/coupons/mine', { params });
}

/**
 * 下单可用券
 * @param {number} amount - 订单金额（用于过滤门槛）
 */
export function getUsableCoupons(amount) {
    return request.get('/coupons/usable', { params: { amount } });
}
