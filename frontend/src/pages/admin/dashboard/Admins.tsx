import { useEffect, useState } from "react";
import { PlusIcon, PencilIcon, TrashIcon } from "@heroicons/react/24/solid";
import { DeleteAdmin, GetAllAdmins } from "@/api/userAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import CreateAdminModal from "@/components/CreateAdminModal";

const Admins = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [admins, setAdmins] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedAdmin, setSelectedAdmin] = useState(null);

  const { user } = useAppSelector((state) => state.user);

  const fetchAdmins = async () => {
    try {
      const token = user?.token;
      if (!token) return;

      const response = await GetAllAdmins(token);
      setAdmins(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch admins");
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm("Are you sure you want to delete this admin?")) return;

    try {
      const token = user?.token;
      await DeleteAdmin(id, token);
      toast.success("Admin deleted successfully");
      fetchAdmins();
    } catch (error: any) {
      toast.error("Failed to delete admin");
    }
  };

  useEffect(() => {
    fetchAdmins();
  }, []);

  const filteredAdmins = admins.filter((admin: any) =>
    admin.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      <input
        type="search"
        placeholder="Search admins..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
      />
      <div className="flex justify-between items-center mb-4 mt-4">
        <h2 className="text-lg font-bold">List of Admins</h2>
        <button
          onClick={() => {
            setSelectedAdmin(null); // Reset for create mode
            setIsModalOpen(true);
          }}
          className="bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 flex items-center space-x-2">
          <PlusIcon className="h-5 w-5" />
          <span>Add Admin</span>
        </button>
      </div>

      {/* Admin Table */}
      <div className="overflow-x-auto">
        <table className="min-w-full table-auto border-collapse">
          <thead>
            <tr className="bg-gray-100 text-left text-sm font-medium text-gray-600">
              <th className="px-4 py-2">Name</th>
              <th className="px-4 py-2">Email</th>
              <th className="px-4 py-2">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredAdmins.map((admin: any) => (
              <tr key={admin.id} className="border-b text-sm text-gray-700">
                <td className="px-4 py-2">{admin.name}</td>
                <td className="px-4 py-2">{admin.email}</td>
                <td className="px-4 py-2 space-x-2">
                  <button
                    onClick={() => {
                      setSelectedAdmin(admin);
                      setIsModalOpen(true);
                    }}
                    className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                    <PencilIcon className="h-4 w-4" />
                    <span>Edit</span>
                  </button>

                  <button
                    onClick={() => handleDelete(admin.id)}
                    className="bg-red-100 text-red-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-red-200 transition inline-flex items-center space-x-1">
                    <TrashIcon className="h-4 w-4" />
                    <span>Delete</span>
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Modal for Create/Edit Admin */}
      <CreateAdminModal
        isOpen={isModalOpen}
        admin={selectedAdmin}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedAdmin(null);
          fetchAdmins(); // Refresh list after create/edit
        }}
      />
    </div>
  );
};

export default Admins;
