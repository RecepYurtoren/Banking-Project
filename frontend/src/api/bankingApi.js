import axios from 'axios';

const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Account API
export const accountApi = {
  getAllAccounts: () => api.get('/accounts'),
  getAccountById: (id) => api.get(`/accounts/${id}`),
  getAccountByNumber: (accountNumber) => api.get(`/accounts/number/${accountNumber}`),
  createSavingsAccount: (data) => api.post('/accounts/savings', data),
  createCheckingAccount: (data) => api.post('/accounts/checking', data),
  deposit: (accountId, data) => api.post(`/accounts/${accountId}/deposit`, data),
  withdraw: (accountId, data) => api.post(`/accounts/${accountId}/withdraw`, data),
  transfer: (data) => api.post('/accounts/transfer', data),
  deactivateAccount: (id) => api.put(`/accounts/${id}/deactivate`),
  activateAccount: (id) => api.put(`/accounts/${id}/activate`),
};

// Transaction API
export const transactionApi = {
  getTransactionsByAccount: (accountId) => api.get(`/transactions/account/${accountId}`),
  getMonthlyTransactions: (accountId, year, month) => 
    api.get(`/transactions/account/${accountId}/monthly`, { params: { year, month } }),
  getTransactionByReference: (referenceNumber) => 
    api.get(`/transactions/reference/${referenceNumber}`),
};

// Report API
export const reportApi = {
  getMonthlyReport: (accountId, year, month) => 
    api.get(`/reports/monthly/${accountId}`, { params: { year, month } }),
  calculateInterest: (accountId) => api.get(`/reports/interest/calculate/${accountId}`),
  applyInterest: (accountId) => api.post(`/reports/interest/apply/${accountId}`),
};

// Admin API (Bank Employee)
export const adminApi = {
  getAllCustomers: () => api.get('/admin/customers'),
  searchCustomers: (query) => api.get('/admin/customers/search', { params: { query } }),
  getCustomer: (id) => api.get(`/admin/customers/${id}`),
  getCustomerAccounts: (customerId) => api.get(`/admin/customers/${customerId}/accounts`),
  getCustomerTransactions: (customerId) => api.get(`/admin/customers/${customerId}/transactions`),
  getAllAccounts: () => api.get('/admin/accounts'),
  getAccountTransactions: (accountId) => api.get(`/admin/accounts/${accountId}/transactions`),
  getTransactionByReference: (referenceNumber) => api.get(`/admin/transactions/${referenceNumber}`),
};

export default api;
