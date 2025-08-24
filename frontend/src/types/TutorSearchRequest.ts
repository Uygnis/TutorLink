export interface TutorSearchRequest {
  name?: string;
  subject?: string;
  minPrice?: number;
  maxPrice?: number;
  availability?: string; // e.g., MONDAY, WEEKDAY, EVENING
}

export type DayAvailability = {
  [day: string]: { start: string; end: string; enabled: boolean };
};

export interface Tutor {
  _id: string;
  firstname: string;
  lastname: string;
  subject: string;
  hourlyRate: number;
  availability: DayAvailability;
}
