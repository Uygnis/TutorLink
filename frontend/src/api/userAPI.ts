import axios, { AxiosResponse } from "axios";

export const LoginUser = async (email: string, password: string): Promise<AxiosResponse<any>> => {
  const url: string = `${import.meta.env.VITE_APP_API}/auth/login`;
  const data = { email, password };
  return await axios.post(url, data);
};

export const RegisterUser = async (
  firstname: string,
  lastname: string,
  email: string,
  password: string
): Promise<AxiosResponse<any>> => {
  const url: string = `${import.meta.env.VITE_APP_API}/auth/register`;
  const data = { firstname, lastname, email, password };
  return await axios.post(url, data);
};

export const CurrentUser = async (authtoken: string) => {
  const url = `${import.meta.env.VITE_APP_API}/user/currentUser`;
  const headers = { Authorization: `Bearer ${authtoken}` };
  return await axios.get(url, { headers });
};

export const CurrentAdmin = async (authtoken: string) => {
  const url = `${import.meta.env.VITE_APP_API}/user/currentAdmin`;
  const headers = { Authorization: `Bearer ${authtoken}` };
  return await axios.get(url, { headers });
};

export const GetAllAdmins = async (authtoken: string): Promise<AxiosResponse<any>> => {
  const url = `${import.meta.env.VITE_APP_API}/user/admins`;
  const headers = { Authorization: `Bearer ${authtoken}` };
  return await axios.get(url, { headers });
};

export const GetAdminById = async (id: string, authtoken: string) => {
  const url = `${import.meta.env.VITE_APP_API}/user/${id}`;
  const headers = { Authorization: `Bearer ${authtoken}` };
  return await axios.get(url, { headers });
};

export const UpdateAdmin = async (id: string, data: any, authtoken: string) => {
  const url = `${import.meta.env.VITE_APP_API}/user/${id}`;
  const headers = { Authorization: `Bearer ${authtoken}` };
  return await axios.put(url, data, { headers });
};

export const DeleteAdmin = async (id: string, authtoken: string) => {
  const url = `${import.meta.env.VITE_APP_API}/user/${id}`;
  const headers = { Authorization: `Bearer ${authtoken}` };
  return await axios.delete(url, { headers });
};
