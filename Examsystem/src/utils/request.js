// 文件类型：js
// 文件名称：request.js

import axios from 'axios';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080', // 后端 API 的基础 URL
    timeout: 5000, // 请求超时时间（毫秒）
});

// 提取成功状态码为变量
const SUCCESS_CODE = '200'; // 后端返回的 code 为字符串类型

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        // 获取 token
        const token = localStorage.getItem('token');
        if (token) {
            // 如果有 token，设置到请求头中
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        // 设置请求头的 Content-Type
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
        return config;
    },
    (error) => {
        // 请求发送失败时的处理
        ElMessage.error('请求发送失败，请检查网络');
        return Promise.reject(error);
    }
);

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        // 如果返回的是字符串，尝试转为 JSON
        if (typeof response.data === 'string') {
            response.data = response.data ? JSON.parse(response.data) : response.data;
        }

        // 处理业务逻辑错误
        if (response.data?.code !== undefined && response.data.code !== SUCCESS_CODE) {
            ElMessage.error(response.data.message || '操作失败');
            return Promise.reject(response.data);
        }

        // 返回处理后的响应数据
        return response.data;
    },
    (error) => {
        // 获取路由实例
        const router = useRouter();

        // 处理 HTTP 错误状态码
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 401：未授权，清除 token 并重定向到登录页面
                    ElMessage.error('登录已过期，请重新登录');
                    localStorage.removeItem('token');
                    router.push('/login');
                    break;
                case 403:
                    // 403：禁止访问
                    ElMessage.error('没有操作权限');
                    break;
                case 404:
                    // 404：未找到接口
                    ElMessage.error('未找到请求接口');
                    break;
                case 500:
                    // 500：服务器错误
                    ElMessage.error('系统异常，请查看控制台');
                    break;
                default:
                    // 其他错误
                    ElMessage.error(`请求错误: ${error.message}`);
            }
        } else {
            // 网络错误
            ElMessage.error('网络错误，请检查连接');
        }

        // 返回错误的 Promise，便于调用方处理
        return Promise.reject(error);
    }
);

// 导出 request 实例
export default request;