const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
const app = express();
const PORT = 3001;

// 中间件
app.use(cors());
app.use(express.json());

// 创建uploads目录
const uploadsDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

// 配置multer用于文件上传
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, uploadsDir);
  },
  filename: function (req, file, cb) {
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, uniqueSuffix + path.extname(file.originalname));
  }
});

const upload = multer({ 
  storage: storage,
  limits: {
    fileSize: 5 * 1024 * 1024 // 5MB
  },
  fileFilter: function (req, file, cb) {
    if (file.mimetype.startsWith('image/')) {
      cb(null, true);
    } else {
      cb(new Error('只允许上传图片文件'));
    }
  }
});

// 静态文件服务
app.use('/uploads', express.static(uploadsDir));

// 模拟用户数据存储
let users = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    password: '123456',
    isadmin: 1,
    status: 'active',
    created_at: new Date().toISOString()
  }
];

// 模拟动态数据存储
let posts = [
  {
    id: 1,
    userId: 1,
    username: 'admin',
    userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    content: '刚入手的新耳机，音质真的很棒！推荐给大家。',
    images: ['https://images.unsplash.com/photo-1572536147248-ac59a8abfa4b'],
    likes: 12,
    comments: 3,
    isLiked: false,
    isCollected: false,
    time: '2小时前',
    createdAt: new Date().toISOString()
  },
  {
    id: 2,
    userId: 1,
    username: 'admin',
    userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    content: '今天发现了一个很不错的购物网站，价格比实体店便宜很多！',
    images: ['https://images.unsplash.com/photo-1511707171634-5f897ff02aa9'],
    likes: 8,
    comments: 2,
    isLiked: true,
    isCollected: false,
    time: '1天前',
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
  }
];

// 模拟评论数据
let comments = [];

// 简单的JWT验证中间件
function authenticateToken(req, res, next) {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (!token) {
    return res.status(401).json({ code: 1, message: '未提供认证令牌' });
  }

  // 这里简化处理，实际应该验证JWT
  if (token.startsWith('mock-jwt-token-')) {
    const user = users.find(u => u.id === 1); // 简化处理，假设都是admin用户
    req.user = { 
      id: user.id, 
      username: user.username,
      isadmin: user.isadmin
    };
    next();
  } else {
    return res.status(403).json({ code: 1, message: '无效的认证令牌' });
  }
}

// 登录接口
app.post('/login', (req, res) => {
  const { username, password } = req.body;
  
  const user = users.find(u => u.username === username && u.password === password);
  
  if (user) {
    res.json({
      code: 0,
      message: '登录成功',
      data: {
        user: {
          id: user.id,
          username: user.username,
          email: user.email,
          isadmin: user.isadmin,
          status: user.status
        },
        token: 'mock-jwt-token-' + Date.now()
      }
    });
  } else {
    res.status(401).json({
      code: 1,
      message: '用户名或密码错误'
    });
  }
});

// 注册接口
app.post('/register', (req, res) => {
  const { username, email, password } = req.body;
  
  // 检查用户是否已存在
  const existingUser = users.find(u => u.username === username || u.email === email);
  
  if (existingUser) {
    res.status(400).json({
      code: 1,
      message: '用户名或邮箱已存在'
    });
    return;
  }
  
  // 创建新用户
  const newUser = {
    id: users.length + 1,
    username,
    email,
    password,
    isadmin: 0,
    status: 'active',
    created_at: new Date().toISOString()
  };
  
  users.push(newUser);
  
  res.json({
    code: 0,
    message: '注册成功',
    data: {
      user: {
        id: newUser.id,
        username: newUser.username,
        email: newUser.email,
        isadmin: newUser.isadmin,
        status: newUser.status
      },
      token: 'mock-jwt-token-' + Date.now()
    }
  });
});

