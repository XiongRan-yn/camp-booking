import request from './request';

/**
 * 计算订单价格（选规格/数量/优惠券后实时计算）
 * @param {Object} data - { productId, specId, quantity, couponId }
 */
export function calculateOrder(data) {
    return request.post('/orders/calculate', data);
}

/**
 * 创建订单
 * @param {Object} data - { productId, specId, quantity, couponId, pointsUsed, contactName, contactPhone, remark }
 */
export function createOrder(data) {
    return request.post('/orders', data);
}

/**
 * 模拟支付
 * @param {number|string} id - 订单ID
 */
export function payOrder(id) {
    return request.put(`/orders/${id}/pay`);
}

/**
 * 订单列表
 * @param {Object} params - { status, keyword, page, pageSize }
 */
export function getOrderList(params = {}) {
    return request.get('/orders', { params });
}

/**
 * 订单详情
 * @param {number|string} id - 订单ID
 */
export function getOrderDetail(id) {
    return request.get(`/orders/${id}`);
}

/**
 * 取消订单
 * @param {number|string} id - 订单ID
 */
export function cancelOrder(id) {
    return request.put(`/orders/${id}/cancel`);
}
