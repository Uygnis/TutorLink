import { TutorSearchRequest } from "@/types/TutorSearchRequest";
import axios, { AxiosResponse } from "axios";

const BASE_URL = `${import.meta.env.VITE_APP_API}/students`;

export const GetStudentByUserId = async (
  id: string,
  authtoken: string
): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/by-user/${id}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const SearchTutors = async (
  req: TutorSearchRequest,
  authtoken: string
): Promise<AxiosResponse<any>> => {
  return await axios.post(`${BASE_URL}/search`, req, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};
