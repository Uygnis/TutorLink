import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { TrashIcon, EyeIcon, PauseCircleIcon, PlayCircleIcon } from "@heroicons/react/24/solid";
import { GetAllAdmins, DeleteUser, ActivateUser, SuspendUser } from "@/api/adminAPI";
import { toast } from "react-toastify";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { setLoading } from "@/redux/loaderSlice";
import { useNavigate } from "react-router-dom";

const ManageAdmins = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [, setIsModalOpen] = useState(false);
  const [admins, setAdmins] = useState<AdminDetails[]>([]);
  const [, setSelectedAdmin] = useState<AdminDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const currentPermissions: string[] = user?.permissions || [];
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const fetchAdmins = async () => {
    try {
      dispatch(setLoading(true));
      const token = user?.token;
      if (!token) return;

      const response = await GetAllAdmins(user.id, token);
      setAdmins(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch admins");
      console.error(error);
    } finally {
      dispatch(setLoading(false));
    }
  };

  const filteredAdmins = admins.filter((admin: any) =>
    admin.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleViewAdmin = (admin: any | null = null) => {
    navigate(`/admin/admins/${admin.id}`);
  };

  // const handleCloseModal = () => {
  //   setIsModalOpen(false);
  //   setSelectedAdmin(null);
  //   fetchAdmins(); // Refresh list

  // };

  const handleSuspendModal = async (admin: any) => {
    const isSuspended = admin.status === "SUSPENDED";
    const action = isSuspended ? "activate" : "suspend";

    if (!confirm(`Are you sure you want to ${action} this admin?`)) return;

    try {
      const token = user?.token;
      if (!token) return;

      if (isSuspended) {
        // Admin is suspended → call activate API
        await ActivateUser(user?.id, admin.id, token, "ADMIN");
        toast.success("Admin activated successfully");
      } else {
        // Admin is active → call suspend API
        await SuspendUser(user?.id, admin.id, token, "ADMIN");
        toast.success("Admin suspended successfully");
      }

      fetchAdmins(); // refresh list
    } catch (error: any) {
      toast.error(`Failed to ${action} admin`);
      console.error(error);
    }
  };

  const handleDelete = async (adminId: number) => {
    if (!confirm("Are you sure you want to delete this admin?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteUser(user.id, adminId, token, "ADMIN");
      toast.success("Admin deleted successfully");
      fetchAdmins(); // Refresh the list
    } catch (error: any) {
      toast.error("Failed to delete admin");
      console.error(error);
    }
  };

  useEffect(() => {
    fetchAdmins();
  }, []);

  return (
    <div>
      <Navbar />
      <div className="p-6">
        <div>
          {/* Search Bar */}
          <input
            type="search"
            placeholder="Search admins..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
          />

          <div className="flex justify-between items-center my-4">
            <h2 className="text-lg font-bold">List of Admins</h2>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full table-auto border-collapse">
              <thead>
                <tr className="bg-gray-100 text-left text-sm font-medium text-gray-600">
                  <th className="px-4 py-2">Name</th>
                  <th className="px-4 py-2">Email</th>
                  <th className="px-4 py-2">Status</th>
                  <th className="px-4 py-2">Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredAdmins.map((admin: any) => (
                  <tr key={admin.id} className="border-b text-sm text-gray-700">
                    <td className="px-4 py-2">{admin.name}</td>
                    <td className="px-4 py-2">{admin.email}</td>
                    <td
                      className={`px-4 py-2 ${admin.status === "ACTIVE"
                        ? "text-green-600"
                        : admin.status === "SUSPENDED"
                          ? "text-red-600"
                          : "text-gray-600"
                        }`}
                    >
                      {admin.status}
                    </td>
                    <td className="px-4 py-2 space-x-2">
                    <button
                      onClick={() => handleViewAdmin(admin)}
                      className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                      <EyeIcon className="h-4 w-4" />
                      <span>View</span>
                    </button>
                      <div className="relative group inline-block">
                        <button
                          onClick={() => currentPermissions.includes("SUSPEND_ADMIN") && handleSuspendModal(admin)}
                          disabled={(!currentPermissions.includes("SUSPEND_ADMIN") || admin.id === user?.id || admin.status === "DELETED")}
                          className={`px-3 py-1 rounded-md backdrop-blur-sm transition inline-flex items-center space-x-1
                          ${!currentPermissions.includes("SUSPEND_ADMIN") || admin.id === user?.id || admin.status === "DELETED"
                              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                              : admin.status === "SUSPENDED"
                                ? "bg-green-100 text-green-700 hover:bg-green-200"
                                : "bg-orange-100 text-orange-700 hover:bg-orange-200"
                            }`}
                        >
                          {admin.status === "SUSPENDED" ? (
                            <PlayCircleIcon className="h-4 w-4" />
                          ) : (
                            <PauseCircleIcon className="h-4 w-4" />
                          )}
                          <span>{admin.status === "SUSPENDED" ? "Activate" : "Suspend"}</span>
                        </button>

                        {/* Tooltip if no permission */}
                        {!currentPermissions.includes("SUSPEND_ADMIN") && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You do not have permission to suspend admin
                          </div>
                        )}
                        {admin.id === user?.id && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot suspend your own account
                          </div>
                        )}

                        {admin.status === "DELETED" && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot suspend a deleted admin
                          </div>
                        )}
                      </div>

                      <div className="relative group inline-block">
                        <button
                          onClick={() => currentPermissions.includes("DELETE_ADMIN") && handleDelete(admin.id)}
                          disabled={!currentPermissions.includes("DELETE_ADMIN") || admin.status !== "SUSPENDED"}
                          className={`px-3 py-1 rounded-md backdrop-blur-sm transition inline-flex items-center space-x-1
                          ${!currentPermissions.includes("DELETE_ADMIN")
                              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                              : admin.status !== "SUSPENDED"
                                ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                                : "bg-red-100 text-red-700 hover:bg-red-200"
                            }`}
                        >
                          <TrashIcon className="h-4 w-4" />
                          <span>Delete</span>
                        </button>

                        {/* Tooltip */}
                        {!currentPermissions.includes("DELETE_ADMIN") ? (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You do not have permission to delete admin
                          </div>
                        ) : admin.status === "ACTIVE" ? (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot delete an active admin
                          </div>
                        ) : null}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* <CreateAdminModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            admin={selectedAdmin}
          /> */}
        </div>
      </div>
    </div>
  );
};
export default ManageAdmins;
