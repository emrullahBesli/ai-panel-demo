import axios from 'axios';

const api = axios.create({
    baseURL: '/',
});

export const chat = (userMessage: string) => api.get(`/chat?userMessage=${userMessage}`);

// Entity APIs
export const listUsers = () => api.get('/api/entities/users');
export const createUser = (username: string, email: string) => 
    api.post(`/api/entities/users?username=${username}&email=${email}`);

export const listOrders = () => api.get('/api/entities/orders');
export const createOrder = (description: string, paidAmount: number, userId: number) => 
    api.post(`/api/entities/orders?description=${description}&paidAmount=${paidAmount}&userId=${userId}`);

export const listOrderUsages = () => api.get('/api/entities/order-usages');
export const createOrderUsage = (orderId: number, usageDetails: string) => 
    api.post(`/api/entities/order-usages?orderId=${orderId}&usageDetails=${usageDetails}`);
