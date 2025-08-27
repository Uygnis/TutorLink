import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { PlusIcon, PencilIcon, TrashIcon, EyeIcon } from "@heroicons/react/24/solid";
import { GetAllAdmins, DeleteUser } from "@/api/adminAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";

const ManageAdmins = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [admins, setAdmins] = useState<AdminDetails[]>([]);
  const [selectedAdmin, setSelectedAdmin] = useState<AdminDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const currentPermissions: string[] = user?.permissions || []; 

  const fetchAdmins = async () => {
    try {
      const token = user?.token;
      if (!token) return;

      const response = await GetAllAdmins(user.id, token);
      setAdmins(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch admins");
      console.error(error);
    }
  };

  const filteredAdmins = admins.filter((admin: any) =>
    admin.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenModal = (admin: any | null = null) => {
    setSelectedAdmin(admin);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedAdmin(null);
    fetchAdmins(); // Refresh list
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this admin?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteUser(id, token);
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
                      className={`px-4 py-2 ${
                        admin.status === "ACTIVE" ? "text-green-600" : "text-red-600"
                      }`}
                    >
                      {admin.status}
                    </td>
                    <td className="px-4 py-2 space-x-2">
                      <button
                        onClick={() => handleOpenModal(admin)}
                        className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1"
                      >
                        <EyeIcon className="h-4 w-4" />
                        <span>View</span>
                      </button>
                      {currentPermissions.includes("DELETE_ADMIN") && (
                        <button
                          onClick={() => handleDelete(admin.id)}
                          className="bg-red-100 text-red-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-red-200 transition inline-flex items-center space-x-1"
                        >
                          <TrashIcon className="h-4 w-4" />
                          <span>Delete</span>
                        </button>
                      )}
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
