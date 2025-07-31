# 比价宝后端服务 - Java重构版本

## 项目概述

本项目是比价宝系统的后端服务，使用Spring Boot框架和设计模式进行重构，提供了商品价格预测、监控等功能。

## 技术栈

- **框架**: Spring Boot 3.2.0
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **ORM**: Spring Data JPA
- **安全**: Spring Security + JWT
- **构建工具**: Maven
- **设计模式**: 策略模式、工厂模式、观察者模式、装饰器模式、单例模式、适配器模式

## 项目结构

```
src/main/java/com/bijiabao/
├── SummerTrainingBackendApplication.java    # 主应用类
├── config/                                  # 配置类
│   ├── ApplicationConfig.java
│   └── SecurityConfig.java
├── controller/                              # 控制器层
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProductController.java
│   ├── FavoriteController.java
│   └── PredictionController.java
├── dto/                                     # 数据传输对象
│   ├── AuthDto.java
│   ├── UserDto.java
│   ├── ProductDto.java
│   └── PredictionResult.java
├── entity/                                  # 实体类
│   ├── User.java
│   ├── Product.java
│   ├── ProductPrice.java
│   └── Favorite.java
├── patterns/                                # 设计模式实现
│   └── strategy/
│       ├── PredictionStrategy.java
│       ├── PredictionStrategyManager.java
│       └── impl/
│           ├── SimpleMovingAverageStrategy.java
│           └── ExponentialSmoothingStrategy.java
├── repository/                              # 数据访问层
│   ├── UserRepository.java
│   ├── ProductRepository.java
│   ├── ProductPriceRepository.java
│   └── FavoriteRepository.java
├── service/                                 # 服务层
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ProductService.java
│   ├── FavoriteService.java
│   ├── PriceHistoryService.java
│   └── impl/
│       ├── AuthServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── ProductServiceImpl.java
│       ├── FavoriteServiceImpl.java
│       └── PriceHistoryServiceImpl.java
└── util/                                    # 工具类
    └── JwtUtil.java
```

## 设计模式应用

### 1. 策略模式 (Strategy Pattern)
- **用途**: 管理不同的价格预测算法
- **实现**: `PredictionStrategy` 接口和具体策略实现类
- **优势**: 可以轻松添加新的预测算法，算法之间相互独立

### 2. 工厂模式 (Factory Pattern)
- **用途**: 创建不同类型的数据源连接
- **实现**: 通过Spring的依赖注入实现
- **优势**: 统一的数据源创建接口，易于扩展

### 3. 观察者模式 (Observer Pattern)
- **用途**: 价格监控和通知系统
- **实现**: Spring的事件机制
- **优势**: 松耦合的事件通知机制

### 4. 装饰器模式 (Decorator Pattern)
- **用途**: 增强API响应，添加额外信息
- **实现**: 通过Builder模式和组合实现
- **优势**: 动态增强响应内容

### 5. 单例模式 (Singleton Pattern)
- **用途**: 数据库连接管理和配置管理
- **实现**: Spring的Bean作用域
- **优势**: 确保全局唯一实例

### 6. 适配器模式 (Adapter Pattern)
- **用途**: 统一不同数据源的接口
- **实现**: Repository接口和实现
- **优势**: 统一的数据操作接口

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd refactor/summer-training-backend
```

2. **配置数据库**
```sql
CREATE DATABASE pricecompare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置**
编辑 `src/main/resources/application.yml`，修改数据库和Redis连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pricecompare
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

4. **运行项目**
```bash
mvn spring-boot:run
```

5. **访问API**
- 应用地址: http://localhost:8080
- API文档: http://localhost:8080/api/prediction/strategies

## API接口

### 认证接口

#### 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password"
}
```

#### 用户注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "password",
  "nickname": "新用户"
}
```

#### 验证令牌
```
POST /api/auth/validate
Content-Type: application/json

{
  "token": "jwt_token_here"
}
```

### 用户接口

#### 获取用户列表
```
GET /api/users?page=0&size=10
```

#### 获取用户信息
```
GET /api/users/{id}
```

#### 更新用户信息
```
PUT /api/users/{id}
Content-Type: application/json

{
  "nickname": "新昵称",
  "email": "newemail@example.com"
}
```

### 商品接口

#### 获取商品列表
```
GET /api/products?page=0&size=10&category=手机数码&status=1
```

#### 获取商品详情
```
GET /api/products/{id}/detail?userId=1
```

#### 搜索商品
```
GET /api/products/search?keyword=iPhone&page=0&size=10
```

#### 获取热门商品
```
GET /api/products/hot
```

#### 获取降价商品
```
GET /api/products/drop
```

### 收藏接口

#### 添加收藏
```
POST /api/favorites
Content-Type: application/json

{
  "userId": 1,
  "productId": 1
}
```

#### 取消收藏
```
DELETE /api/favorites
Content-Type: application/json

{
  "userId": 1,
  "productId": 1
}
```

#### 获取用户收藏列表
```
GET /api/favorites/user/{userId}?page=0&size=10
```

### 品牌接口

#### 获取品牌列表
```
GET /api/brands?page=0&size=10
```

