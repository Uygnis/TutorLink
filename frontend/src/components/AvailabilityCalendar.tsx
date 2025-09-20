import { useState, useEffect } from "react";

export interface TimeSlot {
  enabled: boolean;
  start: string;
  end: string;
}

interface AvailabilityCalendarProps {
  availability: Record<string, TimeSlot>; // e.g., { Mon: {enabled: true, start: "10:00", end:"11:00"} }
  bookedSlots?: { date: string }[]; // [{ date: "2025-09-22" }]
  initialMonth?: Date;
  onSlotClick?: (date: Date, slot: TimeSlot) => void;
  onMonthChange?: (monthStart: Date) => void;
}

const AvailabilityCalendar = ({
  availability,
  bookedSlots = [],
  initialMonth,
  onSlotClick,
  onMonthChange,
}: AvailabilityCalendarProps) => {
  const [monthStart, setMonthStart] = useState<Date>(() => {
    if (initialMonth) return new Date(initialMonth.getFullYear(), initialMonth.getMonth(), 1);
    const today = new Date();
    return new Date(today.getFullYear(), today.getMonth(), 1);
  });

  useEffect(() => {
    onMonthChange?.(monthStart);
  }, [monthStart]);

  // Get all dates for the month
  const getMonthDates = (start: Date) => {
    const dates: Date[] = [];
    const year = start.getFullYear();
    const month = start.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    for (let d = firstDay.getDate(); d <= lastDay.getDate(); d++) {
      dates.push(new Date(year, month, d));
    }
    return dates;
  };

  const goPrevMonth = () =>
    setMonthStart((prev) => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  const goNextMonth = () =>
    setMonthStart((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));

  const monthDates = getMonthDates(monthStart);

  const weekdays = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

  const isSlotBooked = (date: Date) =>
    bookedSlots.some((b) => b.date === date.toISOString().split("T")[0]);

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-2">
        <button onClick={goPrevMonth} className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300">
          &lt;
        </button>
        <span className="font-bold text-lg">
          {monthStart.toLocaleString(undefined, { month: "long", year: "numeric" })}
        </span>
        <button onClick={goNextMonth} className="px-2 py-1 bg-gray-200 rounded hover:bg-gray-300">
          &gt;
        </button>
      </div>

      {/* Weekday Headers */}
      <div className="grid grid-cols-7 text-center font-bold border-b pb-1">
        {weekdays.map((day) => (
          <div key={day}>{day}</div>
        ))}
      </div>

      {/* Days */}
      <div className="grid grid-cols-7 gap-2 mt-2 text-center">
        {/* Empty cells for first week */}
        {Array(monthDates[0].getDay())
          .fill(null)
          .map((_, idx) => (
            <div key={`empty-${idx}`} className="p-2"></div>
          ))}

        {monthDates.map((date) => {
          const dayKey = date.toLocaleDateString(undefined, { weekday: "short" });
          const slot = availability?.[dayKey];

          const booked = isSlotBooked(date); // check booked regardless of availability
          const enabled = slot?.enabled ?? false; // true if available, false otherwise

          return (
            <div
              key={date.toISOString()}
              className={`p-2 border rounded text-sm cursor-pointer ${
                booked
                  ? "bg-red-200 text-red-700 cursor-not-allowed"
                  : enabled
                  ? "bg-green-100 hover:bg-green-200"
                  : "bg-gray-100 text-gray-400 cursor-not-allowed"
              }`}
              onClick={() =>
                enabled &&
                !booked &&
                onSlotClick?.(date, slot ?? { enabled: false, start: "", end: "" })
              }>
              <div className="font-semibold">{date.getDate()}</div>
              {slot?.enabled && <div className="text-xs">{`${slot.start} - ${slot.end}`}</div>}
              {!slot?.enabled && booked && <div className="text-xs">Booked</div>}
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default AvailabilityCalendar;
