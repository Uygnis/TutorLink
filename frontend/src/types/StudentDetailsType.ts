interface StudentDetails {
  id: number;
  name: string;
  email: string;
  studentNumber: string;
  gradeLevel: string;
  profileImageUrl: string;
  student: {
    studentNumber: string;
    gradeLevel: string;
  };
}
