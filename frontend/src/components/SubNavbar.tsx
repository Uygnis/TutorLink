import { NavLink } from "react-router-dom";

const SubNav = () => {
  return (
    <nav className="bg-white px-6 py-3 flex space-x-6 mx-auto">
      <NavLink
        to="doctors"
        className={({ isActive }) =>
          `px-4 py-2 text-sm rounded-md font-semibold transition ${
            isActive ? "bg-blue-500 bg-opacity-20 text-primary" : "text-gray-600 hover:text-primary"
          }`
        }>
        Doctors
      </NavLink>

      <NavLink
        to="admins"
        className={({ isActive }) =>
          `px-4 py-2 text-sm rounded-md font-semibold transition ${
            isActive ? "bg-blue-500 bg-opacity-20 text-primary" : "text-gray-600 hover:text-primary"
          }`
        }>
        Admins
      </NavLink>
    </nav>
  );
};

export default SubNav;
