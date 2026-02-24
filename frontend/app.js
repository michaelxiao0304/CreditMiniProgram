// app.js
App({
  globalData: {
    baseUrl: 'https://1ammoss.com/credit',
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
    var isRetry = options._retry || false;

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
            // 未授权，尝试自动登录
            if (!isRetry) {
              app.autoLogin(function(success) {
                if (success) {
                  // 登录成功，重试请求
                  options._retry = true;
                  app.request(options).then(resolve).catch(reject);
                } else {
                  // 登录失败，清除登录状态
                  app.clearLogin();
                  wx.showToast({
                    title: '请先登录',
                    icon: 'none'
                  });
                  reject(res.data);
                }
              });
            } else {
              // 重试仍然失败
              app.clearLogin();
              wx.showToast({
                title: '请先登录',
                icon: 'none'
              });
              reject(res.data);
            }
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
  },

  // 自动登录
  autoLogin: function(callback) {
    var app = this;
    var userInfo = wx.getStorageSync('userInfo');

    if (!userInfo) {
      callback(false);
      return;
    }

    // 获取微信登录 code
    wx.login({
      success: function(loginRes) {
        wx.request({
          url: app.globalData.baseUrl + '/api/auth/login',
          method: 'POST',
          header: {
            'Content-Type': 'application/json'
          },
          data: {
            code: loginRes.code,
            userInfo: JSON.stringify(userInfo)
          },
          success: function(res) {
            if (res.data.code === 200) {
              app.setToken(res.data.data.token);
              callback(true);
            } else {
              callback(false);
            }
          },
          fail: function() {
            callback(false);
          }
        });
      },
      fail: function() {
        callback(false);
      }
    });
  }
})
