import axios from "axios";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";

const request = axios.create({
    baseURL: "http://localhost:8080",
    timeout: 5000
});

// 请求拦截器
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    config.headers['Content-Type'] = 'application/json;charset=utf-8';
    return config;
}, error => {
    return Promise.reject(error);
});

// 响应拦截器
request.interceptors.response.use(
    response => {
        // 如果返回的是字符串，尝试转为JSON
        if (typeof response.data === 'string') {
            response.data = response.data ? JSON.parse(response.data) : response.data;
        }

        // 处理业务逻辑错误（假设后端返回code不为0表示错误）
        if (response.data?.code !== undefined && response.data.code !== 0) {
            ElMessage.error(response.data.message || '操作失败');
            return Promise.reject(response.data);
        }

        return response.data;
    },
    error => {
        const router = useRouter();

        // 处理HTTP错误状态码
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    ElMessage.error('登录已过期，请重新登录');
                    localStorage.removeItem('token');
                    router.push('/login');
                    break;
                case 403:
                    ElMessage.error('没有操作权限');
                    break;
                case 404:
                    ElMessage.error('未找到请求接口');
                    break;
                case 500:
                    ElMessage.error('系统异常，请查看控制台');
                    break;
                default:
                    ElMessage.error(`请求错误: ${error.message}`);
            }
        } else {
            ElMessage.error('网络错误，请检查连接');
        }

        return Promise.reject(error);
    }
);

export default request;