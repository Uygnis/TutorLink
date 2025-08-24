import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { RegisterUser } from "@/api/userAPI";
import { setLoading } from "@/redux/loaderSlice";
import { useAppDispatch } from "@/redux/store";
import { toast } from "react-toastify";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  tutor?: any;
};

const CreateTutorModal = ({ isOpen, onClose, tutor }: Props) => {
  const {
    register,
    trigger,
    getValues,
    setValue,
    reset,
    formState: { errors },
  } = useForm();

  const dispatch = useAppDispatch();
  //const { user } = useAppSelector((state) => state.user);

  useEffect(() => {
    if (tutor) {
      setValue("email", tutor.email);
    } else {
      reset();
    }
  }, [tutor, setValue, reset]);

  const onSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const isValid = await trigger();
    if (!isValid) return;

    const { firstname, lastname, email, password } = getValues();

    try {
      dispatch(setLoading(true));
      //const token = user?.token;
      let response;

      // if (tutor) {
      //   response = await UpdateTutor(tutor.id, { email, password }, token);
      // } else {
      response = await RegisterUser({
          firstname,
          lastname,
          email,
          password,
          role: "user",
        });
      // }

      dispatch(setLoading(false));

      if (response.status === 200) {
        toast.success(response.data.message);
        onClose();
      } else {
        toast.error(response.data.message);
      }
    } catch (error: any) {
      dispatch(setLoading(false));
      toast.error(error.message);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
        <h2 className="text-xl font-semibold mb-4">{tutor ? "Edit Tutor" : "Add New Tutor"}</h2>
        <form onSubmit={onSubmit}>
          {!tutor && (
            <>
              <input
                className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
                type="text"
                placeholder="First Name"
                {...register("firstname", { required: true, maxLength: 100 })}
              />
              {errors.firstname && <p className="text-sm text-red-500">First name is required</p>}

              <input
                className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
                type="text"
                placeholder="Last Name"
                {...register("lastname", { required: true, maxLength: 100 })}
              />
              {errors.lastname && <p className="text-sm text-red-500">Last name is required</p>}
            </>
          )}

          <input
            className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
            type="text"
            placeholder="Email"
            {...register("email", {
              required: true,
              pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            })}
          />
          {errors.email && <p className="text-sm text-red-500">Valid email is required</p>}

          <input
            className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
            type="password"
            placeholder="Password"
            {...register("password", { required: !tutor })}
          />
          {errors.password && <p className="text-sm text-red-500">Password is required</p>}

          <div className="flex justify-end space-x-2 mt-4">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded bg-gray-300 hover:bg-gray-400">
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded bg-primary text-white hover:bg-primary-dark">
              {tutor ? "Update" : "Submit"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateTutorModal;
