import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { EyeIcon, PauseCircleIcon, PlayCircleIcon, TrashIcon } from "@heroicons/react/24/solid";
import { GetAllTutors, DeleteUser, ActivateUser, SuspendUser } from "@/api/adminAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";
import { Tutor } from "@/types/TutorSearchRequest";

const ManageTutors = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [, setIsModalOpen] = useState(false);
  const [tutors, setTutors] = useState<Tutor[]>([]);
  const [, setSelectedTutor] = useState<Tutor | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const currentPermissions: string[] = user?.permissions || [];

  const fetchTutors = async () => {
    try {
      const token = user?.token;
      if (!token) return;

      const response = await GetAllTutors(user.id, token);
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

  // const handleCloseModal = () => {
  //   setIsModalOpen(false);
  //   setSelectedTutor(null);
  //   fetchTutors(); // Refresh list
  // };

  const handleSuspendModal = async (tutor: any) => {
    const isSuspended = tutor.status === "SUSPENDED";
    const action = isSuspended ? "activate" : "suspend";

    if (!confirm(`Are you sure you want to ${action} this tutor?`)) return;

    try {
      const token = user?.token;
      if (!token) return;

      if (isSuspended) {
        // Tutor is suspended → call activate API
        await ActivateUser(user?.id, tutor.id, token, "TUTOR");
        toast.success("Tutor activated successfully");
      } else {
        // Tutor is active → call suspend API
        await SuspendUser(user?.id, tutor.id, token, "TUTOR");
        toast.success("Tutor suspended successfully");
      }

      fetchTutors(); // refresh list
    } catch (error: any) {
      toast.error(`Failed to ${action} tutor`);
      console.error(error);
    }
  };

  const handleDelete = async (tutorId: number) => {
    if (!confirm("Are you sure you want to delete this tutor?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteUser(user.id, tutorId, token, "TUTOR");
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
                      className={`px-4 py-2 ${tutor.status === "ACTIVE"
                          ? "text-green-600"
                          : tutor.status === "SUSPENDED"
                            ? "text-red-600"
                          : "text-gray-600"
                        }`}
                    >
                      {tutor.status}
                    </td>
                    <td className="px-4 py-2 space-x-2">
                      <button
                        onClick={() => handleOpenModal(tutor)}
                        className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                        <EyeIcon className="h-4 w-4" />
                        <span>View</span>
                      </button>
                      <div className="relative group inline-block">
                        <button
                          onClick={() => currentPermissions.includes("SUSPEND_TUTOR") && handleSuspendModal(tutor)}
                          disabled={!currentPermissions.includes("SUSPEND_TUTOR") || tutor.status === "DELETED"}
                          className={`px-3 py-1 rounded-md backdrop-blur-sm transition inline-flex items-center space-x-1
                          ${!currentPermissions.includes("SUSPEND_TUTOR") || tutor.status === "DELETED"
                              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                              : tutor.status === "SUSPENDED"
                                ? "bg-green-100 text-green-700 hover:bg-green-200"
                                : "bg-orange-100 text-orange-700 hover:bg-orange-200"
                            }`}
                        >
                          {tutor.status === "SUSPENDED" ? (
                            <PlayCircleIcon className="h-4 w-4" />
                          ) : (
                            <PauseCircleIcon className="h-4 w-4" />
                          )}
                          <span>{tutor.status === "SUSPENDED" ? "Activate" : "Suspend"}</span>
                        </button>

                        {/* Tooltip if no permission */}
                        {!currentPermissions.includes("SUSPEND_TUTOR") && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You do not have permission to suspend tutor
                          </div>
                        )}
                        {tutor.status === "DELETED" && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot suspend a deleted tutor
                          </div>
                        )}
                      </div>

                      <div className="relative group inline-block">
                        <button
                          onClick={() => currentPermissions.includes("DELETE_TUTOR") && handleDelete(tutor.id)}
                          disabled={!currentPermissions.includes("DELETE_TUTOR") || tutor.status !== "SUSPENDED"}
                          className={`px-3 py-1 rounded-md backdrop-blur-sm transition inline-flex items-center space-x-1
                          ${!currentPermissions.includes("DELETE_TUTOR")
                              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                              : tutor.status !== "SUSPENDED"
                                ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                                : "bg-red-100 text-red-700 hover:bg-red-200"
                            }`}
                        >
                          <TrashIcon className="h-4 w-4" />
                          <span>Delete</span>
                        </button>

                        {/* Tooltip */}
                        {!currentPermissions.includes("DELETE_TUTOR") ? (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You do not have permission to delete tutor
                          </div>
                        ) : tutor.status === "ACTIVE" ? (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot delete an active tutor
                          </div>
                        ) : null}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
export default ManageTutors;