// 获取动态列表
app.get('/posts', authenticateToken, (req, res) => {
  const { page = 1, pageSize = 10, keyword = '', sort = 'latest' } = req.query;
  
  let filteredPosts = [...posts];
  
  // 关键词搜索
  if (keyword) {
    filteredPosts = filteredPosts.filter(post => 
      post.content.toLowerCase().includes(keyword.toLowerCase())
    );
  }
  
  // 排序
  if (sort === 'likes') {
    filteredPosts.sort((a, b) => b.likes - a.likes);
  } else if (sort === 'comments') {
    filteredPosts.sort((a, b) => b.comments - a.comments);
  } else {
    // latest
    filteredPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + parseInt(pageSize);
  const paginatedPosts = filteredPosts.slice(startIndex, endIndex);
  
  res.json({
    code: 0,
    message: '获取成功',
    data: {
      list: paginatedPosts,
      total: filteredPosts.length,
      page: parseInt(page),
      pageSize: parseInt(pageSize)
    }
  });
});

// 获取用户收藏的动态列表
app.get('/posts/collections/user/:userId', authenticateToken, (req, res) => {
  const userId = parseInt(req.params.userId);
  const { page = 1, pageSize = 10, sort = 'latest' } = req.query;
  
  // 验证用户权限（只能查看自己的收藏）
  if (req.user.id !== userId) {
    return res.status(403).json({ code: 1, message: '无权限查看其他用户的收藏' });
  }
  
  // 获取用户收藏的动态（这里简化处理，实际应该有收藏表）
  let collectedPosts = posts.filter(post => post.isCollected);
  
  // 排序
  if (sort === 'likes') {
    collectedPosts.sort((a, b) => b.likes - a.likes);
  } else if (sort === 'comments') {
    collectedPosts.sort((a, b) => b.comments - a.comments);
  } else {
    // latest
    collectedPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + parseInt(pageSize);
  const paginatedPosts = collectedPosts.slice(startIndex, endIndex);
  
  res.json({
    code: 0,
    message: '获取成功',
    data: {
      list: paginatedPosts,
      total: collectedPosts.length,
      page: parseInt(page),
      pageSize: parseInt(pageSize)
    }
  });
});

// 获取用户动态列表
app.get('/posts/user/:userId', authenticateToken, (req, res) => {
  const userId = parseInt(req.params.userId);
  const { page = 1, pageSize = 10, sort = 'latest' } = req.query;
  
  // 获取用户发布的动态
  let userPosts = posts.filter(post => post.userId === userId);
  
  // 排序
  if (sort === 'likes') {
    userPosts.sort((a, b) => b.likes - a.likes);
  } else if (sort === 'comments') {
    userPosts.sort((a, b) => b.comments - a.comments);
  } else {
    // latest
    userPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  }
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + parseInt(pageSize);
  const paginatedPosts = userPosts.slice(startIndex, endIndex);
  
  res.json({
    code: 0,
    message: '获取成功',
    data: {
      list: paginatedPosts,
      total: userPosts.length,
      page: parseInt(page),
      pageSize: parseInt(pageSize)
    }
  });
});

// 获取推荐动态
app.get('/posts/recommend', authenticateToken, (req, res) => {
  // 这里简化处理，返回最新的动态作为推荐
  let recommendedPosts = [...posts];
  
  // 按最新时间排序
  recommendedPosts.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  
  // 返回前10条
  const topPosts = recommendedPosts.slice(0, 10);
  
  res.json({
    code: 0,
    message: '获取成功',
    data: {
      list: topPosts,
      total: topPosts.length
    }
  });
});

// 获取动态详情
app.get('/posts/:id', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const post = posts.find(p => p.id === postId);
  
  if (!post) {
    return res.status(404).json({ code: 1, message: '动态不存在' });
  }
  
  res.json({
    code: 0,
    message: '获取成功',
    data: post
  });
});

// 创建动态
app.post('/posts', authenticateToken, (req, res) => {
  const { content, images = [] } = req.body;
  
  if (!content || !content.trim()) {
    return res.status(400).json({ code: 1, message: '动态内容不能为空' });
  }
  
  const newPost = {
    id: posts.length + 1,
    userId: req.user.id,
    username: req.user.username,
    userAvatar: 'https://randomuser.me/api/portraits/men/32.jpg',
    content: content.trim(),
    images: images,
    likes: 0,
    comments: 0,
    isLiked: false,
    isCollected: false,
    time: '刚刚',
    createdAt: new Date().toISOString()
  };
  
  posts.unshift(newPost); // 添加到开头
  
  res.json({
    code: 0,
    message: '发布成功',
    data: newPost
  });
});

// 更新动态
app.put('/posts/:id', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const { content, images = [] } = req.body;
  
  const postIndex = posts.findIndex(p => p.id === postId);
  if (postIndex === -1) {
    return res.status(404).json({ code: 1, message: '动态不存在' });
  }
  
  const post = posts[postIndex];
  if (post.userId !== req.user.id) {
    return res.status(403).json({ code: 1, message: '无权限修改此动态' });
  }
  
  post.content = content.trim();
  post.images = images;
  
  res.json({
    code: 0,
    message: '更新成功',
    data: post
  });
});

