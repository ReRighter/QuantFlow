import type { Result } from "@/types/api";

export const apiClient = async (url: string, options: RequestInit= {}) => {
   
    
    const response = await fetch(url, options);

    if (!response.ok) {
        if (response.status === 401) {
            alert('请登录');
            throw new Error('请登录');
        }
        if (response.status === 403) {
            alert('权限不足');
            throw new Error('权限不足');
        }
        const errorData = await response.json();

        throw new Error(errorData.message || '请求失败');
    }

    return response;
    
}


