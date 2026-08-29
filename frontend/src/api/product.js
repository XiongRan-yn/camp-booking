import request from './request';

/**
 * 商品列表
 * @param {Object} params - { category, subCategory, keyword, page, pageSize }
 */
export function getProductList(params = {}) {
    return request.get('/products', { params });
}

/**
 * 商品详情
 * @param {number|string} id - 商品ID
 */
export function getProductDetail(id) {
    return request.get(`/products/${id}`);
}
