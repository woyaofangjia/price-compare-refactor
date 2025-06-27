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
    return response.data
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 动态相关API
export const postsAPI = {
  // 获取动态列表
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

  // 点赞/取消点赞
  toggleLike(id) {
    return api.post(`/posts/${id}/like`)
  },

  // 收藏/取消收藏
  toggleCollect(id) {
    return api.post(`/posts/${id}/collect`)
  },

  // 获取热门动态
  getTrendingPosts() {
    return api.get('/posts/trending')
  },

  // 获取用户动态
  getUserPosts(userId) {
    return api.get(`/posts/user/${userId}`)
  },

  // 评论相关API
  getComments(postId) {
    return api.get(`/posts/${postId}/comments`)
  },
  addComment(postId, data) {
    return api.post(`/posts/${postId}/comments`, data)
  },
  deleteComment(postId, commentId) {
    return api.delete(`/posts/${postId}/comments/${commentId}`)
  }
}

// 文件上传API
export const uploadAPI = {
  // 上传图片
  uploadImage(file, userId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)
    
    return axios.post('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
  }
}

export default postsAPI 