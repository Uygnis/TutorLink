import { useForm } from "react-hook-form";
import { CloudIcon } from "@heroicons/react/24/solid";
import { Link } from "react-router-dom";
import { useAppDispatch } from "@/redux/store";
import { RegisterUser } from "@/api/userAPI";
import { setLoading } from "@/redux/loaderSlice";
import { toast } from "react-toastify";
import { useState, useEffect } from "react";
import { getNextStudentId } from "@/api/sequenceAPI";

const Register = () => {
  const [selectedRole, setSelectedRole] = useState("");
  const [studentNumber, setStudentNumber] = useState("");

  const {
    register,
    trigger,
    getValues,
    formState: { errors },
  } = useForm();

  const dispatch = useAppDispatch();

  //Func: Handle form submission
  const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const isValid = await trigger();
    if (!isValid) return;

    const {
      firstname,
      lastname,
      email,
      password,
      role,
      gradeLevel,
      permissions,
      subject,
    } = getValues();

    try {
      dispatch(setLoading(true));
      const response = await RegisterUser({
        firstname,
        lastname,
        email,
        password,
        role,
        // Only send student fields if role is STUDENT
        ...(role === "STUDENT" && { studentNumber, gradeLevel }),
        ...(role === "ADMIN" && { permissions }),
        ...(role === "TUTOR" && { subject }),
      });
      dispatch(setLoading(false));

      if (response.status === 200) {
        toast.success(response.data.message);
      } else {
        toast.error(response.data.message);
      }
    } catch (error: any) {
      dispatch(setLoading(false));
      toast.error(error.message);
    }
  };

  useEffect(() => {
    if (selectedRole === "STUDENT") {
      getNextStudentId()
        .then((id) => setStudentNumber(id))
        .catch((err) => console.error(err));
    }
  }, [selectedRole]);

  return (
    <div className="h-screen bg-primary flex items-center justify-center p-5 overflow-hidden">
      {/* Container */}
      <div className="flex flex-col items-center">
        <div className="bg-white h-full w-[400px] rounded-md p-5">
          {/* Header */}
          <div className="mb-5">
            <Link to={"/"}>
              <CloudIcon className="h-6 w-6 text-gray-400" />
            </Link>
            <h1 className="font-bold text-xl">Register</h1>
            <p className="text-sm text-gray-500">
              You will be redirected to the login page
            </p>
          </div>
          {/* Register Form */}
          <form onSubmit={onSubmit} method="POST">
            {/* Input Fields */}
            <input
              className="bg-gray-200 px-2 py-1 rounded-md w-full"
              type="text"
              placeholder="First Name"
              {...register("firstname", {
                required: true,
                maxLength: 100,
              })}
            />
            {errors.firstname && (
              <p className="mt-1 text-red-500 text-sm">
                {errors.firstname.type === "required" &&
                  "This field is required."}
                {errors.firstname.type === "maxLength" &&
                  "Max length is 100 char."}
              </p>
            )}
            <input
              className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
              type="text"
              placeholder="Last Name"
              {...register("lastname", {
                required: true,
                maxLength: 100,
              })}
            />
            {errors.lastname && (
              <p className="mt-1 text-red-500 text-sm">
                {errors.lastname.type === "required" &&
                  "This field is required."}
                {errors.lastname.type === "maxLength" &&
                  "Max length is 100 char."}
              </p>
            )}
            <input
              className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
              type="text"
              placeholder="Email"
              {...register("email", {
                required: true,
                pattern: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
              })}
            />
            {errors.email && (
              <p className="mt-1 text-red-500 text-sm">
                {errors.email.type === "required" && "This field is required."}
                {errors.email.type === "pattern" && "Invalid email address."}
              </p>
            )}
            <input
              className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
              type="password"
              placeholder="Password"
              {...register("password", {
                required: true,
              })}
            />
            {errors.password && (
              <p className="mt-1 text-red-500 text-sm">
                {errors.password.type === "required" &&
                  "This field is required."}
              </p>
            )}

            {/* Role dropdown */}
            <select
              className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
              {...register("role", { required: true })}
              defaultValue=""
              onChange={(e) => {
                setSelectedRole(e.target.value);
              }}
            >
              <option value="" disabled>
                Select role
              </option>
              <option value="ADMIN">Admin</option>
              <option value="STUDENT">Student</option>
              <option value="TUTOR">Tutor</option>
            </select>
            {errors.role && (
              <p className="mt-1 text-red-500 text-sm">Role is required.</p>
            )}

            {/* Conditionally render student-specific fields */}
            {selectedRole === "STUDENT" && (
              <>
                <input
                  className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
                  type="text"
                  placeholder="Student Number"
                  value={studentNumber}
                  readOnly
                />

                {/* Hidden registered field */}
                <input
                  type="hidden"
                  value={studentNumber}
                  {...register("studentNumber")}
                />

                <select
                  className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
                  {...register("gradeLevel", { required: true })}
                  defaultValue=""
                >
                  <option value="" disabled>
                    Select Grade Level
                  </option>
                  <option value="Primary School">Primary School</option>
                  <option value="Secondary School">Secondary School</option>
                  <option value="Polytechnic">Polytechnic</option>
                  <option value="JC">JC</option>
                </select>
                {errors.gradeLevel && (
                  <p className="mt-1 text-red-500 text-sm">
                    Grade Level is required.
                  </p>
                )}
              </>
            )}

            {/* Conditionally render admin-specific permissions */}
            {selectedRole === "ADMIN" && (
              <div className="mt-3">
                <label className="font-semibold">Admin Permissions:</label>

                <div className="grid grid-cols-2 gap-6 mt-4">
                  {/* Left Column */}
                  <div className="space-y-6">
                    {/* Student Management */}
                    <div>
                      <p className="font-medium text-gray-700">
                        Student Management
                      </p>
                      <div className="mt-2 space-y-2">
                        <label className="flex items-center space-x-2">
                          <input
                            type="checkbox"
                            value="VIEW_STUDENTS"
                            {...register("permissions")}
                          />
                          <span>View Students</span>
                        </label>
                        <label className="flex items-center space-x-2">
                          <input
                            type="checkbox"
                            value="SUSPEND_STUDENT"
                            {...register("permissions")}
                          />
                          <span>Suspend Student</span>
                        </label>
                      </div>
                    </div>

                    {/* Admin Management (stacked under student) */}
                    <div>
                      <p className="font-medium text-gray-700">
                        Admin Management
                      </p>
                      <div className="mt-2 space-y-2">
                        <label className="flex items-center space-x-2">
                          <input
                            type="checkbox"
                            value="VIEW_ADMIN"
                            {...register("permissions")}
                          />
                          <span>View Admins</span>
                        </label>
                        <label className="flex items-center space-x-2">
                          <input
                            type="checkbox"
                            value="CREATE_ADMIN"
                            {...register("permissions")}
                          />
                          <span>Create Admin</span>
                        </label>
                      </div>
                    </div>
                  </div>

                  {/* Right Column (Tutor Management) */}
                  <div>
                    <p className="font-medium text-gray-700">
                      Tutor Management
                    </p>
                    <div className="mt-2 space-y-2">
                      <label className="flex items-center space-x-2">
                        <input
                          type="checkbox"
                          value="VIEW_TUTORS"
                          {...register("permissions")}
                        />
                        <span>View Tutors</span>
                      </label>
                      <label className="flex items-center space-x-2">
                        <input
                          type="checkbox"
                          value="APPROVE_TUTOR"
                          {...register("permissions")}
                        />
                        <span>Approve Tutor</span>
                      </label>
                      <label className="flex items-center space-x-2">
                        <input
                          type="checkbox"
                          value="REJECT_TUTOR"
                          {...register("permissions")}
                        />
                        <span>Reject Tutor</span>
                      </label>
                      <label className="flex items-center space-x-2">
                        <input
                          type="checkbox"
                          value="SUSPEND_TUTOR"
                          {...register("permissions")}
                        />
                        <span>Suspend Tutor</span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Conditionally render tutor-specific fields */}
            {selectedRole === "TUTOR" && (
              <>
                {/* Hidden registered field */}
                <select
                  className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
                  {...register("subject", { required: true })}
                  defaultValue=""
                >
                  <option value="" disabled>
                    Select Subject
                  </option>
                  <option value="English">English</option>
                  <option value="Mathematics">Mathematics</option>
                  <option value="Biology">Biology</option>
                  <option value="Chemistry">Chemistry</option>
                  <option value="Physics">Physics</option>
                  <option value="Geography">Geography</option>
                  <option value="History">History</option>
                  <option value="Literature">Literature</option>
                </select>
                {errors.subject && (
                  <p className="mt-1 text-red-500 text-sm">
                    Subject is required.
                  </p>
                )}
              </>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              className="mt-3 rounded-lg bg-primary text-white w-full px-20 py-2 transition duration-500 hover:bg-gray-200 hover:text-primary "
            >
              Submit
            </button>
          </form>
          {/* Register Link */}
          <div className="mt-3 text-sm">
            Have an account already?{" "}
            <Link className="text-primary" to="/login">
              Login Now!
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
