type TutorDetails = {
  userId: string;
  hourlyRate: number;
  qualifications: File[]; // store uploaded files
  availability: {};
};

type TutorCredentials = {
  password: string;
  email: string;
};

type TutorAccount = TutorDetails & TutorCredentials;
