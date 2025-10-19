import React, { useState } from "react";

export interface BookingCardProps {
  id: string;
  tutorId?: string;
  studentId?: string;
  tutorName: string;
  studentName?: string;
  date: string;
  start: string;
  end: string;
  status: "confirmed" | "pending" | "cancelled";
  lessonType: string;
  isPastSession?: boolean;
  onClick?: (id: string) => void; // optional click handler
  onCancel?: (id: string) => void; // NEW: cancel handler
}

const BookingCard: React.FC<BookingCardProps> = ({
  id,
  tutorName,
  date,
  start,
  end,
  lessonType,
  status,
  isPastSession = false,
  onClick,
  onCancel,
}) => {
  const [isHovered, setIsHovered] = useState(false);

  const statusColor = {
    confirmed: "bg-green-100 text-green-800",
    pending: "bg-yellow-100 text-yellow-800",
    cancelled: "bg-red-100 text-red-800",
  } as const;

  const handleCancelClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // prevent triggering onClick
    if (onCancel) onCancel(id);
  };

  const isPendingHover = status === "pending" && isHovered;

  return (
    <div
      onClick={() => onClick && onClick(id)}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      className={`relative flex items-center justify-between border rounded-md p-4 shadow-sm transition cursor-pointer ${
        isPendingHover ? "bg-red-100" : "bg-white hover:shadow-md"
      }`}>
      {/* Left: Date */}
      <div className="text-center pr-4 border-r">
        <p className={`uppercase ${isPastSession ? "text-xs" : "text-sm"} text-gray-500`}>Mar</p>
        <p className={`font-bold ${isPastSession ? "text-lg" : "text-2xl"}`}>
          {new Date(date).getDate()}
        </p>
        <p className={`${isPastSession ? "text-xs" : "text-sm"} text-gray-500`}>
          {new Date(date).toLocaleDateString(undefined, { weekday: "short", year: "numeric" })}
        </p>
      </div>

      {/* Middle: Main Info */}
      <div className="flex-1 px-4">
        <p className={`${isPastSession ? "text-sm" : "text-lg"} font-semibold`}>{lessonType}</p>
        <p className={`${isPastSession ? "text-xs" : "text-sm"} text-gray-600`}>
          {start} - {end} SG
        </p>
        <p className={`${isPastSession ? "text-xs" : "text-sm"} text-gray-600`}>{tutorName}</p>
      </div>

      {/* Right: Actions / Status */}
      {!isPastSession && !isPendingHover && (
        <div className="flex flex-col items-end gap-1">
          <span className={`px-2 py-1 rounded-md text-LG font-semibold ${statusColor[status]}`}>
            {status.charAt(0).toUpperCase() + status.slice(1)}
          </span>
        </div>
      )}

      {/* Cancel button overlay for pending sessions */}
      {isPendingHover && (
        <button
          onClick={handleCancelClick}
          className="absolute inset-0 flex items-center justify-center bg-red-500/70 text-white font-bold rounded-md backdrop-blur-sm">
          Cancel
        </button>
      )}
    </div>
  );
};

export default BookingCard;
