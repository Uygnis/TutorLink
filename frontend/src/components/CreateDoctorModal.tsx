import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import { CreateDoctor, UpdateDoctor } from "@/api/doctorAPI";
import { useAppSelector, useAppDispatch } from "@/redux/store";
import { setLoading } from "@/redux/loaderSlice";

type Props = {
  isOpen: boolean;
  onClose: () => void;
  doctor?: any | null;
};

const CreateDoctorModal = ({ isOpen, onClose, doctor }: Props) => {
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm();
  const { user } = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();

  useEffect(() => {
    if (doctor) {
      setValue("name", doctor.name);
      setValue("email", doctor.email);
      setValue("status", doctor.status);
    } else {
      reset(); // Clear form on create
    }
  }, [doctor, setValue, reset]);

  const onSubmit = async (data: any) => {
    try {
      dispatch(setLoading(true));
      const token = user?.token;
      if (!token) {
        throw new Error("Token is missing");
      }

      if (doctor) {
        // Edit mode
        await UpdateDoctor(doctor.id, data, token);
        toast.success("Doctor updated successfully");
      } else {
        // Create mode
        await CreateDoctor(data, token);
        toast.success("Doctor created successfully");
      }

      onClose();
    } catch (error: any) {
      toast.error("Operation failed");
    } finally {
      dispatch(setLoading(false));
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
      <div className="bg-white p-6 rounded-lg shadow-lg w-full max-w-md">
        <h2 className="text-xl font-semibold mb-4">{doctor ? "Edit Doctor" : "Add Doctor"}</h2>
        <form onSubmit={handleSubmit(onSubmit)}>
          {!doctor && (
            <>
              <input
                {...register("docId", { required: true })}
                className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
                placeholder="Doctor ID"
              />
              {errors.docId && <p className="text-sm text-red-500">Doctor ID is required</p>}
            </>
          )}

          <input
            {...register("name", { required: true })}
            className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
            placeholder="Full Name"
          />
          {errors.name && <p className="text-sm text-red-500">Name is required</p>}

          <input
            {...register("email", {
              required: true,
              pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            })}
            className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2"
            placeholder="Email"
          />
          {errors.email && <p className="text-sm text-red-500">Valid email is required</p>}

          <select
            {...register("status", { required: true })}
            className="bg-gray-200 px-3 py-2 rounded-md w-full mb-2">
            <option value="">Select Status</option>
            <option value="Active">Active</option>
            <option value="Inactive">Inactive</option>
          </select>
          {errors.status && <p className="text-sm text-red-500">Status is required</p>}

          <div className="flex justify-end gap-2 mt-4">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded bg-gray-300 hover:bg-gray-400">
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 rounded bg-primary text-white hover:bg-primary-dark">
              Submit
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateDoctorModal;
