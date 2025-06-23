// 模拟 API 请求
export const fetchDynamics = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve([
        {
          id: 1,
          userId: 101,
          username: '数码测评师',
          avatar: 'https://pic1.zhimg.com/80/v2-6f9a371e5d5a7b0b7a8a9e5e5d5a7b0b_720w.webp',
          content: '刚入手了新款iPhone 15 Pro Max，续航能力太强了，一天重度使用完全没问题！拍照效果也比上一代有明显提升，特别是夜景模式。',
          images: [
            'https://imgservice.suning.cn/uimg1/b2c/image/4e6Lr4Gm4fKcVr1Qy6dLHA.jpg_800w_800h_4e',
            'https://imgservice.suning.cn/uimg1/b2c/image/_i4QxwR9Q0S0jz3nXe4e0Q.jpg_800w_800h_4e',
            'https://imgservice.suning.cn/uimg1/b2c/image/5v8DZc1e5iM6lKv7f2QJ2w.jpg_800w_800h_4e'
          ],
          likes: 124,
          comments: 42,
          isLiked: false,
          isCollected: false,
          timestamp: '2小时前',
          product: {
            id: 1001,
            name: 'Apple iPhone 15 Pro Max',
            price: 8999,
            platform: '京东自营',
            image: 'https://imgservice.suning.cn/uimg1/b2c/image/4e6Lr4Gm4fKcVr1Qy6dLHA.jpg'
          }
        },
        // 更多动态数据...
      ])
    }, 500)
  })
}

// 实际项目中会使用类似这样的函数
/*
import axios from 'axios'

export const fetchDynamics = async () => {
  try {
    const response = await axios.get('/api/dynamics')
    return response.data
  } catch (error) {
    console.error('获取动态数据失败:', error)
    return []
  }
}
*/
