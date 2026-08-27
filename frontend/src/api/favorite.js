import request from './request';

/**
 * 收藏列表
 * @param {Object} params - { type, page, pageSize }
 */
export function getFavoriteList(params = {}) {
    return request.get('/favorites', { params });
}

/**
 * 添加收藏
 * @param {Object} data - { targetId, targetType }
 */
export function addFavorite(data) {
    return request.post('/favorites', data);
}

/**
 * 取消收藏
 * @param {number|string} id - 收藏记录ID
 */
export function removeFavorite(id) {
    return request.delete(`/favorites/${id}`);
}