// 删除动态
app.delete('/posts/:id', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const postIndex = posts.findIndex(p => p.id === postId);
  
  if (postIndex === -1) {
    return res.status(404).json({ code: 1, message: '动态不存在' });
  }
  
  const post = posts[postIndex];
  if (post.userId !== req.user.id) {
    return res.status(403).json({ code: 1, message: '无权限删除此动态' });
  }
  
  posts.splice(postIndex, 1);
  
  res.json({
    code: 0,
    message: '删除成功'
  });
});

// 点赞/取消点赞
app.post('/posts/:id/like', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const post = posts.find(p => p.id === postId);
  
  if (!post) {
    return res.status(404).json({ code: 1, message: '动态不存在' });
  }
  
  post.isLiked = !post.isLiked;
  post.likes += post.isLiked ? 1 : -1;
  
  res.json({
    code: 0,
    message: post.isLiked ? '点赞成功' : '取消点赞',
    data: {
      likes: post.likes,
      isLiked: post.isLiked
    }
  });
});

// 收藏/取消收藏
app.post('/posts/:id/collect', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const post = posts.find(p => p.id === postId);
  
  if (!post) {
    return res.status(404).json({ code: 1, message: '动态不存在' });
  }
  
  post.isCollected = !post.isCollected;
  
  res.json({
    code: 0,
    message: post.isCollected ? '收藏成功' : '取消收藏',
    data: {
      isCollected: post.isCollected
    }
  });
});

// 获取评论列表
app.get('/posts/:id/comments', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const postComments = comments.filter(c => c.postId === postId);
  
  res.json({
    code: 0,
    message: '获取成功',
    data: postComments
  });
});

// 添加评论
app.post('/posts/:id/comments', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.id);
  const { content } = req.body;
  
  const post = posts.find(p => p.id === postId);
  if (!post) {
    return res.status(404).json({ code: 1, message: '动态不存在' });
  }
  
  if (!content || !content.trim()) {
    return res.status(400).json({ code: 1, message: '评论内容不能为空' });
  }
  
  const newComment = {
    id: comments.length + 1,
    postId: postId,
    userId: req.user.id,
    author: req.user.username,
    avatar: 'https://randomuser.me/api/portraits/lego/1.jpg',
    text: content.trim(),
    time: '刚刚',
    createdAt: new Date().toISOString()
  };
  
  comments.push(newComment);
  post.comments += 1;
  
  res.json({
    code: 0,
    message: '评论成功',
    data: newComment
  });
});

// 删除评论
app.delete('/posts/:postId/comments/:commentId', authenticateToken, (req, res) => {
  const postId = parseInt(req.params.postId);
  const commentId = parseInt(req.params.commentId);
  
  const commentIndex = comments.findIndex(c => c.id === commentId && c.postId === postId);
  if (commentIndex === -1) {
    return res.status(404).json({ code: 1, message: '评论不存在' });
  }
  
  const comment = comments[commentIndex];
  if (comment.userId !== req.user.id) {
    return res.status(403).json({ code: 1, message: '无权限删除此评论' });
  }
  
  comments.splice(commentIndex, 1);
  
  // 更新动态的评论数
  const post = posts.find(p => p.id === postId);
  if (post) {
    post.comments = Math.max(0, post.comments - 1);
  }
  
  res.json({
    code: 0,
    message: '删除成功'
  });
});

