interface StudentDetails {
  id: number;
  name: string;
  email: string;
  studentNumber: string;
  gradeLevel: string;
  student: {
    studentNumber: string;
    gradeLevel: string;
  }
}