#### 获取品牌信息
```
GET /api/brands/{id}
```

#### 根据名称获取品牌
```
GET /api/brands/name/{name}
```

#### 创建品牌
```
POST /api/brands
Content-Type: application/json

{
  "name": "品牌名称",
  "logo": "logo_url",
  "description": "品牌描述",
  "sortWeight": 1
}
```

#### 更新品牌信息
```
PUT /api/brands/{id}
Content-Type: application/json

{
  "name": "新品牌名",
  "description": "品牌描述"
}
```

#### 搜索品牌
```
GET /api/brands/search?keyword=关键词&page=0&size=10
```

#### 获取活跃品牌列表
```
GET /api/brands/active
```

#### 更新品牌状态
```
PUT /api/brands/{id}/status
Content-Type: application/json

{
  "status": 1
}
```

### 日志接口

#### 获取日志列表
```
GET /api/logs?page=0&size=10
```

#### 根据用户ID获取日志
```
GET /api/logs/user/{userId}?page=0&size=10
```

#### 根据操作类型获取日志
```
GET /api/logs/action/{action}?page=0&size=10
```

#### 根据时间范围获取日志
```
GET /api/logs/time-range?startTime=2024-01-01T00:00:00&endTime=2024-12-31T23:59:59
```

#### 搜索日志
```
GET /api/logs/search?keyword=关键词&page=0&size=10
```

#### 获取日志统计信息
```
GET /api/logs/stats
```

#### 获取操作类型统计
```
GET /api/logs/action-stats
```

### 推荐接口

#### 获取热门推荐
```
GET /api/recommend/hot?limit=10
```

#### 获取基于用户行为的推荐
```
GET /api/recommend/user/{userId}?limit=10
```

#### 获取基于商品相似度的推荐
```
GET /api/recommend/item/{productId}?limit=10
```

#### 获取基于分类的推荐
```
GET /api/recommend/category/{category}?limit=10
```

#### 获取基于品牌的推荐
```
GET /api/recommend/brand/{brandId}?limit=10
```

#### 获取降价推荐
```
GET /api/recommend/price-drop?limit=10
```

#### 获取个性化推荐
```
GET /api/recommend/personalized/{userId}?limit=10
```

#### 获取推荐统计信息
```
GET /api/recommend/stats
```

### 价格预测接口

#### 获取可用预测策略
```
GET /api/prediction/strategies
```

#### 使用指定策略进行预测
```
GET /api/prediction/{productId}?strategy=simple&days=7
```

参数说明:
- `productId`: 商品ID
- `strategy`: 预测策略 (simple, exponential, auto, all)
- `days`: 预测天数 (默认7天)

#### 使用所有策略进行预测
```
GET /api/prediction/{productId}/all
```

### 响应格式

成功响应:
```json
{
  "success": true,
  "predictedPrice": 99.99,
  "trend": "上涨",
  "confidence": 0.85,
  "dataPoints": 10,
  "strategyName": "简单移动平均",
  "predictionTime": "2024-01-01T12:00:00"
}
```

错误响应:
```json
{
  "success": false,
  "error": "数据不足，无法进行预测",
  "strategyName": "简单移动平均"
}
```

## 预测策略

### 1. 简单移动平均
- **适用场景**: 价格波动较小的商品
- **算法**: 计算最近N天价格的平均值
- **参数**: days (天数)

### 2. 指数平滑
- **适用场景**: 有趋势性的价格数据
- **算法**: 使用指数平滑公式预测
- **参数**: alpha (平滑系数), days (天数)

### 3. 自动选择
- **适用场景**: 根据数据特征自动选择最佳策略
- **算法**: 分析价格波动性，选择最适合的策略

## 缓存策略

- **价格历史**: 缓存商品的价格历史数据
- **预测结果**: 缓存预测结果，提高响应速度
- **数据特征**: 缓存计算的数据特征

## 监控和日志

- **日志级别**: DEBUG (开发环境)
- **监控端点**: Spring Boot Actuator
- **健康检查**: `/actuator/health`

## 扩展指南

### 添加新的预测策略

1. 实现 `PredictionStrategy` 接口:
```java
@Component
public class NewPredictionStrategy implements PredictionStrategy {
    @Override
    public PredictionResult predict(Long productId, Map<String, Object> options) {
        // 实现预测逻辑
    }
    
    @Override
    public String getStrategyName() {
        return "新策略";
    }
    
    @Override
    public String getDescription() {
        return "新策略描述";
    }
}
```

2. Spring会自动注册新策略到 `PredictionStrategyManager`

### 添加新的数据源

1. 创建Repository接口
2. 实现数据访问逻辑
3. 在Service中使用

## 测试

运行测试:
```bash
mvn test
```

## 部署

### 打包
```bash
mvn clean package
```

### 运行JAR
```bash
java -jar target/summer-training-backend-1.0.0.jar
```

### Docker部署
```bash
docker build -t bijiabao-backend .
docker run -p 8080:8080 bijiabao-backend
```

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

- 项目维护者: [Your Name]
- 邮箱: [your.email@example.com]
- 项目地址: [repository-url] 