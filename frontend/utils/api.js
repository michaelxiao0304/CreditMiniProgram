/**
 * API接口封装
 */
var app = getApp();

/**
 * 微信登录
 */
function login(code, userInfo) {
  return app.request({
    url: '/api/auth/login',
    method: 'POST',
    data: {
      code: code,
      userInfo: JSON.stringify(userInfo)
    }
  });
}

/**
 * 获取产品列表
 */
function getProducts(params) {
  return app.request({
    url: '/api/products',
    method: 'GET',
    data: params
  });
}

/**
 * 获取产品详情
 */
function getProductDetail(id) {
  return app.request({
    url: '/api/products/' + id,
    method: 'GET'
  });
}

/**
 * 获取银行列表
 */
function getBanks() {
  return app.request({
    url: '/api/banks',
    method: 'GET'
  });
}

/**
 * 获取顾问信息
 */
function getConsultant(productId) {
  return app.request({
    url: '/api/consultant/' + productId,
    method: 'GET'
  });
}

/**
 * 获取用户收藏列表
 */
function getFavorites() {
  return app.request({
    url: '/api/favorites',
    method: 'GET'
  });
}

/**
 * 添加收藏
 */
function addFavorite(productId) {
  return app.request({
    url: '/api/favorites',
    method: 'POST',
    data: { productId: productId }
  });
}

/**
 * 取消收藏
 */
function removeFavorite(productId) {
  return app.request({
    url: '/api/favorites/' + productId,
    method: 'DELETE'
  });
}

/**
 * 检查是否收藏
 */
function checkFavorite(productId) {
  return app.request({
    url: '/api/favorites/check/' + productId,
    method: 'GET'
  });
}

/**
 * 获取浏览历史
 */
function getHistory() {
  return app.request({
    url: '/api/history',
    method: 'GET'
  });
}

/**
 * 添加浏览历史
 */
function addHistory(productId) {
  return app.request({
    url: '/api/history',
    method: 'POST',
    data: { productId: productId }
  });
}

/**
 * 清空浏览历史
 */
function clearHistory() {
  return app.request({
    url: '/api/history',
    method: 'DELETE'
  });
}

/**
 * 提交反馈
 */
function submitFeedback(content, contact) {
  return app.request({
    url: '/api/feedback',
    method: 'POST',
    data: { content: content, contact: contact }
  });
}

/**
 * 绑定手机号
 */
function bindPhone(encryptedData, iv) {
  return app.request({
    url: '/api/auth/bindPhone',
    method: 'POST',
    data: {
      encryptedData: encryptedData,
      iv: iv
    }
  });
}

module.exports = {
  login: login,
  getProducts: getProducts,
  getProductDetail: getProductDetail,
  getBanks: getBanks,
  getConsultant: getConsultant,
  getFavorites: getFavorites,
  addFavorite: addFavorite,
  removeFavorite: removeFavorite,
  checkFavorite: checkFavorite,
  getHistory: getHistory,
  addHistory: addHistory,
  clearHistory: clearHistory,
  submitFeedback: submitFeedback,
  bindPhone: bindPhone
};
