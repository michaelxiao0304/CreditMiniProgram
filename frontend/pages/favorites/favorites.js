// pages/favorites/favorites.js
const api = require('../../utils/api.js');

Page({
  data: {
    favorites: [],
    loading: false
  },

  onLoad() {
    this.loadFavorites();
  },

  onShow() {
    this.loadFavorites();
  },

  async loadFavorites() {
    this.setData({ loading: true });

    try {
      const res = await api.getFavorites();
      if (res.code === 200) {
        this.setData({
          favorites: res.data || []
        });
      }
    } catch (err) {
      console.error('加载收藏失败', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  async onRemoveFavorite(e) {
    const productId = e.currentTarget.dataset.id;
    const index = e.currentTarget.dataset.index;

    wx.showModal({
      title: '提示',
      content: '确定取消收藏吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.removeFavorite(productId);

            const favorites = [...this.data.favorites];
            favorites.splice(index, 1);

            this.setData({ favorites });

            wx.showToast({
              title: '已取消收藏',
              icon: 'success'
            });
          } catch (err) {
            console.error('取消收藏失败', err);
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
