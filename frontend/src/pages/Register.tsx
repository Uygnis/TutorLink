import { useForm } from "react-hook-form";
import { CloudIcon } from "@heroicons/react/24/solid";
import { Link } from "react-router-dom";
import { useAppDispatch } from "@/redux/store";
import { RegisterUser } from "@/api/userAPI";
import { setLoading } from "@/redux/loaderSlice";
import { toast } from "react-toastify";

type Props = {};

const Register = (props: Props) => {
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
    const { firstname, lastname, email, password, role } = getValues();
    const isValid = await trigger();

    if (!isValid) return;

    try {
      dispatch(setLoading(true));
      const response = await RegisterUser(firstname, lastname, email, password, role);
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
            <p className="text-sm text-gray-500">You will be redirected to the login page</p>
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
                {errors.firstname.type === "required" && "This field is required."}
                {errors.firstname.type === "maxLength" && "Max length is 100 char."}
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
                {errors.lastname.type === "required" && "This field is required."}
                {errors.lastname.type === "maxLength" && "Max length is 100 char."}
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
                {errors.password.type === "required" && "This field is required."}
              </p>
            )}

            {/* Role dropdown */}
            <select
              className="mt-3 bg-gray-200 px-2 py-1 rounded-md w-full"
              {...register("role", { required: true })}
              defaultValue="">
              <option value="" disabled>
                Select role
              </option>
              <option value="ADMIN">Admin</option>
              <option value="STUDENT">Student</option>
              <option value="TUTOR">Tutor</option>
            </select>
            {errors.role && <p className="mt-1 text-red-500 text-sm">Role is required.</p>}

            {/* Submit Button */}
            <button
              type="submit"
              className="mt-3 rounded-lg bg-primary text-white w-full px-20 py-2 transition duration-500 hover:bg-gray-200 hover:text-primary ">
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
