// pages/history/history.js
var api = require('../../utils/api.js');
var util = require('../../utils/util.js');
var app = getApp();

Page({
  data: {
    history: [],
    loading: false
  },

  onLoad() {
    // 检查登录状态
    if (!app.globalData.token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      wx.navigateBack();
      return;
    }
    this.loadHistory();
  },

  async loadHistory() {
    this.setData({ loading: true });

    try {
      var res = await api.getHistory();
      if (res.code === 200) {
        // 格式化时间
        var history = (res.data || []).map(function(item) {
          var obj = Object.assign({}, item);
          obj.bankLogoUrl = app.getImageUrl(item.bankLogoUrl);
          obj.createdAt = util.relativeTime(item.createdAt);
          return obj;
        });
        this.setData({ history });
      }
    } catch (err) {
      console.error('加载历史失败', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  onClearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空所有浏览历史吗？',
      success: async function(res) {
        if (res.confirm) {
          try {
            await api.clearHistory();
            this.setData({ history: [] });
            wx.showToast({
              title: '已清空',
              icon: 'success'
            });
          } catch (err) {
            console.error('清空历史失败', err);
            wx.showToast({
              title: '操作失败',
              icon: 'none'
            });
          }
        }
      }
    });
  }
});
