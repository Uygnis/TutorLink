import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import defaultProfile from "../../assets/default-profile-pic.jpg";
import Navbar from "@/components/Navbar";
import { setLoading } from "@/redux/loaderSlice";
import { GetAdminByUserId } from "@/api/adminAPI";

const AdminViewAdminDetails = () => {
  const { adminId } = useParams<{ adminId: string }>();
  const [admin, setAdmin] = useState<any | null>(null);
  const { user } = useAppSelector((state) => state.user);
  const navigate = useNavigate();
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();

  useEffect(() => {
    const fetchAdmin = async () => {
      if (!adminId || !user?.token) return;

      try {
        dispatch(setLoading(true));
        const res = await GetAdminByUserId(adminId, user.token);
        const data = res.data;

        console.log("data", data);
        setAdmin(data);
      } catch (err) {
        console.error("Failed to fetch admin:", err);
      } finally {
        dispatch(setLoading(false));
      }
    };
    fetchAdmin();
  }, [adminId, user]);


  if (!loading || admin) {
    return (
      <div>
        <Navbar />
        <div className="min-h-screen bg-[#f9f9f9] p-6">
          <button
            onClick={() => navigate(-1)}
            className="mb-4 px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition">
            ← Back
          </button>

          {/* Admin Profile + Qualifications */}
          <div className="grid grid-cols-1 md:grid-cols-5 gap-6 mb-6">
            {/* Admin Profile (60%) */}
            <div className="bg-white rounded-lg shadow-md p-6 flex flex-col md:flex-row gap-6 md:col-span-3 max-h-[320px]">
              <img
                src={admin?.profileImageUrl || defaultProfile}
                alt={admin?.firstName}
                className="w-32 h-32 rounded-full object-cover border shadow"
              />
              <div className="flex-1">
                <h1 className="text-3xl font-bold">
                  {admin?.firstName} {admin?.lastName}
                </h1>
                {/* Subjects with Badge Style */}
                <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Status:</span>
                  <span
                    className={`px-3 py-1 rounded-full text-sm font-medium ${
                      admin?.status === "ACTIVE"
                        ? "bg-green-100 text-green-800"
                        : "bg-red-100 text-red-800"
                    }`}>
                    {admin?.status}</span>
                </div>
                <div className="mt-3 flex flex-wrap items-center gap-2">
                  <span className="font-semibold text-gray-700">Email:</span>
                  <span>
                    {admin?.email}</span>
                </div>
              </div>
            </div>

            <div className="bg-white rounded-lg shadow-md p-6 md:col-span-2 max-h-[320px] overflow-y-auto">
              <h2 className="text-xl font-semibold mb-3">Current Permissions</h2>
              <div className="flex flex-wrap gap-3">
              {admin?.permissions && admin.permissions.length > 0 ? (
                admin.permissions.map((perm: string, index: number) => (
                  <span
                    key={index}
                    className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                    {perm}
                  </span>
                ))
              ) : (
                <p className="text-gray-500">No permissions assigned.</p>
              )}</div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-md mb-6 p-6">
            <h2 className="text-xl font-semibold mb-3"></h2>
           
          </div>
        </div>
      </div>
    );
  };
}

export default AdminViewAdminDetails;