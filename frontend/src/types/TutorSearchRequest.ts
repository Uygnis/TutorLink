export interface TutorSearchRequest {
  name?: string;
  subject?: string;
  minPrice?: number;
  maxPrice?: number;
  availability?: string; // e.g., MONDAY, WEEKDAY, EVENING
}