// 模拟商品数据
let products = [
  {
    id: 1,
    title: 'Apple AirPods Pro',
    desc: '主动降噪无线耳机，空间音频技术',
    price: 1899,
    currentPrice: 1899,
    priceChange: -5.2,
    img: '/default-product.png',
    platforms: ['京东', '天猫', '苏宁'],
    watchCount: 1250,
    status: 'active',
    createdAt: new Date().toISOString()
  },
  {
    id: 2,
    title: 'iPhone 15 Pro',
    desc: 'A17 Pro芯片，钛金属设计',
    price: 7999,
    currentPrice: 7999,
    priceChange: 0,
    img: '/default-product.png',
    platforms: ['京东', '天猫', '拼多多'],
    watchCount: 890,
    status: 'active',
    createdAt: new Date().toISOString()
  },
  {
    id: 3,
    title: 'MacBook Air M2',
    desc: '13.6英寸，M2芯片，超长续航',
    price: 9499,
    currentPrice: 9299,
    priceChange: -2.1,
    img: '/default-product.png',
    platforms: ['京东', '天猫', '苏宁'],
    watchCount: 567,
    status: 'active',
    createdAt: new Date().toISOString()
  }
];

// 模拟收藏数据
let favorites = [
  {
    id: 1,
    user_id: 1,
    product_id: 1,
    alertPrice: null,
    created_at: new Date().toISOString()
  },
  {
    id: 2,
    user_id: 1,
    product_id: 2,
    alertPrice: 7500,
    created_at: new Date().toISOString()
  }
];

// 获取商品列表
app.get('/products', (req, res) => {
  const { limit = 100, page = 1, pageSize = 20 } = req.query;
  
  let filteredProducts = [...products];
  
  // 分页
  const startIndex = (page - 1) * pageSize;
  const endIndex = startIndex + parseInt(pageSize);
  const paginatedProducts = filteredProducts.slice(startIndex, endIndex);
  
  // 限制返回数量
  const limitedProducts = limit ? paginatedProducts.slice(0, parseInt(limit)) : paginatedProducts;
  
  res.json({
    code: 0,
    message: '获取成功',
    data: {
      list: limitedProducts,
      total: filteredProducts.length,
      page: parseInt(page),
      pageSize: parseInt(pageSize)
    }
  });
});

// 获取热门商品
app.get('/products/hot', (req, res) => {
  // 返回监控次数最多的商品作为热门商品
  const hotProducts = [...products]
    .sort((a, b) => b.watchCount - a.watchCount)
    .slice(0, 10);
  
  res.json(hotProducts);
});

// 获取商品详情
app.get('/products/:id', (req, res) => {
  const productId = parseInt(req.params.id);
  const product = products.find(p => p.id === productId);
  
  if (!product) {
    return res.status(404).json({ code: 1, message: '商品不存在' });
  }
  
  res.json(product);
});

// 获取商品图表数据
app.get('/products/:id/chart-data', (req, res) => {
  const productId = parseInt(req.params.id);
  const product = products.find(p => p.id === productId);
  
  if (!product) {
    return res.status(404).json({ code: 1, message: '商品不存在' });
  }
  
  // 模拟图表数据
  const platformData = {
    '京东': [
      { date: '2024-01-01', price: product.price },
      { date: '2024-01-15', price: product.price + 50 },
      { date: '2024-02-01', price: product.currentPrice }
    ],
    '天猫': [
      { date: '2024-01-01', price: product.price - 20 },
      { date: '2024-01-15', price: product.price + 30 },
      { date: '2024-02-01', price: product.currentPrice - 10 }
    ],
    '苏宁': [
      { date: '2024-01-01', price: product.price + 30 },
      { date: '2024-01-15', price: product.price + 80 },
      { date: '2024-02-01', price: product.currentPrice + 20 }
    ]
  };
  
  const monthlyData = [
    { month: '1月', avgPrice: product.price },
    { month: '2月', avgPrice: product.currentPrice },
    { month: '3月', avgPrice: product.currentPrice - 100 },
    { month: '4月', avgPrice: product.currentPrice - 50 }
  ];
  
  res.json({
    platformData,
    monthlyData
  });
});

