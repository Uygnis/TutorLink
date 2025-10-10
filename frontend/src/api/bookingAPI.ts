import axios, { AxiosResponse } from "axios";
import { BookingRequest, BookingResponse } from "@/types/BookingType";

const BASE_URL = `${import.meta.env.VITE_APP_API}/bookings`;

/** Create a new booking */
export const CreateBooking = async (
  req: BookingRequest,
  authtoken: string
): Promise<AxiosResponse<BookingResponse>> => {
  return await axios.post(`${BASE_URL}`, req, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

/** Accept a booking */
export const AcceptBooking = async (
  bookingId: string,
  authtoken: string
): Promise<AxiosResponse<BookingResponse>> => {
  return await axios.put(
    `${BASE_URL}/${bookingId}/accept`,
    {},
    {
      headers: { Authorization: `Bearer ${authtoken}` },
    }
  );
};

/** Get bookings for a tutor on a specific date  */
export const GetBookingsForTutor = async (
  tutorId: string,
  date: string,
  authtoken: string
): Promise<AxiosResponse<BookingResponse[]>> => {
  return await axios.get(`${BASE_URL}/tutor/${tutorId}`, {
    params: { date },
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

export const GetBookingsForTutorRange = (
  tutorId: string,
  startDate: string,
  endDate: string,
  token: string
) => {
  return axios.get(`${BASE_URL}/tutor/range/${tutorId}`, {
    params: { startDate, endDate },
    headers: { Authorization: `Bearer ${token}` },
  });
};

/** Get bookings for a student */
export const GetBookingsForStudent = async (
  studentId: string,
  authtoken: string
): Promise<AxiosResponse<BookingResponse[]>> => {
  return await axios.get(`${BASE_URL}/student/${studentId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};

/** Cancel a booking  */
export const CancelBooking = async (
  bookingId: string,
  authtoken: string
): Promise<AxiosResponse<BookingResponse>> => {
  return await axios.put(
    `${BASE_URL}/${bookingId}/cancel`,
    {},
    {
      headers: { Authorization: `Bearer ${authtoken}` },
    }
  );
};

/** Get booking by ID */
export const GetBookingById = async (
  bookingId: string,
  authtoken: string
): Promise<AxiosResponse<BookingResponse>> => {
  return await axios.get(`${BASE_URL}/${bookingId}`, {
    headers: { Authorization: `Bearer ${authtoken}` },
  });
};
