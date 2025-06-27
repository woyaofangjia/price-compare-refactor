import axios from 'axios';
import router from '../router';

const service = axios.create({
  baseURL: '/api', // 根据实际后端代理配置调整
  timeout: 5000
});

// 请求拦截器，自动加 token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器，未登录自动跳转
service.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      router.push('/user/login');
    }
    return Promise.reject(error);
  }
);

export default service; 