// 获取用户收藏
app.get('/favorites', authenticateToken, (req, res) => {
  const userId = parseInt(req.query.userId);
  
  // 验证用户权限
  if (req.user.id !== userId) {
    return res.status(403).json({ code: 1, message: '无权限查看其他用户的收藏' });
  }
  
  const userFavorites = favorites.filter(f => f.user_id === userId);
  
  // 获取收藏的商品详情
  const favoriteProducts = userFavorites.map(favorite => {
    const product = products.find(p => p.id === favorite.product_id);
    return product ? { 
      ...product, 
      favorite_id: favorite.id,
      alertPrice: favorite.alertPrice || null
    } : null;
  }).filter(Boolean);
  
  res.json(favoriteProducts);
});

// 添加收藏
app.post('/favorites', authenticateToken, (req, res) => {
  const { productId } = req.body;
  const userId = req.user.id;
  
  if (!productId) {
    return res.status(400).json({ code: 1, message: '商品ID不能为空' });
  }
  
  // 检查商品是否存在
  const product = products.find(p => p.id === parseInt(productId));
  if (!product) {
    return res.status(404).json({ code: 1, message: '商品不存在' });
  }
  
  // 检查是否已经收藏
  const existingFavorite = favorites.find(f => f.user_id === userId && f.product_id === parseInt(productId));
  if (existingFavorite) {
    return res.status(400).json({ code: 1, message: '该商品已经收藏过了' });
  }
  
  // 创建新收藏
  const newFavorite = {
    id: favorites.length + 1,
    user_id: userId,
    product_id: parseInt(productId),
    alertPrice: null,
    created_at: new Date().toISOString()
  };
  
  favorites.push(newFavorite);
  
  res.json({
    code: 0,
    message: '收藏成功',
    data: newFavorite
  });
});

// 删除收藏
app.delete('/favorites/:favoriteId', authenticateToken, (req, res) => {
  const favoriteId = parseInt(req.params.favoriteId);
  const userId = req.user.id;
  
  const favoriteIndex = favorites.findIndex(f => f.id === favoriteId && f.user_id === userId);
  
  if (favoriteIndex === -1) {
    return res.status(404).json({ code: 1, message: '收藏不存在或无权限删除' });
  }
  
  favorites.splice(favoriteIndex, 1);
  
  res.json({
    code: 0,
    message: '取消收藏成功'
  });
});

// 检查收藏状态
app.get('/favorites/check', authenticateToken, (req, res) => {
  const userId = parseInt(req.query.userId);
  const productId = parseInt(req.query.productId);
  
  // 验证用户权限
  if (req.user.id !== userId) {
    return res.status(403).json({ code: 1, message: '无权限查看其他用户的收藏' });
  }
  
  const favorite = favorites.find(f => f.user_id === userId && f.product_id === productId);
  
  res.json({
    code: 0,
    message: '检查成功',
    data: {
      exists: !!favorite,
      id: favorite ? favorite.id : null
    }
  });
});

// 设置提醒价格
app.put('/favorites/:favoriteId/alert', authenticateToken, (req, res) => {
  const favoriteId = parseInt(req.params.favoriteId);
  const { alertPrice } = req.body;
  const userId = req.user.id;
  
  const favorite = favorites.find(f => f.id === favoriteId && f.user_id === userId);
  
  if (!favorite) {
    return res.status(404).json({ code: 1, message: '收藏不存在或无权限修改' });
  }
  
  // 验证价格
  if (alertPrice !== null && alertPrice !== undefined) {
    const price = parseFloat(alertPrice);
    if (isNaN(price) || price <= 0) {
      return res.status(400).json({ code: 1, message: '提醒价格必须大于0' });
    }
    favorite.alertPrice = price;
  } else {
    favorite.alertPrice = null;
  }
  
  res.json({
    code: 0,
    message: '提醒价格设置成功',
    data: {
      id: favorite.id,
      alertPrice: favorite.alertPrice
    }
  });
});

