import request from '../utils/request';

// 注册
export function register(data) {
  return request.post('/auth/register', data);
}

// 登录
export function login(data) {
  return request.post('/auth/login', data);
}

// 获取用户信息
export function getProfile() {
  return request.get('/auth/profile');
}

// 修改用户信息
export function updateProfile(data) {
  return request.put('/auth/profile', data);
} 