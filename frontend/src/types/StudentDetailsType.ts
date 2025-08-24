interface StudentDetails {
  id: number;
  name: string;
  email: string;
  student: {
    studentNumber: string;
    gradeLevel: string;
  }
}
