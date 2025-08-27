export type NavLink = {
  name: string;
  path: string;
  requiredPermissions: string[];
};

export const navConfig: Record<string, NavLink[]> = {
  ADMIN: [
    {
      name: "Dashboard",
      path: "/admin/dashboard",
      requiredPermissions: [] // always visible
    },
    {
      name: "Students",
      path: "/admin/students",
      requiredPermissions: ["VIEW_STUDENTS"]
    },
    {
      name: "Tutors",
      path: "/admin/tutors",
      requiredPermissions: ["VIEW_TUTORS"]
    },
    {
      name: "Admins",
      path: "/admin/admins",
      requiredPermissions: ["VIEW_ADMIN"]
    }
  ],
  STUDENT: [
    { name: "Home", path: "/student/dashboard", requiredPermissions: [] },
    { name: "Find a Tutor", path: "/student/find-tutor", requiredPermissions: [] },
  ],
};
