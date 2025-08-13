import { useForm } from "react-hook-form";
import { CloudIcon } from "@heroicons/react/24/solid";
import { useNavigate, Link } from "react-router-dom";
import { useAppDispatch } from "@/redux/store";
import { LoginUser } from "@/api/userAPI";
import { setLoading } from "@/redux/loaderSlice";
import { setUser } from "@/redux/userSlice";
import { toast } from "react-toastify";

const Login = () => {
  const {
    register,
    trigger,
    getValues,
    formState: { errors },
  } = useForm();

  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  //Func: Handle form submission
  const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const { email, password } = getValues();
    const isValid = await trigger();

    if (!isValid) return;

    try {
      dispatch(setLoading(true));

      const response = await LoginUser(email, password);
      dispatch(setLoading(false));
      const { user } = response.data;

      // Store user data and token in local storage
      localStorage.setItem("user", JSON.stringify(user));

      toast.success(response.data.message);

      // Update redux
      dispatch(setUser(user));

      // Redirect based on role
      if (user.role === "ADMIN") {
        navigate("/admin/dashboard");
      } else if (user.role === "STUDENT") {
        navigate("/student/dashboard");
      } else {
        navigate("/user/dashboard");
      }
    } catch (error: any) {
      dispatch(setLoading(false));
      console.log(error);
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
            <h1 className="font-bold text-xl">Login</h1>
            <p className="text-sm text-gray-500">You will be redirected to the homepage</p>
          </div>
          {/* Login Form */}
          <form onSubmit={onSubmit} method="POST">
            {/* Input Fields */}
            <input
              className="bg-gray-200 px-2 py-1 rounded-md w-full"
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

            {/* Submit Button */}
            <button
              type="submit"
              className="mt-3 rounded-lg bg-primary text-white w-full px-20 py-2 transition duration-500 hover:bg-gray-200 hover:text-primary ">
              Submit
            </button>
          </form>
          {/* Register Link */}
          <div className="mt-3 text-sm">
            Dont have an account ?{" "}
            <Link className="text-primary" to="/register">
              Register Now!
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
