import axios, { AxiosResponse } from "axios";

const BASE_URL = `${import.meta.env.VITE_APP_API}/admins`;

export const GetAdminByUserId = async (
  id: string,
  authtoken?: string
): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/by-user/${id}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetAllTutors = async (userId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/tutors/${userId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetTutorDetails = async (tutorId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/getTutorDetails/${tutorId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};


export const GetAllStudents = async (userId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/students/${userId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetStudentDetails = async (studentId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/getStudentDetails/${studentId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetAllAdmins = async (userId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.get(`${BASE_URL}/admins/${userId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const ApproveTutor = async (userId: any, tutorId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.put(`${BASE_URL}/approveTutor/${userId}/${tutorId}`, {}, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const RejectTutor = async (userId: any, tutorId: string, authtoken: string): Promise<AxiosResponse<any>> => {
  return await axios.put(`${BASE_URL}/rejectTutor/${userId}/${tutorId}`, {}, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};


export const SuspendUser = async (
  adminId: any,
  id: any,
  authtoken: string,
  role: string
): Promise<AxiosResponse<any>> => {
  const config = {
    headers: { Authorization: `Bearer ${authtoken}` },
  };

  if (role === "STUDENT") {
    return await axios.put(`${BASE_URL}/suspendStudent/${adminId}/${id}`, {}, config);
  } else if (role === "TUTOR") {
    return await axios.put(`${BASE_URL}/suspendTutor/${adminId}/${id}`, {}, config);
  } else {
    return await axios.put(`${BASE_URL}/suspendAdmin/${adminId}/${id}`, {}, config);
  }
};

export const ActivateUser = async (
  adminId: any,
  id: any,
  authtoken: string,
  role: string
): Promise<AxiosResponse<any>> => {
  const config = {
    headers: { Authorization: `Bearer ${authtoken}` },
  };

  if (role === "STUDENT") {
    return await axios.put(`${BASE_URL}/activateStudent/${adminId}/${id}`, {}, config);
  } else if (role === "TUTOR") {
    return await axios.put(`${BASE_URL}/activateTutor/${adminId}/${id}`, {}, config);
  } else {
    return await axios.put(`${BASE_URL}/activateAdmin/${adminId}/${id}`, {}, config);
  }
};

export const DeleteUser = async (
  adminId: any,
  id: any,
  authtoken: string,
  role: string
): Promise<AxiosResponse<any>> => {
  const config = {
    headers: { Authorization: `Bearer ${authtoken}` },
  };

  if (role === "STUDENT") {
    return await axios.delete(`${BASE_URL}/student/${adminId}/${id}`, config);
  } else if (role === "TUTOR") {
    return await axios.delete(`${BASE_URL}/tutor/${adminId}/${id}`, config);
  } else {
    return await axios.delete(`${BASE_URL}/admin/${adminId}/${id}`, config);
  }
};

