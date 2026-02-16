// pages/mine/mine.js
const app = getApp();
const api = require('../../utils/api.js');

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
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    const phoneNumber = wx.getStorageSync('phoneNumber') || '';

    // userInfo可能是字符串或对象，需要判断
    let parsedUserInfo = null;
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
    if (e.detail.errMsg !== 'getPhoneNumber:ok') {
      wx.showToast({
        title: '需要授权获取手机号',
        icon: 'none'
      });
      return;
    }

    const encryptedData = e.detail.encryptedData;
    const iv = e.detail.iv;

    wx.showLoading({ title: '绑定中...' });
    try {
      const res = await api.bindPhone(encryptedData, iv);
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
      }
    } catch (err) {
      wx.hideLoading();
      console.error('绑定手机号失败', err);
      wx.showToast({
        title: '绑定失败',
        icon: 'none'
      });
    }
  },

  // 加载收藏数量
  async loadFavoritesCount() {
    try {
      const res = await api.getFavorites();
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
    const userInfo = e.detail.userInfo;
    if (!userInfo) {
      // 用户拒绝授权
      wx.showToast({
        title: '需要授权才能登录',
        icon: 'none'
      });
      return;
    }

    // 获取code用于登录
    const loginResult = await new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          resolve(res.code);
        },
        fail: reject
      });
    });

    // 调用登录API
    wx.showLoading({ title: '登录中...' });
    try {
      const res = await api.login(loginResult, userInfo);
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
    const { feedbackContent, feedbackContact } = this.data;

    if (!feedbackContent.trim()) {
      wx.showToast({
        title: '请输入反馈内容',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    try {
      const res = await api.submitFeedback(feedbackContent, feedbackContact);
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
