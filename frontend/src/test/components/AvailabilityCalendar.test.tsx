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

  it("renders current month and weekday headers", () => {
    render(<AvailabilityCalendar role="student" availability={baseAvailability} bookedSlots={[]} />);

    const today = new Date();
    const monthName = today.toLocaleString(undefined, { month: "long", year: "numeric" });
    expect(screen.getByText(monthName)).toBeInTheDocument();

    ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].forEach((day) => {
      expect(screen.getByText(day)).toBeInTheDocument();
    });
  });

  it("calls onMonthChange when month changes", async () => {
    render(
      <AvailabilityCalendar
        role="student"
        availability={baseAvailability}
        onMonthChange={mockOnMonthChange}
      />
    );

    await waitFor(() => expect(mockOnMonthChange).toHaveBeenCalledTimes(1));

    // Click next month
    fireEvent.click(screen.getByText(">"));
    await waitFor(() => expect(mockOnMonthChange).toHaveBeenCalledTimes(2));

    // Click previous month
    fireEvent.click(screen.getByText("<"));
    await waitFor(() => expect(mockOnMonthChange).toHaveBeenCalledTimes(3));
  });

  it("shows loading spinner initially and then hides it after bookedSlots load", async () => {
    const { rerender } = render(
      <AvailabilityCalendar role="student" availability={baseAvailability} bookedSlots={[]} />
    );

    // Should show spinner first
    expect(screen.getByRole("status", { hidden: true }) || screen.getByText(/spin/i)).toBeTruthy;

    rerender(
      <AvailabilityCalendar
        role="student"
        availability={baseAvailability}
        bookedSlots={[{ date: "2025-11-01", status: "confirmed" }]}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText(/spin/i)).not.toBeInTheDocument();
    });
  });

  it("renders available slot and triggers onSlotClick for student", async () => {
    render(
      <AvailabilityCalendar
        role="student"
        availability={baseAvailability}
        onSlotClick={mockOnSlotClick}
        bookedSlots={[]}
      />
    );

    const availableSlots = screen.queryAllByText(/10:00|11:00|13:00|15:00|08:00/);
    expect(availableSlots.length).toBeGreaterThan(0);

    const firstSlotCell = availableSlots[0].closest("div");
    if (firstSlotCell) fireEvent.click(firstSlotCell);

    await waitFor(() => expect(mockOnSlotClick).toHaveBeenCalled());
  });

  it("prevents clicking past or disabled slots", async () => {
    const pastDate = new Date();
    pastDate.setDate(pastDate.getDate() - 5);

    render(
      <AvailabilityCalendar
        role="student"
        availability={{ Mon: { enabled: false, start: "10:00", end: "11:00" } }}
        bookedSlots={[{ date: pastDate.toISOString(), status: "confirmed" }]}
        onSlotClick={mockOnSlotClick}
      />
    );

    const dayCells = screen.getAllByText(/^\d+$/);
    const cell = dayCells[0].closest("div");
    if (cell) fireEvent.click(cell);

    expect(mockOnSlotClick).not.toHaveBeenCalled();
  });

  it("renders tutor view and allows clicking on pending slot", async () => {
    const today = new Date();
    const dateStr = today.toISOString().split("T")[0];

    render(
      <AvailabilityCalendar
        role="tutor"
        availability={baseAvailability}
        bookedSlots={[{ date: dateStr, status: "pending" }]}
        onSlotClick={mockOnSlotClick}
      />
    );

    const pendingText = screen.getByText(/Pending/i);
    const pendingCell = pendingText.closest("div");
    if (pendingCell) fireEvent.click(pendingCell);

    await waitFor(() => expect(mockOnSlotClick).toHaveBeenCalled());
  });

  it("renders different booking statuses visually", () => {
    const statuses = ["confirmed", "pending", "on_hold", "reschedule_requested"];
    const bookedSlots = statuses.map((status, i) => ({
      date: `2025-11-${i + 1}`,
      status,
    }));

    render(
      <AvailabilityCalendar
        role="student"
        availability={baseAvailability}
        bookedSlots={bookedSlots}
      />
    );

    expect(screen.getByText(/Booked/i)).toBeInTheDocument();
    expect(screen.getByText(/Pending/i)).toBeInTheDocument();
    expect(screen.getByText(/On Hold/i)).toBeInTheDocument();
    expect(screen.getByText(/Reschedule Requested/i)).toBeInTheDocument();
  });
});
