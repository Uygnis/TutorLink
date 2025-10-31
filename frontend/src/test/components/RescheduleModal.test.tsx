import React from "react";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import RescheduleModal from "@/components/RescheduleModal";
import { useAppSelector } from "@/redux/store";
import { GetTutorProfile } from "@/api/tutorAPI";
import { GetBookingsForTutorRange, RequestReschedule } from "@/api/bookingAPI";
import { toast } from "react-toastify";

// --------------------
// Mocks
// --------------------
jest.mock("@/redux/store", () => ({
  useAppSelector: jest.fn(),
}));
jest.mock("@/api/tutorAPI", () => ({
  GetTutorProfile: jest.fn(),
}));
jest.mock("@/api/bookingAPI", () => ({
  GetBookingsForTutorRange: jest.fn(),
  RequestReschedule: jest.fn(),
}));
jest.mock("react-toastify", () => ({
  toast: { success: jest.fn(), error: jest.fn() },
}));

jest.mock("@/components/AvailabilityCalendar", () => {
  return function MockCalendar({ onSlotClick }: any) {
    return (
      <div data-testid="calendar">
        <button
          data-testid="mock-slot"
          onClick={() =>
            onSlotClick(new Date("2025-11-10"), {
              start: "09:00",
              end: "10:00",
              enabled: true,
            })
          }>
          Select Slot
        </button>
      </div>
    );
  };
});

jest.mock("@/components/BookingModal", () => {
  return function MockBookingModal({ onConfirm, onClose }: any) {
    return (
      <div data-testid="booking-modal">
        <button onClick={() => onConfirm("Beginner")}>Confirm</button>
        <button onClick={onClose}>CloseModal</button>
      </div>
    );
  };
});

// --------------------
// Tests
// --------------------
describe("RescheduleModal", () => {
  const mockOnClose = jest.fn();
  const mockOnRescheduleConfirmed = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    (useAppSelector as jest.Mock).mockReturnValue({
      user: { id: "student123", token: "mockToken" },
    });
  });

  const tutorId = "tutor001";
  const bookingId = "booking123";

  const mockTutorData = {
    data: {
      availability: { Mon: { start: "09:00", end: "17:00", enabled: true } },
      lessonType: ["Beginner", "Advanced"],
    },
  };

  const mockBookingsData = {
    data: [{ date: "2025-11-12", status: "booked" }],
  };

  const renderModal = async () => {
    (GetTutorProfile as jest.Mock).mockResolvedValue(mockTutorData);
    (GetBookingsForTutorRange as jest.Mock).mockResolvedValue(mockBookingsData);

    render(
      <RescheduleModal
        bookingId={bookingId}
        tutorId={tutorId}
        onClose={mockOnClose}
        onRescheduleConfirmed={mockOnRescheduleConfirmed}
      />
    );

    // Wait for loading to finish
    await waitFor(() =>
      expect(screen.queryByText(/Loading availability/i)).not.toBeInTheDocument()
    );
  };

  // ---------- TESTS ----------

  test("renders loading then calendar after data fetch", async () => {
    await renderModal();

    expect(GetTutorProfile).toHaveBeenCalledWith("mockToken", tutorId);
    expect(GetBookingsForTutorRange).toHaveBeenCalled();
    expect(screen.getByTestId("calendar")).toBeInTheDocument();
  });

  test("shows error toast if tutor profile fetch fails", async () => {
    (GetTutorProfile as jest.Mock).mockRejectedValue(new Error("fail"));
    render(
      <RescheduleModal
        bookingId={bookingId}
        tutorId={tutorId}
        onClose={mockOnClose}
        onRescheduleConfirmed={mockOnRescheduleConfirmed}
      />
    );

    await waitFor(() =>
      expect(toast.error).toHaveBeenCalledWith("Failed to load tutor availability")
    );
  });

  test("shows error toast if bookings fetch fails", async () => {
    (GetTutorProfile as jest.Mock).mockResolvedValue(mockTutorData);
    (GetBookingsForTutorRange as jest.Mock).mockRejectedValue(new Error("fail"));

    render(
      <RescheduleModal
        bookingId={bookingId}
        tutorId={tutorId}
        onClose={mockOnClose}
        onRescheduleConfirmed={mockOnRescheduleConfirmed}
      />
    );

    await waitFor(() => expect(toast.error).toHaveBeenCalledWith("Failed to load booked slots"));
  });

  test("clicking slot shows BookingModal", async () => {
    await renderModal();
    fireEvent.click(screen.getByTestId("mock-slot"));
    expect(screen.getByTestId("booking-modal")).toBeInTheDocument();
  });

  test("confirms reschedule successfully", async () => {
    await renderModal();

    fireEvent.click(screen.getByTestId("mock-slot"));
    (RequestReschedule as jest.Mock).mockResolvedValue({});

    fireEvent.click(screen.getByText("Confirm"));

    await waitFor(() => {
      expect(RequestReschedule).toHaveBeenCalledWith(
        bookingId,
        expect.objectContaining({
          tutorId,
          studentId: "student123",
          date: expect.any(String),
          start: "09:00",
          end: "10:00",
          lessonType: "Beginner",
        }),
        "mockToken"
      );
      expect(toast.success).toHaveBeenCalledWith("Reschedule request submitted!");
      expect(mockOnRescheduleConfirmed).toHaveBeenCalled();
    });
  });

  test("handles RequestReschedule API failure", async () => {
    await renderModal();

    fireEvent.click(screen.getByTestId("mock-slot"));
    (RequestReschedule as jest.Mock).mockRejectedValue(new Error("fail"));

    fireEvent.click(screen.getByText("Confirm"));

    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith("Failed to request reschedule");
    });
  });

  test("calls onClose when Close button clicked", async () => {
    await renderModal();
    fireEvent.click(screen.getByText("Close"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  test("skips data fetch if user or token missing", async () => {
    (useAppSelector as jest.Mock).mockReturnValue({ user: null });
    render(
      <RescheduleModal
        bookingId={bookingId}
        tutorId={tutorId}
        onClose={mockOnClose}
        onRescheduleConfirmed={mockOnRescheduleConfirmed}
      />
    );
    await waitFor(() => {
      expect(GetTutorProfile).not.toHaveBeenCalled();
    });
  });
});
