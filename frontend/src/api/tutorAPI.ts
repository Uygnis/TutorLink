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
