import Navbar from "@/components/Navbar";
import SubNav from "@/components/SubNavbar"; // adjust path as needed
import { Outlet } from "react-router-dom";

const AdminDashboard = () => {
  return (
    <div>
      <Navbar />
      <SubNav />
      <div className="min-h-screen bg-[#f2f2f2] p-6">
        <div className="mx-auto bg-white rounded-lg shadow-md p-6">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
