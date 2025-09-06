import { useState } from "react";

export interface TimeSlot {
  enabled: boolean;
  start: string;
  end: string;
}

interface AvailabilityCalendarProps {
  availability: Record<string, TimeSlot>;
  initialWeekStart?: Date;
  onSlotClick?: (date: Date, slot: TimeSlot) => void;
}

const AvailabilityCalendar = ({
  availability,
  initialWeekStart,
  onSlotClick,
}: AvailabilityCalendarProps) => {
  const [weekStart, setWeekStart] = useState<Date>(() => {
    if (initialWeekStart) return initialWeekStart;
    const today = new Date();
    const dayOfWeek = today.getDay();
    const monday = new Date(today);
    monday.setDate(today.getDate() - ((dayOfWeek + 6) % 7)); // Monday as start of week
    return monday;
  });

  const getWeekDates = (start: Date) => {
    return Array.from({ length: 7 }, (_, i) => {
      const d = new Date(start);
      d.setDate(start.getDate() + i);
      return d;
    });
  };

  const formatMonthDateRange = (dates: Date[]) => {
    const options: Intl.DateTimeFormatOptions = { day: "numeric", month: "short" };
    return `${dates[0].toLocaleDateString(undefined, options)} - ${dates[6].toLocaleDateString(
      undefined,
      options
    )}`;
  };

  const goPrevWeek = () => {
    setWeekStart((prev) => {
      const newDate = new Date(prev); // clone
      newDate.setDate(prev.getDate() - 7);
      return newDate;
    });
  };

  const goNextWeek = () => {
    setWeekStart((prev) => {
      const newDate = new Date(prev); // clone
      newDate.setDate(prev.getDate() + 7);
      return newDate;
    });
  };

  const weekDates = getWeekDates(weekStart);

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-2">
        <button onClick={goPrevWeek} className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300">
          &lt;
        </button>
        <span className="font-bold">{formatMonthDateRange(weekDates)}</span>
        <button onClick={goNextWeek} className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300">
          &gt;
        </button>
      </div>

      {/* Day Headers */}
      <div className="grid grid-cols-7 gap-2 text-center font-bold border-b pb-1">
        {weekDates.map((date) => (
          <div key={date.toDateString()}>
            {date.toLocaleDateString(undefined, { weekday: "short" })} <br /> {date.getDate()}
          </div>
        ))}
      </div>

      {/* Time Slots */}
      <div className="grid grid-cols-7 gap-2 text-center mt-2">
        {weekDates.map((date) => {
          const dayKey = date.toLocaleDateString(undefined, { weekday: "short" });
          const slot = availability?.[dayKey];

          return (
            <div
              key={date.toDateString()}
              className={`p-2 border rounded text-sm cursor-pointer ${
                slot?.enabled
                  ? "bg-green-100 hover:bg-green-200"
                  : "bg-gray-100 text-gray-400 cursor-not-allowed"
              }`}
              onClick={() => slot?.enabled && onSlotClick?.(date, slot)}>
              {slot?.enabled ? `${slot.start} - ${slot.end}` : "-"}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default AvailabilityCalendar;
