/**
 * 格式化金额显示
 * @param {number} amount - 金额
 * @returns {string} 格式化后的金额
 */
function formatAmount(amount) {
  if (!amount) return '0';
  if (amount >= 10000) {
    return (amount / 10000).toFixed(1) + '万';
  }
  return amount.toString();
}

/**
 * 格式化利率显示
 * @param {number} rate - 利率
 * @returns {string} 格式化后的利率
 */
function formatRate(rate) {
  if (!rate) return '0';
  return rate.toFixed(2) + '%';
}

/**
 * 手机号脱敏
 * @param {string} phone - 手机号
 * @returns {string} 脱敏后的手机号
 */
function maskPhone(phone) {
  if (!phone || phone.length < 11) return phone;
  return phone.substring(0, 3) + '****' + phone.substring(7);
}

/**
 * 格式化日期
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的日期
 */
function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

/**
 * 格式化日期时间
 * @param {string} dateStr - 日期字符串
 * @returns {string} 格式化后的日期时间
 */
function formatDateTime(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hour = String(date.getHours()).padStart(2, '0');
  const minute = String(date.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hour}:${minute}`;
}

/**
 * 相对时间显示
 * @param {string} dateStr - 日期字符串
 * @returns {string} 相对时间
 */
function relativeTime(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now - date;

  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;

  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前';
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前';
  } else if (diff < 7 * day) {
    return Math.floor(diff / day) + '天前';
  } else {
    return formatDate(dateStr);
  }
}

module.exports = {
  formatAmount,
  formatRate,
  maskPhone,
  formatDate,
  formatDateTime,
  relativeTime
};
