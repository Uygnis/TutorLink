export type DayAvailability = {
  [day: string]: { start: string; end: string; enabled: boolean };
};

type AvailabilityPickerProps = {
  value?: DayAvailability;
  onChange?: (availability: DayAvailability) => void;
};
