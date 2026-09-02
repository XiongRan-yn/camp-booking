import request from "./request";

/**
 * 登录
 * @param {Object} data - { username, password }
 */
export function loginApi(data) {
  return request.post("/auth/login", data);
}

/**
 * 注册
 * @param {Object} data - { username, password, nickname }
 */
export function registerApi(data) {
  return request.post("/auth/register", data);
}