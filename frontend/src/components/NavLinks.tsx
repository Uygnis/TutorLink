export type NavLink = {
  name: string;
  path: string;
};

export const navConfig: Record<string, NavLink[]> = {
  ADMIN: [
    { name: "Home", path: "/admin/dashboard" },
    { name: "Manage Tutors", path: "/admin/tutors" },
    { name: "Manage Students", path: "/admin/students" },
    { name: "Manage Admins", path: "/admin/admins" },
  ],
  STUDENT: [
    { name: "Home", path: "/student/dashboard" },
    { name: "Find a Tutor", path: "/student/find-tutor" },
  ],
};
