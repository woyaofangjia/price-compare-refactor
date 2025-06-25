const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 3001;

// 中间件
app.use(cors());
app.use(express.json());

// 模拟用户数据存储
let users = [
  {
    id: 1,
    username: 'admin',
    email: 'admin@example.com',
    password: '123456'
  }
];

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

// 健康检查接口
app.get('/health', (req, res) => {
  res.json({ status: 'ok', message: '服务器运行正常' });
});

app.listen(PORT, () => {
  console.log(`服务器运行在 http://localhost:${PORT}`);
}); 