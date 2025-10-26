import { useNavigate } from "react-router-dom";
import Navbar from "@/components/Navbar";

const WalletCancelPage = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-[#f2f2f2]">
      <Navbar />
      <div className="max-w-xl mx-auto p-6 mt-10 bg-white shadow-md rounded-xl text-center">
        <h1 className="text-2xl font-bold mb-2 text-yellow-600">⚠️ Payment Cancelled</h1>
        <p className="text-gray-600 mb-6">
          Your payment was not completed. No credits have been added to your wallet.
        </p>

        <div className="flex justify-center gap-4">
          <button
            onClick={() => navigate("/student/wallet?status=cancelled")}
            className="px-4 py-2 bg-gray-300 text-black rounded-md hover:bg-gray-400 transition">
            Back to Wallet
          </button>
          <button
            onClick={() => navigate("/student/dashboard")}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition">
            Go to Dashboard
          </button>
        </div>
      </div>
    </div>
  );
};

export default WalletCancelPage;
