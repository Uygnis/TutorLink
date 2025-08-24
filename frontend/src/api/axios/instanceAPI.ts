import axios, { AxiosInstance } from "axios";

export const getApiInstance = (token?: string): AxiosInstance => {
  const instance = axios.create({
    baseURL: import.meta.env.VITE_APP_API, // or "/api"
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });

  return instance;
};
