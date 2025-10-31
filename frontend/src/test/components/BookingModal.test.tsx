import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import BookingModal, { BookingModalProps } from "@/components/BookingModal";

describe("BookingModal", () => {
  const mockOnClose = jest.fn();
  const mockOnConfirm = jest.fn();

  const baseProps: BookingModalProps = {
    lessonTypes: ["Math", "Science", "English"],
    slot: { date: new Date("2025-11-10"), slot: { start: "10:00", end: "12:00" } },
    hourlyRate: 50,
    onClose: mockOnClose,
    onConfirm: mockOnConfirm,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  // ---------- COST CALCULATION ----------
  test("calculates estimated cost correctly", async () => {
    render(<BookingModal {...baseProps} />);

    // 2 hours × 50 SGD/hr
    expect(await screen.findByText("SGD 100.00")).toBeInTheDocument();
    expect(screen.getByText("(50 SGD/hr × 2 hour(s))")).toBeInTheDocument();
  });

  // ---------- OVERNIGHT SLOT ----------
  test("handles overnight slots where end time < start time", async () => {
    render(
      <BookingModal
        {...baseProps}
        slot={{ date: new Date("2025-11-10"), slot: { start: "23:00", end: "01:00" } }}
      />
    );

    // (end - start = 2 hours after midnight)
    await waitFor(() => {
      expect(screen.getByText("SGD 100.00")).toBeInTheDocument();
    });
  });

  // ---------- NO HOURLY RATE ----------
  test("does not show estimated cost section when hourlyRate is missing", () => {
    render(<BookingModal {...baseProps} hourlyRate={undefined} />);
    expect(screen.queryByText(/Estimated Cost/)).not.toBeInTheDocument();
  });

  // ---------- CHANGE LESSON TYPE ----------
  test("updates selected lesson type when user selects another", () => {
    render(<BookingModal {...baseProps} />);
    const select = screen.getByDisplayValue("Math");
    fireEvent.change(select, { target: { value: "English" } });
    expect(screen.getByDisplayValue("English")).toBeInTheDocument();
  });

  // ---------- CONFIRM BUTTON ----------
  test("calls onConfirm with selected lesson type", () => {
    render(<BookingModal {...baseProps} />);
    fireEvent.click(screen.getByText("Confirm"));
    expect(mockOnConfirm).toHaveBeenCalledWith("Math");
  });

  // ---------- CANCEL BUTTON ----------
  test("calls onClose when Cancel is clicked", () => {
    render(<BookingModal {...baseProps} />);
    fireEvent.click(screen.getByText("Cancel"));
    expect(mockOnClose).toHaveBeenCalled();
  });

  // ---------- NO CONFIRM WHEN EMPTY LESSON TYPE ----------
  test("does not call onConfirm if no lesson type selected", () => {
    render(<BookingModal {...baseProps} lessonTypes={[]} />);
    fireEvent.click(screen.getByText("Confirm"));
    expect(mockOnConfirm).not.toHaveBeenCalled();
  });
});
