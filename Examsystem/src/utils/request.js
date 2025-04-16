import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
    baseURL: 'http://localhost:8080', // 后端 API 的基础 URL
    timeout: 5000, // 请求超时时间（毫秒）
});

// 请求拦截器
request.interceptors.request.use(
    (config) => {
        const requestURL = config.url;
        if (!requestURL.includes('/api/user/register') && !requestURL.includes('/api/user/login')) {
            // 获取 token
            const token = localStorage.getItem('token');
            if (token) {
                // 如果有 token，设置到请求头中
                config.headers['Authorization'] = `Bearer ${token}`;
            }
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
        return response.data;
    },
    (error) => {
        // 响应出错时的处理
        ElMessage.error('响应出错: ' + error.message);
        return Promise.reject(error);
    }
);

export default request;