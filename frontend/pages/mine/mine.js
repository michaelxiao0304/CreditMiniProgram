// pages/mine/mine.js
var app = getApp();
var api = require('../../utils/api.js');

Page({
  data: {
    // 登录状态
    isLoggedIn: false,
    userInfo: null,
    phoneNumber: '',

    // 数据统计
    favoritesCount: 0,

    // 弹窗状态
    showFeedbackModal: false,
    showAboutModal: false,

    // 反馈内容
    feedbackContent: '',
    feedbackContact: ''
  },

  onLoad() {
    this.checkLoginStatus();
  },

  onShow() {
    if (this.data.isLoggedIn) {
      this.loadFavoritesCount();
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    var token = wx.getStorageSync('token');
    var userInfo = wx.getStorageSync('userInfo');
    var phoneNumber = wx.getStorageSync('phoneNumber') || '';

    // userInfo可能是字符串或对象，需要判断
    var parsedUserInfo = null;
    if (userInfo) {
      try {
        parsedUserInfo = typeof userInfo === 'string' ? JSON.parse(userInfo) : userInfo;
      } catch (e) {
        parsedUserInfo = null;
      }
    }

    this.setData({
      isLoggedIn: !!token,
      userInfo: parsedUserInfo,
      phoneNumber: phoneNumber
    });
  },

  // 获取手机号
  async onGetPhoneNumber(e) {
    // 打印详细错误信息，方便调试
    console.log('getPhoneNumber result:', e.detail);

    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      var errorMsg = '需要授权获取手机号';
      // 根据不同错误给出更明确的提示
      if (e.detail.errMsg && e.detail.errMsg.indexOf('cancel') !== -1) {
        errorMsg = '已取消授权';
      } else if (e.detail.errMsg && e.detail.errMsg.indexOf('deny') !== -1) {
        errorMsg = '已拒绝授权获取手机号';
      }
      wx.showToast({
        title: errorMsg,
        icon: 'none',
        duration: 2000
      });
      return;
    }

    var encryptedData = e.detail.encryptedData;
    var iv = e.detail.iv;

    if (!encryptedData) {
      wx.showToast({
        title: '获取数据失败，请重试',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '绑定中...' });
    try {
      var res = await api.bindPhone(encryptedData, iv);
      wx.hideLoading();

      if (res.code === 200 && res.data.phoneNumber) {
        wx.setStorageSync('phoneNumber', res.data.phoneNumber);
        this.setData({
          phoneNumber: res.data.phoneNumber
        });
        wx.showToast({
          title: '手机号绑定成功',
          icon: 'success'
        });
      } else {
        wx.showToast({
          title: res.msg || '绑定失败',
          icon: 'none'
        });
      }
    } catch (err) {
      wx.hideLoading();
      console.error('绑定手机号失败', err);
      wx.showToast({
        title: '绑定失败，请重试',
        icon: 'none'
      });
    }
  },

  // 选择头像
  onChooseAvatar(e) {
    var avatarUrl = e.detail.avatarUrl;
    if (!avatarUrl) {
      return;
    }

    var that = this;
    wx.showLoading({ title: '保存中...' });

    // 更新本地数据
    var userInfo = this.data.userInfo || {};
    userInfo.avatarUrl = avatarUrl;
    wx.setStorageSync('userInfo', userInfo);

    this.setData({
      userInfo: userInfo
    });

    wx.hideLoading();
    wx.showToast({
      title: '头像已更新',
      icon: 'success'
    });
  },

  // 编辑昵称
  onEditNickname() {
    var that = this;
    var currentNickname = this.data.userInfo ? (this.data.userInfo.nickName || this.data.userInfo.nickname || '') : '';

    wx.showModal({
      title: '修改昵称',
      placeholderText: '请输入昵称',
      content: currentNickname,
      editable: true,
      success: function(res) {
        if (res.confirm && res.content && res.content.trim()) {
          var userInfo = that.data.userInfo || {};
          userInfo.nickname = res.content.trim();
          wx.setStorageSync('userInfo', userInfo);

          that.setData({
            userInfo: userInfo
          });

          wx.showToast({
            title: '昵称已更新',
            icon: 'success'
          });
        }
      }
    });
  },

  // 加载收藏数量
  async loadFavoritesCount() {
    try {
      var res = await api.getFavorites();
      if (res.code === 200) {
        this.setData({
          favoritesCount: (res.data || []).length
        });
      }
    } catch (err) {
      console.error('获取收藏数量失败', err);
    }
  },

  // 微信登录 - 获取用户信息 (使用button open-type="getUserInfo")
  async onGetUserInfo(e) {
    var userInfo = e.detail.userInfo;
    if (!userInfo) {
      // 用户拒绝授权
      wx.showToast({
        title: '需要授权才能登录',
        icon: 'none'
      });
      return;
    }

    // 获取code用于登录
    var loginResult = await new Promise(function(resolve, reject) {
      wx.login({
        success: function(res) {
          resolve(res.code);
        },
        fail: reject
      });
    });

    // 调用登录API
    wx.showLoading({ title: '登录中...' });
    try {
      var res = await api.login(loginResult, userInfo);
      wx.hideLoading();

      if (res.code === 200) {
        // 保存token和用户信息
        app.setToken(res.data.token);
        app.setUserInfo(userInfo);

        this.setData({
          isLoggedIn: true,
          userInfo: userInfo
        });

        wx.showToast({
          title: '登录成功',
          icon: 'success'
        });

        // 刷新数据
        this.loadFavoritesCount();
      }
    } catch (err) {
      wx.hideLoading();
      console.error('登录失败', err);
      wx.showToast({
        title: '登录失败',
        icon: 'none'
      });
    }
  },

  // 兼容旧的登录方法
  async onLogin() {
    this.onGetUserInfo({ detail: { userInfo: null } });
  },

  // 退出登录
  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearLogin();
          this.setData({
            isLoggedIn: false,
            userInfo: null,
            phoneNumber: null,
            favoritesCount: 0
          });
          wx.showToast({
            title: '已退出',
            icon: 'success'
          });
        }
      }
    });
  },

  // 跳转收藏页面
  onNavigateToFavorites() {
    if (!this.checkAuth()) return;
    wx.navigateTo({
      url: '/pages/favorites/favorites'
    });
  },

  // 跳转历史页面
  onNavigateToHistory() {
    if (!this.checkAuth()) return;
    wx.navigateTo({
      url: '/pages/history/history'
    });
  },

  // 检查授权
  checkAuth() {
    if (!this.data.isLoggedIn) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return false;
    }
    return true;
  },

  // 显示反馈弹窗
  onShowFeedback() {
    if (!this.checkAuth()) return;
    this.setData({
      showFeedbackModal: true,
      feedbackContent: '',
      feedbackContact: ''
    });
  },

  // 关闭反馈弹窗
  onCloseFeedbackModal() {
    this.setData({
      showFeedbackModal: false
    });
  },

  // 反馈输入
  onFeedbackInput(e) {
    this.setData({
      feedbackContent: e.detail.value
    });
  },

  // 联系方式输入
  onContactInput(e) {
    this.setData({
      feedbackContact: e.detail.value
    });
  },

  // 提交反馈
  async onSubmitFeedback() {
    var { feedbackContent, feedbackContact } = this.data;

    if (!feedbackContent.trim()) {
      wx.showToast({
        title: '请输入反馈内容',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    try {
      var res = await api.submitFeedback(feedbackContent, feedbackContact);
      wx.hideLoading();

      if (res.code === 200) {
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        });
        this.onCloseFeedbackModal();
      }
    } catch (err) {
      wx.hideLoading();
      console.error('提交反馈失败', err);
      wx.showToast({
        title: '提交失败',
        icon: 'none'
      });
    }
  },

  // 显示关于我们
  onShowAbout() {
    this.setData({
      showAboutModal: true
    });
  },

  // 关闭关于我们
  onCloseAboutModal() {
    this.setData({
      showAboutModal: false
    });
  }
});
