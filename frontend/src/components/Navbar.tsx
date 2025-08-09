import { useNavigate, useLocation } from "react-router-dom";
import { useAppDispatch } from "@/redux/store";
import { setUser } from "@/redux/userSlice";

const Navbar = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    dispatch(setUser(null));
    navigate("/login");
  };

  const navLinks = [
    { name: "Internal", path: "/admin/dashboard" },
    { name: "Patients", path: "/admin/patients" },
    { name: "Wards", path: "/admin/wards" },
    { name: "Transactions", path: "/admin/transactions" },
  ];

  return (
    <div className="w-full bg-primary  h-16 flex items-stretch justify-between px-6">
      {/* Left side */}
      <div className="flex items-stretch space-x-6">
        <div className="text-2xl font-bold text-white flex items-center">🏥</div>

        {navLinks.map((link) => {
          const isActive =
            location.pathname === link.path || location.pathname.startsWith(link.path + "/");

          return (
            <a
              key={link.path}
              href={link.path}
              className={`flex items-center px-4 text-sm font-medium transition ${
                isActive
                  ? "bg-white text-primary h-full"
                  : "text-white hover:bg-white hover:text-primary"
              }`}>
              {link.name}
            </a>
          );
        })}
      </div>

      {/* Logout Button */}
      <div className="flex items-center">
        <button
          onClick={handleLogout}
          className="rounded-lg bg-white text-primary px-4 py-2 transition duration-300 hover:bg-gray-200 hover:text-primary">
          Logout
        </button>
      </div>
    </div>
  );
};

export default Navbar;
