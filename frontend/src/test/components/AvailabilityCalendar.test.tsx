import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import AvailabilityCalendar, { TimeSlot } from "@/components/AvailabilityCalendar";
import React from "react";

describe("AvailabilityCalendar", () => {
  const mockOnSlotClick = jest.fn();
  const mockOnMonthChange = jest.fn();

  const baseAvailability: Record<string, TimeSlot> = {
    Mon: { enabled: true, start: "10:00", end: "11:00" },
    Tue: { enabled: true, start: "11:00", end: "12:00" },
    Wed: { enabled: false, start: "09:00", end: "10:00" },
    Thu: { enabled: true, start: "13:00", end: "14:00" },
    Fri: { enabled: true, start: "15:00", end: "16:00" },
    Sat: { enabled: true, start: "08:00", end: "09:00" },
    Sun: { enabled: false, start: "09:00", end: "10:00" },
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders current month and weekday headers", () => {
    render(
      <AvailabilityCalendar role="student" availability={baseAvailability} bookedSlots={[]} />
    );

    const today = new Date();
    const monthName = today.toLocaleString(undefined, { month: "long", year: "numeric" });
    expect(screen.getByText(monthName)).toBeInTheDocument();

    ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].forEach((day) => {
      expect(screen.getByText(day)).toBeInTheDocument();
    });
  });

  test("calls onMonthChange when monthStart changes", async () => {
    render(
      <AvailabilityCalendar
        role="student"
        availability={baseAvailability}
        onMonthChange={mockOnMonthChange}
      />
    );

    await waitFor(() => expect(mockOnMonthChange).toHaveBeenCalledTimes(1));

    fireEvent.click(screen.getByText(">"));
    await waitFor(() => expect(mockOnMonthChange).toHaveBeenCalledTimes(2));
  });

  test("renders available slot and triggers onSlotClick", async () => {
    render(
      <AvailabilityCalendar
        role="student"
        availability={baseAvailability}
        onSlotClick={mockOnSlotClick}
        bookedSlots={[]}
      />
    );

    // Look for any displayed time (use regex for flexibility)
    const availableSlots = screen.queryAllByText(/10:00|11:00|13:00|15:00|08:00/);
    expect(availableSlots.length).toBeGreaterThan(0);

    const dayCell = availableSlots[0].closest("div");
    if (dayCell) fireEvent.click(dayCell);

    await waitFor(() => expect(mockOnSlotClick).toHaveBeenCalled());
  });

  test("renders tutor view with different classnames", () => {
    render(<AvailabilityCalendar role="tutor" availability={baseAvailability} bookedSlots={[]} />);

    const dayCells = screen.getAllByText(/^\d+$/);
    const firstCell = dayCells[0].closest("div");
    expect(firstCell?.className.includes("text-red-700")).toBe(false);
  });
});
