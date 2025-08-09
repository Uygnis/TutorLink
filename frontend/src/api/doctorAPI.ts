import axios, { AxiosResponse } from "axios";

const BASE_URL = `${import.meta.env.VITE_APP_API}/doctors`;

export const GetAllDoctors = async (authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(BASE_URL, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetDoctorById = async (id: number, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/${id}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const CreateDoctor = async (
  doctorData: {
    name: string;
    docId: string;
    email: string;
    status: string;
  },
  authtoken: string
): Promise<AxiosResponse<any>> => {
  return await axios.post(BASE_URL, doctorData, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const UpdateDoctor = async (
  id: number,
  doctorData: {
    name?: string;
    email?: string;
    status?: string;
  },
  authtoken: string
): Promise<AxiosResponse<any>> => {
  return await axios.put(`${BASE_URL}/${id}`, doctorData, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const DeleteDoctor = async (id: number, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.delete(`${BASE_URL}/${id}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};
