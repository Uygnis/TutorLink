import axios, { AxiosResponse } from "axios";

const BASE_URL = `${import.meta.env.VITE_APP_API}/admins`;

export const GetAdminByUserId = async (
  id: string,
  authtoken: string
): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/by-user/${id}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetAllTutors = async (authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/tutors`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetAllStudents = async (authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/students`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetAllAdmins = async (authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/admins`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};


export const DeleteUser = async (id: number, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.delete(`${BASE_URL}/${id}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};


