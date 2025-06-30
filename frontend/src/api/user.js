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

// 管理员API - 获取所有用户
export function getAllUsers() {
  return request.get('/users');
}

// 管理员API - 更新用户信息（角色、状态等）
export function updateUser(userId, data) {
  return request.put(`/users/${userId}`, data);
}

// 管理员API - 删除用户
export function deleteUser(userId) {
  return request.delete(`/users/${userId}`);
} 