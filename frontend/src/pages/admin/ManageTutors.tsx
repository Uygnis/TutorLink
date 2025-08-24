import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { PlusIcon, PencilIcon, TrashIcon } from "@heroicons/react/24/solid";
import { GetAllTutors, DeleteUser } from "@/api/adminAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import CreateTutorModal from "@/components/CreateTutorModal";
import { Tutor } from "@/types/TutorSearchRequest";

const ManageTutors = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [tutors, setTutors] = useState<Tutor[]>([]);
  const [selectedTutor, setSelectedTutor] = useState<Tutor | null>(null);

  const { user } = useAppSelector((state) => state.user);

  const fetchTutors = async () => {
    try {
      const token = user?.token;
      if (!token) return;

      const response = await GetAllTutors(token);
      console.log("Admin API Response:", response.data);
      setTutors(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch tutors");
      console.error(error);
    }
  };

  const filteredTutors = tutors.filter((tutor: any) =>
    tutor.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenModal = (tutor: any | null = null) => {
    setSelectedTutor(tutor);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedTutor(null);
    fetchTutors(); // Refresh list
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this tutor?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteUser(id, token);
      toast.success("Tutor deleted successfully");
      fetchTutors(); // Refresh the list
    } catch (error: any) {
      toast.error("Failed to delete tutor");
      console.error(error);
    }
  };

  useEffect(() => {
    fetchTutors();
  }, []);
  return (
    <div>
      <Navbar />

      <div className="p-6">
        <div>
          {/* Search Bar */}
          <input
            type="search"
            placeholder="Search tutors..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
          />

          <div className="flex justify-between items-center my-4">
            <h2 className="text-lg font-bold">List of Tutors</h2>
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
                {filteredTutors.map((tutor: any) => (
                  <tr key={tutor.id} className="border-b text-sm text-gray-700">
                    <td className="px-4 py-2">{tutor.name}</td>
                    <td className="px-4 py-2">{tutor.email}</td>
                    <td
                      className={`px-4 py-2 ${
                        tutor.status === "Active" ? "text-green-600" : "text-red-600"
                      }`}>
                      {tutor.status}
                    </td>
                    <td className="px-4 py-2 space-x-2">
                      <button
                        onClick={() => handleOpenModal(tutor)}
                        className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                        <PencilIcon className="h-4 w-4" />
                        <span>Edit</span>
                      </button>
                      <button
                        onClick={() => handleDelete(tutor.id)}
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
          <CreateTutorModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            tutor={selectedTutor}
          />
        </div>
      </div>
    </div>
  );
};
export default ManageTutors;
