import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Tutor } from "@/types/TutorType";

interface TutorState {
  selectedTutor: Tutor | null;
}

const initialState: TutorState = {
  selectedTutor: null,
};

export const tutorSlice = createSlice({
  name: "tutor",
  initialState,
  reducers: {
    setTutor(state, action: PayloadAction<Tutor>) {
      state.selectedTutor = action.payload;
    },
    clearSelectedTutor(state) {
      state.selectedTutor = null;
    },
  },
});

export const { setTutor, clearSelectedTutor } = tutorSlice.actions;
export default tutorSlice.reducer;
