import { AxiosResponse } from "axios";
import { getApiInstance } from "./axios/instanceAPI";

const BASE_URL = `/tutors`;

export const UpdateTutorProfile = async (
  token: string,
  userId: string,
  data: FormData
): Promise<AxiosResponse<any>> => {
  const url = `${BASE_URL}/${userId}`;
  const api = getApiInstance(token);

  return await api.put(url, data, {
    headers: {
      ...api.defaults.headers.common,
      "Content-Type": "multipart/form-data",
    },
  });
};

export const GetTutorProfile = async (
  token: string,
  userId: string
): Promise<AxiosResponse<any>> => {
  const url = `${BASE_URL}/${userId}`;
  const api = getApiInstance(token);
  return await api.get(url);
};

export const UploadTutorProfilePicture = async (
  tutorId: string,
  file: File,
  token: string
): Promise<AxiosResponse<any>> => {
  const formData = new FormData();
  formData.append("file", file);

  const api = getApiInstance(token);

  return await api.post(`${BASE_URL}/${tutorId}/profile-picture`, formData, {
    headers: {
      ...api.defaults.headers.common,
      "Content-Type": "multipart/form-data",
    },
  });
};
