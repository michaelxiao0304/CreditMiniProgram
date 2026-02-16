// app.js
App({
  globalData: {
    baseUrl: 'https://credit-miniapp.loca.lt',
    token: '',
    userInfo: null
  },

  // 获取完整图片URL
  getImageUrl: function(path) {
    if (!path) return '';
    if (path.indexOf('http') === 0) return path;
    return this.globalData.baseUrl + path;
  },

  onLaunch: function() {
    // 检查登录状态
    this.checkLogin();
  },

  checkLogin: function() {
    var token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      var userInfo = wx.getStorageSync('userInfo');
      if (userInfo) {
        this.globalData.userInfo = userInfo;
      }
    }
  },

  setToken: function(token) {
    this.globalData.token = token;
    wx.setStorageSync('token', token);
  },

  setUserInfo: function(userInfo) {
    this.globalData.userInfo = userInfo;
    wx.setStorageSync('userInfo', userInfo);
  },

  clearLogin: function() {
    this.globalData.token = '';
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  // 请求封装
  request: function(options) {
    var app = this;
    return new Promise(function(resolve, reject) {
      var header = options.header || {};

      // 添加token
      if (app.globalData.token) {
        header['Authorization'] = 'Bearer ' + app.globalData.token;
      }

      // 添加content-type
      if (!header['Content-Type']) {
        header['Content-Type'] = 'application/json';
      }

      wx.request({
        url: app.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: header,
        success: function(res) {
          if (res.data.code === 200) {
            resolve(res.data);
          } else if (res.data.code === 401) {
            // 未授权，清除登录状态
            app.clearLogin();
            wx.showToast({
              title: '请先登录',
              icon: 'none'
            });
            reject(res.data);
          } else {
            wx.showToast({
              title: res.data.msg || '请求失败',
              icon: 'none'
            });
            reject(res.data);
          }
        },
        fail: function(err) {
          wx.showToast({
            title: '网络请求失败',
            icon: 'none'
          });
          reject(err);
        }
      });
    });
  }
})
