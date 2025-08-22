export type NavLink = {
  name: string;
  path: string;
};

export const navConfig: Record<string, NavLink[]> = {
  ADMIN: [
    { name: "Home", path: "/admin/dashboard" },
    { name: "Doctors", path: "/admin/doctors" },
  ],
  STUDENT: [
    { name: "Home", path: "/student/dashboard" },
    { name: "Find a Tutor", path: "/student/find-tutor" },
  ],
};
