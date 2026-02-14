// pages/history/history.js
const api = require('../../utils/api.js');
const util = require('../../utils/util.js');
const app = getApp();

Page({
  data: {
    history: [],
    loading: false
  },

  onLoad() {
    this.loadHistory();
  },

  async loadHistory() {
    this.setData({ loading: true });

    try {
      const res = await api.getHistory();
      if (res.code === 200) {
        // 格式化时间
        const history = (res.data || []).map(item => ({
          ...item,
          bankLogoUrl: app.getImageUrl(item.bankLogoUrl),
          createdAt: util.relativeTime(item.createdAt)
        }));
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
      success: async (res) => {
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