// 管理员API - 获取所有用户
app.get('/admin/users', authenticateToken, (req, res) => {
  // 检查是否为管理员
  const currentUser = users.find(u => u.id === req.user.id);
  if (!currentUser || !currentUser.isadmin) {
    return res.status(403).json({ code: 1, message: '无权限访问管理员功能' });
  }
  
  // 返回所有用户（不包含密码）
  const userList = users.map(user => ({
    id: user.id,
    username: user.username,
    email: user.email,
    isadmin: user.isadmin,
    status: user.status,
    created_at: user.created_at
  }));
  
  res.json({
    code: 0,
    message: '获取成功',
    data: userList
  });
});

// 管理员API - 更新用户信息
app.put('/admin/users/:userId', authenticateToken, (req, res) => {
  // 检查是否为管理员
  const currentUser = users.find(u => u.id === req.user.id);
  if (!currentUser || !currentUser.isadmin) {
    return res.status(403).json({ code: 1, message: '无权限访问管理员功能' });
  }
  
  const userId = parseInt(req.params.userId);
  const { isadmin, status } = req.body;
  
  const userIndex = users.findIndex(u => u.id === userId);
  if (userIndex === -1) {
    return res.status(404).json({ code: 1, message: '用户不存在' });
  }
  
  // 更新用户信息
  if (isadmin !== undefined) {
    users[userIndex].isadmin = isadmin;
  }
  if (status !== undefined) {
    users[userIndex].status = status;
  }
  
  res.json({
    code: 0,
    message: '更新成功',
    data: {
      id: users[userIndex].id,
      username: users[userIndex].username,
      email: users[userIndex].email,
      isadmin: users[userIndex].isadmin,
      status: users[userIndex].status,
      created_at: users[userIndex].created_at
    }
  });
});

// 管理员API - 删除用户
app.delete('/admin/users/:userId', authenticateToken, (req, res) => {
  // 检查是否为管理员
  const currentUser = users.find(u => u.id === req.user.id);
  if (!currentUser || !currentUser.isadmin) {
    return res.status(403).json({ code: 1, message: '无权限访问管理员功能' });
  }
  
  const userId = parseInt(req.params.userId);
  
  // 不能删除自己
  if (userId === req.user.id) {
    return res.status(400).json({ code: 1, message: '不能删除自己的账户' });
  }
  
  const userIndex = users.findIndex(u => u.id === userId);
  if (userIndex === -1) {
    return res.status(404).json({ code: 1, message: '用户不存在' });
  }
  
  // 不能删除管理员
  if (users[userIndex].isadmin) {
    return res.status(400).json({ code: 1, message: '不能删除管理员账户' });
  }
  
  users.splice(userIndex, 1);
  
  res.json({
    code: 0,
    message: '删除成功'
  });
});

// 图片上传
app.post('/upload/image', authenticateToken, upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ code: 1, message: '请选择要上传的图片' });
  }
  
  const imageUrl = `http://localhost:${PORT}/uploads/${req.file.filename}`;
  
  res.json({
    code: 0,
    message: '上传成功',
    data: {
      url: imageUrl,
      filename: req.file.filename
    }
  });
});

// 错误处理中间件
app.use((error, req, res, next) => {
  if (error instanceof multer.MulterError) {
    if (error.code === 'LIMIT_FILE_SIZE') {
      return res.status(400).json({ code: 1, message: '文件大小不能超过5MB' });
    }
  }
  
  console.error('服务器错误:', error);
  res.status(500).json({ code: 1, message: '服务器内部错误' });
});

// 健康检查接口
app.get('/health', (req, res) => {
  res.json({ status: 'ok', message: '服务器运行正常' });
});

app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`);
  console.log(`图片上传目录: ${uploadsDir}`);
}); 