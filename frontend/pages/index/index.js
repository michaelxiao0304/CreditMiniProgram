// pages/index/index.js
var api = require('../../utils/api.js');
var util = require('../../utils/util.js');
var app = getApp();

Page({
  data: {
    // 筛选
    banks: [],
    currentBankId: 0,

    // 产品列表
    products: [],
    page: 1,
    size: 20,
    loading: false,
    hasMore: true,

    // 展开详情
    expandedId: null,

    // 顾问弹窗
    showContactModal: false,
    consultant: null,
    currentProductId: null
  },

  onLoad() {
    this.loadBanks();
    this.loadProducts();
  },

  onShow() {
    // 检查收藏状态
    this.checkFavorites();
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({
      page: 1,
      products: []
    });
    Promise.all([
      this.loadBanks(),
      this.loadProducts()
    ]).then(function() {
      wx.stopPullDownRefresh();
    });
  },

  // 上拉加载
  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        page: this.data.page + 1
      });
      this.loadProducts(true);
    }
  },

  // 加载银行列表
  async loadBanks() {
    try {
      var res = await api.getBanks();
      if (res.code === 200) {
        this.setData({
          banks: res.data || []
        });
      }
    } catch (err) {
      console.error('加载银行列表失败', err);
    }
  },

  // 加载产品列表
  async loadProducts(loadMore = false) {
    if (this.data.loading) return;

    this.setData({ loading: true });

    try {
      var params = {
        page: this.data.page,
        size: this.data.size
      };
      // 只传递有效的 bankId
      if (this.data.currentBankId) {
        params.bankId = this.data.currentBankId;
      }

      var res = await api.getProducts(params);

      if (res.code === 200) {
        var newProducts = (res.data.records || []).map(function(item) {
          var obj = Object.assign({}, item);
          obj.bankLogoUrl = app.getImageUrl(item.bankLogoUrl);
          obj.tags = item.tags ? item.tags.split(',') : [];
          obj.isFavorited = false;
          return obj;
        });

        this.setData({
          products: loadMore ? this.data.products.concat(newProducts) : newProducts,
          hasMore: res.data.records && res.data.records.length >= this.data.size
        });
      }
    } catch (err) {
      console.error('加载产品列表失败', err);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 刷新产品列表
  onRefreshTap() {
    if (this.data.loading) return;

    wx.showLoading({
      title: '刷新中...',
      mask: true
    });

    this.setData({
      page: 1,
      products: []
    });
    Promise.all([
      this.loadBanks(),
      this.loadProducts()
    ]).then(function() {
      wx.hideLoading();
      wx.showToast({
        title: '刷新成功',
        icon: 'success',
        duration: 1500
      });
    }).catch(function() {
      wx.hideLoading();
    });
  },

  // 切换银行筛选
  onBankChange(e) {
    var bankId = parseInt(e.currentTarget.dataset.id);
    this.setData({
      currentBankId: bankId,
      page: 1,
      products: []
    });
    this.loadProducts();
  },

  // 产品点击
  onProductTap(e) {
    var productId = e.currentTarget.dataset.id;
    // 添加浏览历史
    this.addHistory(productId);
  },

  // 展开/收起详情
  onExpandTap(e) {
    var productId = e.currentTarget.dataset.id;
    this.setData({
      expandedId: this.data.expandedId === productId ? null : productId
    });
  },

  // 收藏点击
  async onFavoriteTap(e) {
    // 检查登录状态
    if (!app.globalData.token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    var productId = e.currentTarget.dataset.id;
    var index = e.currentTarget.dataset.index;
    var product = this.data.products[index];

    try {
      if (product.isFavorited) {
        await api.removeFavorite(productId);
        wx.showToast({
          title: '取消收藏',
          icon: 'success'
        });
      } else {
        await api.addFavorite(productId);
        wx.showToast({
          title: '收藏成功',
          icon: 'success'
        });
      }

      // 更新状态
      var products = this.data.products.slice();
      products[index].isFavorited = !products[index].isFavorited;
      this.setData({ products });
    } catch (err) {
      console.error('收藏操作失败', err);
    }
  },

  // 检查收藏状态
  async checkFavorites() {
    // 未登录时不检查收藏状态
    if (!app.globalData.token) {
      return;
    }

    var products = this.data.products;
    if (!products.length) return;

    try {
      var res = await api.getFavorites();
      if (res.code === 200) {
        // 使用普通对象代替 Set
        var favoriteIds = {};
        (res.data || []).forEach(function(item) {
          favoriteIds[item.productId] = true;
        });
        var updatedProducts = products.map(function(item) {
          var obj = Object.assign({}, item);
          obj.isFavorited = !!favoriteIds[item.id];
          return obj;
        });
        this.setData({ products: updatedProducts });
      }
    } catch (err) {
      // 未登录或接口错误时不显示错误
      console.log('检查收藏状态跳过');
    }
  },

  // 联系顾问
  async onContactTap(e) {
    var productId = e.currentTarget.dataset.id;
    this.setData({ currentProductId: productId });

    wx.showLoading({ title: '加载中...' });

    try {
      var res = await api.getConsultant(productId);
      wx.hideLoading();

      if (res.code === 200 && res.data) {
        this.setData({
          showContactModal: true,
          consultant: res.data
        });
      } else {
        wx.showToast({
          title: '暂无顾问信息',
          icon: 'none'
        });
      }
    } catch (err) {
      wx.hideLoading();
      console.error('获取顾问信息失败', err);
      wx.showToast({
        title: '获取顾问失败',
        icon: 'none'
      });
    }
  },

  // 关闭联系弹窗
  onCloseContactModal() {
    this.setData({
      showContactModal: false,
      consultant: null
    });
  },

  // 一键拨号
  onCallConsultant() {
    var phone = this.data.consultant.phoneRaw;
    if (phone) {
      wx.makePhoneCall({
        phoneNumber: phone
      });
    }
  },

  // 添加浏览历史
  async addHistory(productId) {
    try {
      await api.addHistory(productId);
    } catch (err) {
      console.error('添加浏览记录失败', err);
    }
  }
});
