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
    password: '123456'
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
    req.user = { id: 1, username: 'admin' }; // 简化用户信息
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
          email: user.email
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
    password
  };
  
  users.push(newUser);
  
  res.json({
    code: 0,
    message: '注册成功',
    data: {
      user: {
        id: newUser.id,
        username: newUser.username,
        email: newUser.email
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