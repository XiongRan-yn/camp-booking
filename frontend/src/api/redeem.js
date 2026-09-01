import request from "./request";

/**
 * 兑换码兑换
 * @param {string} code - 兑换码
 */
export function submitRedeemCode(code) {
  return request.post("/redeem", { code });
}