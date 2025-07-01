import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 成功响应直接返回数据
    return response.data
  },
  error => {
    console.error('API Error:', error)
    // 如果有响应数据，返回响应数据，否则返回错误对象
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

// 动态相关API
export const postsAPI = {
  // 获取动态列表（不再需要userId参数，后端从token解析）
  getPosts(params = {}) {
    return api.get('/posts', { params })
  },

  // 获取动态详情
  getPostById(id) {
    return api.get(`/posts/${id}`)
  },

  // 创建动态
  createPost(data) {
    return api.post('/posts', data)
  },

  // 更新动态
  updatePost(id, data) {
    return api.put(`/posts/${id}`, data)
  },

  // 删除动态
  deletePost(id) {
    return api.delete(`/posts/${id}`)
  },

  // 点赞/取消点赞（不再需要userId参数，后端从token解析）
  toggleLike(id, like = true) {
    return api.post(`/posts/${id}/like`, { like })
  },

  // 收藏/取消收藏（不再需要userId参数，后端从token解析）
  toggleCollect(id, collect = true) {
    return api.post(`/posts/${id}/collect`, { collect })
  },

  // 获取推荐动态（不再需要userId参数，后端从token解析）
  getRecommendPosts() {
    return api.get('/posts/recommend')
  },

  // 获取用户动态
  getUserPosts(userId) {
    return api.get(`/posts/user/${userId}`)
  },

  // 获取用户收藏的动态列表
  getUserCollections(userId, page = 1, pageSize = 10, sort = 'latest') {
    return api.get(`/posts/collections/user/${userId}`, {
      params: { page, pageSize, sort }
    })
  },

  // 评论相关API
  getComments(postId, page = 1, pageSize = 20, sort = 'latest') {
    return api.get(`/posts/${postId}/comments`, {
      params: { page, pageSize, sort }
    })
  },
  addComment(postId, data) {
    // data 只需 content 字段
    return api.post(`/posts/${postId}/comments`, { content: data.content })
  },
  deleteComment(postId, commentId) {
    return api.delete(`/posts/${postId}/comments/${commentId}`)
  },

  // 管理员专用API
  getAdminPosts(params) { return api.get('/admin/posts', { params }); },
  deleteAdminPost(id) { return api.delete(`/admin/posts/${id}`); },
  getAdminPostDetail(id) { return api.get(`/admin/posts/${id}`); },
  // 管理员删除评论
  deleteAdminComment(postId, commentId) {
    return api.delete(`/admin/posts/${postId}/comments/${commentId}`);
  },
}

// 文件上传API
export const uploadAPI = {
  // 上传图片（不再需要userId参数，后端从token解析）
  uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return axios.post('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
  }
}

export default postsAPI 