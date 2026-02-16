/**
 * API接口封装
 */
const app = getApp();

/**
 * 微信登录
 */
export function login(code, userInfo) {
  return app.request({
    url: '/api/auth/login',
    method: 'POST',
    data: {
      code,
      userInfo: JSON.stringify(userInfo)
    }
  });
}

/**
 * 获取产品列表
 */
export function getProducts(params) {
  return app.request({
    url: '/api/products',
    method: 'GET',
    data: params
  });
}

/**
 * 获取产品详情
 */
export function getProductDetail(id) {
  return app.request({
    url: `/api/products/${id}`,
    method: 'GET'
  });
}

/**
 * 获取银行列表
 */
export function getBanks() {
  return app.request({
    url: '/api/banks',
    method: 'GET'
  });
}

/**
 * 获取顾问信息
 */
export function getConsultant(productId) {
  return app.request({
    url: `/api/consultant/${productId}`,
    method: 'GET'
  });
}

/**
 * 获取用户收藏列表
 */
export function getFavorites() {
  return app.request({
    url: '/api/favorites',
    method: 'GET'
  });
}

/**
 * 添加收藏
 */
export function addFavorite(productId) {
  return app.request({
    url: '/api/favorites',
    method: 'POST',
    data: { productId }
  });
}

/**
 * 取消收藏
 */
export function removeFavorite(productId) {
  return app.request({
    url: `/api/favorites/${productId}`,
    method: 'DELETE'
  });
}

/**
 * 检查是否收藏
 */
export function checkFavorite(productId) {
  return app.request({
    url: `/api/favorites/check/${productId}`,
    method: 'GET'
  });
}

/**
 * 获取浏览历史
 */
export function getHistory() {
  return app.request({
    url: '/api/history',
    method: 'GET'
  });
}

/**
 * 添加浏览历史
 */
export function addHistory(productId) {
  return app.request({
    url: '/api/history',
    method: 'POST',
    data: { productId }
  });
}

/**
 * 清空浏览历史
 */
export function clearHistory() {
  return app.request({
    url: '/api/history',
    method: 'DELETE'
  });
}

/**
 * 提交反馈
 */
export function submitFeedback(content, contact) {
  return app.request({
    url: '/api/feedback',
    method: 'POST',
    data: { content, contact }
  });
}

/**
 * 绑定手机号
 */
export function bindPhone(encryptedData, iv) {
  return app.request({
    url: '/api/auth/bindPhone',
    method: 'POST',
    data: {
      encryptedData,
      iv
    }
  });
}
