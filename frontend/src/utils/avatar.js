/**
 * 获取用户头像URL
 * @param {string} avatar - 头像路径
 * @returns {string} 完整的头像URL
 */
export function getAvatarUrl(avatar) {
  if (avatar) {
    // 如果是完整URL，直接使用
    if (avatar.startsWith('http')) {
      return avatar
    }
    // 如果是相对路径，添加后端基础URL
    return `http://localhost:3000${avatar}`
  }
  // 默认头像
  return 'https://picsum.photos/seed/user/100/100'
}

/**
 * 处理头像加载错误
 * @param {Event} event - 图片加载错误事件
 */
export function handleAvatarError(event) {
  event.target.src = 'https://picsum.photos/seed/user/100/100'
}

/**
 * 获取用户头像URL（带错误处理）
 * @param {string} avatar - 头像路径
 * @param {string} fallback - 备用头像URL
 * @returns {string} 完整的头像URL
 */
export function getAvatarUrlWithFallback(avatar, fallback = 'https://picsum.photos/seed/user/100/100') {
  if (avatar) {
    // 如果是完整URL，直接使用
    if (avatar.startsWith('http')) {
      return avatar
    }
    // 如果是相对路径，添加后端基础URL
    return `http://localhost:3000${avatar}`
  }
  // 默认头像
  return fallback
} 