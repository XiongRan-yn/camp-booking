import request from './request';

/**
 * 积分余额
 */
export function getPointsBalance() {
    return request.get('/points/balance');
}

/**
 * 积分流水
 * @param {Object} params - { page, pageSize }
 */
export function getPointsRecords(params = {}) {
    return request.get('/points/records', { params });
}
