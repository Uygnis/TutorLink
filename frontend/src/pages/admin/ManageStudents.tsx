import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { PlusIcon, PencilIcon, TrashIcon } from "@heroicons/react/24/solid";
import { GetAllStudents, DeleteUser } from "@/api/adminAPI";
import { toast } from "react-toastify";
import { useAppSelector } from "@/redux/store";

const ManageStudents = () => {
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [students, setStudents] = useState<StudentDetails[]>([]);
  const [selectedStudent, setSelectedStudent] = useState<StudentDetails | null>(null);

  const { user } = useAppSelector((state) => state.user);

  const fetchStudents = async () => {
    try {
      const token = user?.token;
      if (!token) return;

      const response = await GetAllStudents(token);
      console.log("Admin API Response:", response.data);
      setStudents(response.data);
    } catch (error: any) {
      toast.error("Failed to fetch students");
      console.error(error);
    }
  };

  const filteredStudents = students.filter((student: any) =>
    student.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleOpenModal = (student: any | null = null) => {
    setSelectedStudent(student);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedStudent(null);
    fetchStudents(); // Refresh list
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Are you sure you want to delete this student?")) return;

    try {
      const token = user?.token;
      if (!token) return;

      await DeleteUser(id, token);
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
            <button
              onClick={() => handleOpenModal()}
              className="bg-primary text-white px-4 py-2 rounded hover:bg-opacity-90 flex items-center space-x-2">
              <PlusIcon className="h-5 w-5" />
              <span>Add Student</span>
            </button>
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
                    <td className="px-4 py-2">{student.student.studentNumber}</td>
                    <td className="px-4 py-2">{student.student.gradeLevel}</td>
                    <td
                      className={`px-4 py-2 ${
                        student.status === "Active" ? "text-green-600" : "text-red-600"
                      }`}>
                      {student.status}
                    </td>
                    <td className="px-4 py-2 space-x-2">
                      <button
                        onClick={() => handleOpenModal(student)}
                        className="bg-blue-100 text-blue-700 px-3 py-1 rounded-md backdrop-blur-sm hover:bg-blue-200 transition inline-flex items-center space-x-1">
                        <PencilIcon className="h-4 w-4" />
                        <span>Edit</span>
                      </button>
                      <button
                        onClick={() => handleDelete(student.id)}
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
