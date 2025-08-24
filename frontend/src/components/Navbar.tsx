import { useNavigate, useLocation } from "react-router-dom";
import { useAppDispatch } from "@/redux/store";
import { setUser } from "@/redux/userSlice";
import { navConfig } from "@/components/NavLinks";

const Navbar = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const user = JSON.parse(localStorage.getItem("user") || "{}");
  const role = user?.role || "ADMIN";

  const navLinks = navConfig[role] || [];

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    dispatch(setUser(null));
    navigate("/login");
  };

  return (
    <div className="w-full bg-white h-16 flex items-stretch justify-between px-6 border-b border-gray-300 sticky top-0 z-50">
      <div className="flex items-stretch space-x-6">
        <div className="text-2xl font-bold text-primary flex items-center">TutorLink</div>

        {navLinks.map((link) => {
          const isActive =
            location.pathname === link.path || location.pathname.startsWith(link.path + "/");

          return (
            <a
              key={link.path}
              href={link.path}
              className={`flex items-center px-4 text-medium transition ${
                isActive
                  ? "text-white font-bold bg-primary my-4 rounded-lg"
                  : "text-gray-600 hover:bg-gray-200 hover:text-primary"
              }`}>
              {link.name}
            </a>
          );
        })}
      </div>

      <div className="flex items-center">
        <button
          onClick={handleLogout}
          className="rounded-lg bg-primary text-white px-4 py-2 transition duration-300 hover:bg-gray-200 hover:text-primary">
          Logout
        </button>
      </div>
    </div>
  );
};

export default Navbar;
