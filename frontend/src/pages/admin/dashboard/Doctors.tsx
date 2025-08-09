import { useEffect, useState } from "react";
import { PlusIcon, PencilIcon, TrashIcon } from "@heroicons/react/24/solid";
import { GetAllDoctors, DeleteDoctor } from "@/api/doctorAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import CreateDoctorModal from "@/components/CreateDoctorModal";
import { Doctor } from "@/types/DoctorType";

const Doctors = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [selectedDoctor, setSelectedDoctor] = useState<Doctor | null>(null);

  const { user } = useAppSelector((state) => state.user);

  const fetchDoctors = async () => {
    try {
      const token = user?.token;
      if (!token) return;

      const response = await GetAllDoctors(token);
      console.log("Doctors API Response:", response.data);
      setDoctors(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch doctors");
      console.error(error);
    }
  };

  const filteredDoctors = doctors.filter((doctor: any) =>
    doctor.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenModal = (doctor: any | null = null) => {
    setSelectedDoctor(doctor);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedDoctor(null);
    fetchDoctors(); // Refresh list
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this doctor?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteDoctor(id, token);
      toast.success("Doctor deleted successfully");
      fetchDoctors(); // Refresh the list
    } catch (error: any) {
      toast.error("Failed to delete doctor");
      console.error(error);
    }
  };

  useEffect(() => {
    fetchDoctors();
  }, []);

  return (
    <div>
      {/* Search Bar */}
      <input
        type="search"
        placeholder="Search doctors..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
      />

      <div className="flex justify-between items-center my-4">
        <h2 className="text-lg font-bold">List of Doctors</h2>
        <button
          onClick={() => handleOpenModal()}
          className="bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 flex items-center space-x-2">
          <PlusIcon className="h-5 w-5" />
          <span>Add Doctor</span>
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full table-auto border-collapse">
          <thead>
            <tr className="bg-gray-100 text-left text-sm font-medium text-gray-600">
              <th className="px-4 py-2">Name</th>
              <th className="px-4 py-2">DocID</th>
              <th className="px-4 py-2">Email</th>
              <th className="px-4 py-2">Status</th>
              <th className="px-4 py-2">Action</th>
            </tr>
          </thead>
          <tbody>
            {filteredDoctors.map((doctor: any) => (
              <tr key={doctor.id} className="border-b text-sm text-gray-700">
                <td className="px-4 py-2">{doctor.name}</td>
                <td className="px-4 py-2">{doctor.docId}</td>
                <td className="px-4 py-2">{doctor.email}</td>
                <td
                  className={`px-4 py-2 ${
                    doctor.status === "Active" ? "text-green-600" : "text-red-600"
                  }`}>
                  {doctor.status}
                </td>
                <td className="px-4 py-2 space-x-2">
                  <button
                    onClick={() => handleOpenModal(doctor)}
                    className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                    <PencilIcon className="h-4 w-4" />
                    <span>Edit</span>
                  </button>
                  <button
                    onClick={() => handleDelete(doctor.id)}
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
      <CreateDoctorModal isOpen={isModalOpen} onClose={handleCloseModal} doctor={selectedDoctor} />
    </div>
  );
};

export default Doctors;
