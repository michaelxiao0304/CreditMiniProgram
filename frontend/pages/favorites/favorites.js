// pages/favorites/favorites.js
var api = require('../../utils/api.js');
var app = getApp();

Page({
  data: {
    favorites: [],
    loading: false
  },

  onLoad() {
    this.loadFavorites();
  },

  onShow() {
    // 检查登录状态
    if (!app.globalData.token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      wx.navigateBack();
      return;
    }
    this.loadFavorites();
  },

  async loadFavorites() {
    this.setData({ loading: true });

    try {
      var res = await api.getFavorites();
      if (res.code === 200) {
        var favorites = (res.data || []).map(function(item) {
          var obj = Object.assign({}, item);
          obj.bankLogoUrl = app.getImageUrl(item.bankLogoUrl);
          return obj;
        });
        this.setData({ favorites });
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
    var productId = e.currentTarget.dataset.id;
    var index = e.currentTarget.dataset.index;

    wx.showModal({
      title: '提示',
      content: '确定取消收藏吗？',
      success: async function(res) {
        if (res.confirm) {
          try {
            await api.removeFavorite(productId);

            var favorites = this.data.favorites.slice();
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
