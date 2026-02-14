// app.js
App({
  globalData: {
    baseUrl: 'http://localhost:8080',
    token: '',
    userInfo: null
  },

  onLaunch() {
    // 检查登录状态
    this.checkLogin();
  },

  checkLogin() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      const userInfo = wx.getStorageSync('userInfo');
      if (userInfo) {
        this.globalData.userInfo = userInfo;
      }
    }
  },

  setToken(token) {
    this.globalData.token = token;
    wx.setStorageSync('token', token);
  },

  setUserInfo(userInfo) {
    this.globalData.userInfo = userInfo;
    wx.setStorageSync('userInfo', userInfo);
  },

  clearLogin() {
    this.globalData.token = '';
    this.globalData.userInfo = null;
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  },

  // 请求封装
  request(options) {
    const app = this;
    return new Promise((resolve, reject) => {
      const header = options.header || {};

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
        success(res) {
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
        fail(err) {
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
