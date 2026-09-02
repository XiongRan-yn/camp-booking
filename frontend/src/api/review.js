import request from './request';

/**
 * 提交评价（仅已支付/已完成订单可评价）
 * @param {Object} data - { type, productId, orderId, rating, content, images }
 *   type: 'hotel'（民宿）| 'dynamic'（研学营动态）
 */
export function submitReview(data) {
    return request.post('/reviews', data);
}

/**
 * 查询评价列表
 * 传 targetType+targetId 按对象查；不传则查当前用户的评价
 * @param {Object} params - { targetType, targetId, type }
 */
export function getReviewList(params = {}) {
    return request.get('/reviews', { params });
}
