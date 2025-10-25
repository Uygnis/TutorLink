export interface TutorSearchRequest {
  name?: string;
  subject?: string;
  minPrice?: number;
  maxPrice?: number;
  availability?: string;
  description?: string;
  lessonType?: string[];
  profileImageUrl?: string;
  qualifications?: QualificationFileType[];
}

export type DayAvailability = {
  [day: string]: { start: string; end: string; enabled: boolean };
};

export interface Tutor {
  id: string;
  userId: string;
  firstName: string;
  lastName: string;
  subject: string;
  hourlyRate: number;
  fileUploads: File[]; // store uploaded files
  availability: DayAvailability;
  description: string;
  lessonType: string[];
  profileImageUrl: string;
  qualifications: QualificationFileType[];
  email: string;
  status: string;
  stagedProfile: Tutor | null;
  rejectedReason: string;
}
