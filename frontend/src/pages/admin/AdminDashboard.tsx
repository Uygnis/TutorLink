import { useNavigate } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import { GetAdminByUserId } from "@/api/adminAPI";

const AdminDashboard = () => {
  const [adminDetails, setAdminDetails] = useState<AdminDetails | null>(null);
  const [metrics, setMetrics] = useState({
    totalUsers: 0,
    totalTutors: 0,
    totalStudents: 0,
    suspendedUsers: 0,
  });

  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();

  const fetchAdminDetails = async (id: string) => {
    try {
      if (!user?.token) {
        toast.error("No token found. Please login again.");
        navigate("/login");
        return;
      }

      const response = await GetAdminByUserId(id, user.token);
      setAdminDetails(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch admin details");
      console.error(error);
    }
  };

  const fetchMetrics = async () => {
    try {
      if (!user?.token) return;

      // const [totalUsersRes, activeSessionsRes, pendingApprovalsRes, suspendedUsersRes, totalAdminsRes] = await Promise.all([
      //   GetTotalUsers(user.token),
      //   GetActiveSessions(user.token),
      //   GetPendingApprovals(user.token),
      //   GetSuspendedUsers(user.token),
      //   GetTotalAdmins(user.token),
      // ]);

      setMetrics({
        totalUsers: 1,
        totalTutors: 5,
        totalStudents: 5,
        suspendedUsers: 4,
      });

    } catch (error: any) {
      console.error("Failed to fetch dashboard metrics", error);
    }
  };

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (!user.id) {
      toast.error("User ID missing. Please login again.");
      navigate("/login");
      return;
    }
    fetchAdminDetails(user.id);
    fetchMetrics();

  }, [user, navigate]);

  return (
    <div>
      <Navbar />
      <div className="min-h-screen bg-[#f2f2f2] p-6">
        <h1 className="font-bold text-xl mb-5 ">Welcome to your Dashboard ! </h1>
        {/* Two-column layout */}
        <div className="flex gap-6">
          <div className="flex flex-col w-[70%] space-y-6">
            <div className="bg-white rounded-md shadow-md p-5">
              <h2 className="font-bold text-lg mb-3">Users Summary</h2>
              {metrics ? (
                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-blue-50 p-3 rounded text-center">
                    <p className="text-sm text-gray-500">Total Users</p>
                    <p className="font-bold text-xl">{metrics.totalUsers}</p>
                  </div>
                  <div className="bg-green-50 p-3 rounded text-center">
                    <p className="text-sm text-gray-500">Total Tutors</p>
                    <p className="font-bold text-xl">{metrics.totalTutors}</p>
                  </div>
                  <div className="bg-yellow-50 p-3 rounded text-center">
                    <p className="text-sm text-gray-500">Total Students</p>
                    <p className="font-bold text-xl">{metrics.totalStudents}</p>
                  </div>
                  <div className="bg-red-50 p-3 rounded text-center">
                    <p className="text-sm text-gray-500">Suspended Users</p>
                    <p className="font-bold text-xl">{metrics.suspendedUsers}</p>
                  </div>
                </div>
              ) : (
                <div className="h-40 flex items-center justify-center text-gray-400">
                  No users to show for summary.
                </div>
              )}


            </div>

            <div className="bg-white rounded-md shadow-md p-5">
              <h2 className="font-bold text-lg mb-3">Pending Activities</h2>
              <div className="h-40 flex items-center justify-center text-gray-400">
                No pending activies at this time.
              </div>
            </div>
          </div>

          {/* Right side (Admin Profile Card) */}
          <div className="w-[30%]">
            <div className="bg-white rounded-md shadow-md p-5">
              <div className="text-center">
                <h1 className="font-bold text-xl">Admin Profile</h1>
                {adminDetails ? (
                  <div className="mt-4 text-left">
                    <p>
                      <strong>Full Name:</strong> {user?.name}
                    </p>
                    <p>
                      <strong>Email:</strong> {user?.email}
                    </p>
                    <p><strong>Admin Permissions:</strong></p>
                    <div className="mt-1 flex flex-wrap gap-2">
                      {adminDetails.permissions.map((perm) => (
                        <span
                          key={perm as string}
                          className="bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded"
                        >
                          {perm}
                        </span>
                      ))}
                    </div>
                  </div>
                ) : (
                  <p>Loading admin details...</p>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
