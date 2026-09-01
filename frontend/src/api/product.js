import request from "./request";

// 商品列表（公开接口，无需登录）
export function getProducts(params = {}) {
  return request.get("/products", { params });
}

// 商品详情
export function getProductDetail(id) {
  return request.get(`/products/${id}`);
}
