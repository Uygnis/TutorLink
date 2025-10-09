import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { EyeIcon, PauseCircleIcon, PlayCircleIcon, TrashIcon } from "@heroicons/react/24/solid";
import { GetAllStudents, DeleteUser, SuspendUser, ActivateUser } from "@/api/adminAPI";
import { toast } from "react-toastify";
import { useAppDispatch, useAppSelector } from "@/redux/store";
import { setLoading } from "@/redux/loaderSlice";

const ManageStudents = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [, setIsModalOpen] = useState(false);
  const [students, setStudents] = useState<StudentDetails[]>([]);
  const [, setSelectedStudent] = useState<StudentDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);
  const currentPermissions: string[] = user?.permissions || [];
  const { loading } = useAppSelector((state) => state.loaders);
  const dispatch = useAppDispatch();

  const fetchStudents = async () => {
    try {
      dispatch(setLoading(true));
      const token = user?.token;
      if (!token) return;

      const response = await GetAllStudents(user.id, token);
      console.log("Admin API Response:", response.data);
      setStudents(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch students");
      console.error(error);
    } finally {
      dispatch(setLoading(false));
    }
  };

  const filteredStudents = students.filter((student: any) =>
    student.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenModal = (student: any | null = null) => {
    setSelectedStudent(student);
    setIsModalOpen(true);
  };

  const handleSuspendModal = async (student: any) => {
    const isSuspended = student.status === "SUSPENDED";
    const action = isSuspended ? "activate" : "suspend";

    if (!confirm(`Are you sure you want to ${action} this student?`)) return;

    try {
      const token = user?.token;
      if (!token) return;

      if (isSuspended) {
        // student is suspended → call activate API
        await ActivateUser(user?.id, student.id, token, "STUDENT");
        toast.success("Student activated successfully");
      } else {
        // student is active → call suspend API
        await SuspendUser(user?.id, student.id, token, "STUDENT");
        toast.success("Student suspended successfully");
      }

      fetchStudents(); // refresh list
    } catch (error: any) {
      toast.error(`Failed to ${action} student`);
      console.error(error);
    }
  };

  // const handleCloseModal = () => {
  //   setIsModalOpen(false);
  //   setSelectedStudent(null);
  //   fetchStudents(); // Refresh list
  // };

  const handleDelete = async (studentId: number) => {
    if (!confirm("Are you sure you want to delete this student?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteUser(user.id, studentId, token, "STUDENT");
      toast.success("Student deleted successfully");
      fetchStudents(); // Refresh the list
    } catch (error: any) {
      toast.error("Failed to delete student");
      console.error(error);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, []);


  return (
    <div>
      <Navbar />

      <div className="p-6">
        <div>
          {/* Search Bar */}
          <input
            type="search"
            placeholder="Search students..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary"
          />

          <div className="flex justify-between items-center my-4">
            <h2 className="text-lg font-bold">List of Students</h2>
          </div>

          <div className="overflow-x-auto">
            <table className="min-w-full table-auto border-collapse">
              <thead>
                <tr className="bg-gray-100 text-left text-sm font-medium text-gray-600">
                  <th className="px-4 py-2">Name</th>
                  <th className="px-4 py-2">Email</th>
                  <th className="px-4 py-2">Student No.</th>
                  <th className="px-4 py-2">Grade Level</th>
                  <th className="px-4 py-2">Status</th>
                  <th className="px-4 py-2">Action</th>
                </tr>
              </thead>
              <tbody>
                {filteredStudents.map((student: any) => (
                  <tr key={student.id} className="border-b text-sm text-gray-700">
                    <td className="px-4 py-2">{student.name}</td>
                    <td className="px-4 py-2">{student.email}</td>
                    <td className="px-4 py-2">{student.student?.studentNumber}</td>
                    <td className="px-4 py-2">{student.student?.gradeLevel}</td>
                    <td
                      className={`px-4 py-2 ${student.status === "ACTIVE"
                        ? "text-green-600"
                        : student.status === "SUSPENDED"
                          ? "text-red-600"
                          : "text-gray-600"
                        }`}
                    >
                      {student.status}
                    </td>
                    <td className="px-4 py-2 space-x-2">
                      <button
                        onClick={() => handleOpenModal(student)}
                        className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                        <EyeIcon className="h-4 w-4" />
                        <span>View</span>
                      </button>
                      <div className="relative group inline-block">
                        <button
                          onClick={() => currentPermissions.includes("SUSPEND_STUDENT") && handleSuspendModal(student)}
                          disabled={!currentPermissions.includes("SUSPEND_STUDENT") || student.status === "DELETED"}
                          className={`px-3 py-1 rounded-md backdrop-blur-sm transition inline-flex items-center space-x-1
                          ${!currentPermissions.includes("SUSPEND_STUDENT") || student.status === "DELETED"
                              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                              : student.status === "SUSPENDED"
                                ? "bg-green-100 text-green-700 hover:bg-green-200"
                                : "bg-orange-100 text-orange-700 hover:bg-orange-200"
                            }`}
                        >
                          {student.status === "SUSPENDED" ? (
                            <PlayCircleIcon className="h-4 w-4" />
                          ) : (
                            <PauseCircleIcon className="h-4 w-4" />
                          )}
                          <span>{student.status === "SUSPENDED" ? "Activate" : "Suspend"}</span>
                        </button>

                        {/* Tooltip if no permission */}
                        {!currentPermissions.includes("SUSPEND_STUDENT") && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You do not have permission to suspend students
                          </div>
                        )}
                        {student.status === "DELETED" && (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot suspend a deleted student
                          </div>
                        )}
                      </div>

                      <div className="relative group inline-block">
                        <button
                          onClick={() => currentPermissions.includes("DELETE_STUDENT") && handleDelete(student.id)}
                          disabled={!currentPermissions.includes("DELETE_STUDENT") || student.status !== "SUSPENDED"}
                          className={`px-3 py-1 rounded-md backdrop-blur-sm transition inline-flex items-center space-x-1
                          ${!currentPermissions.includes("DELETE_STUDENT")
                              ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                              : student.status !== "SUSPENDED"
                                ? "bg-gray-200 text-gray-400 cursor-not-allowed"
                                : "bg-red-100 text-red-700 hover:bg-red-200"
                            }`}
                        >
                          <TrashIcon className="h-4 w-4" />
                          <span>Delete</span>
                        </button>

                        {/* Tooltip */}
                        {!currentPermissions.includes("DELETE_STUDENT") ? (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You do not have permission to delete students
                          </div>
                        ) : student.status === "ACTIVE" ? (
                          <div className="absolute -top-8 left-1/2 -translate-x-1/2 whitespace-nowrap 
                    bg-gray-700 text-white text-xs px-2 py-1 rounded opacity-0 
                    group-hover:opacity-100 transition">
                            You cannot delete an active student
                          </div>
                        ) : null}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {/* <CreateStudentModal
            isOpen={isModalOpen}
            onClose={handleCloseModal}
            student={selectedStudent}
          /> */}
        </div>
      </div>
    </div>
  );
};
export default ManageStudents;
