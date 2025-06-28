import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import path from 'path'

export default defineConfig({
  plugins: [
    vue(),

    // 自动引入 API（如 ref、reactive、ElMessage）
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router'],
      dts: 'src/auto-imports.d.ts'
    }),

    // 自动注册组件（如 <el-button />）
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts'
    })
  ],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:3000',
        changeOrigin: true,
        secure: false,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('代理错误:', err);
            console.log('请求URL:', req.url);
            console.log('请求方法:', req.method);
          });
          proxy.on('proxyReq', (proxyReq, req, res) => {
            console.log('发送请求到目标:', req.method, req.url);
            console.log('请求头:', proxyReq.getHeaders());
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            console.log('收到目标响应:', proxyRes.statusCode, req.url);
          });
        },
        // 添加对文件上传的特殊处理
        onProxyReq: (proxyReq, req, res) => {
          if (req.headers['content-type'] && req.headers['content-type'].includes('multipart/form-data')) {
            console.log('处理multipart请求');
            // 移除可能导致问题的头部
            proxyReq.removeHeader('content-length');
          }
        }
      }
    }
  }
})
