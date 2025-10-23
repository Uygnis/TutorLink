import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import Navbar from "@/components/Navbar";
import { GetAdminByUserId, GetDashboardSummary } from "@/api/adminAPI";
import { setLoading } from "@/redux/loaderSlice";
import { Tutor } from "@/types/TutorType";
import { AdminDashboardType } from "@/types/AdminDashboardType";

const AdminDashboard = () => {
  const [adminDetails, setAdminDetails] = useState<AdminDetails | null>(null);
  const [metrics, setMetrics] = useState<AdminDashboardType | null>(null);
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();
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


  const fetchMetrics = async (id: string) => {
    try {
      if (!user?.token) {
        toast.error("No token found. Please login again.");
        navigate("/login");
        return;
      }

      const response = await GetDashboardSummary(id, user.token);
      setMetrics(response.data);
      console.log(metrics?.pendingTutors);

    } catch (error: any) {
      console.error("Failed to fetch dashboard metrics", error);
    }
  };

  useEffect(() => {
    const loadDashboard = async () => {
      dispatch(setLoading(true));
      try {
        if (!user) {
          navigate("/login");
          return;
        }
        if (!user.id) {
          toast.error("User ID missing. Please login again.");
          navigate("/login");
          return;
        }
        await Promise.all([
          fetchAdminDetails(user.id),
          fetchMetrics(user.id)
        ]);
      } catch (err) {
        console.error(err);
      } finally {
        dispatch(setLoading(false));
      }
    };

    if (user?.id) {
      loadDashboard();
    }
  }, [user, navigate]);


  if (!loading || (metrics && adminDetails)) {
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

                {metrics && metrics?.pendingTutors.length > 0 ? (
                  <div className="space-y-3">
                    {metrics.pendingTutors.map((tutor: Tutor) => (
                      <div
                        key={tutor.userId}
                        className="flex justify-between items-center border p-3 rounded-md hover:bg-gray-50"
                      >
                        <div>
                          <p className="font-semibold">
                            {tutor.firstName} {tutor.lastName}
                          </p>
                          <p className="text-sm text-gray-600">{tutor.email}</p>
                          <p className="text-xs text-gray-500">
                            Subject: {tutor.subject ?? "N/A"}
                          </p>
                        </div>
                        <button
                          onClick={() => navigate(`/admin/tutors/${tutor.userId}`)}
                          className="bg-blue-600 text-white text-sm px-3 py-1 rounded hover:bg-blue-700"
                        >
                          View
                        </button>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="h-40 flex items-center justify-center text-gray-400">
                    No pending tutor activities at this time.
                  </div>
                )}
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
  }

};

export default AdminDashboard;
