type TutorDetails = {
  userId: string;
  hourlyRate: number;
  fileUploads: File[]; // store uploaded files
  availability: DayAvailability;
  subject: string;
  qualifications: QualificationFileType[];
  description?: string;
  lessonType?: string[];
  profileImageUrl?: string;
};

type TutorCredentials = {
  password: string;
  email: string;
};

type TutorAccount = TutorDetails & TutorCredentials;
