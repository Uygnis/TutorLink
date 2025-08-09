import { Link } from "react-router-dom";
import { CloudIcon } from "@heroicons/react/24/solid";
type Props = {};

const Home = (props: Props) => {
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
            <h1 className="font-bold text-xl">Welcome</h1>
            <p className="text-sm text-gray-500">Please Login or Register</p>
          </div>

          <div className="flex flex-col w-full">
            <Link
              to={"/login"}
              className="text-center mt-3 rounded-lg bg-primary text-white w-full py-2 transition duration-500 hover:bg-gray-200 hover:text-primary ">
              Login
            </Link>
            <Link
              to={"/register"}
              className="text-center mt-3 rounded-lg bg-primary text-white w-full  py-2 transition duration-500 hover:bg-gray-200 hover:text-primary ">
              Register
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;
