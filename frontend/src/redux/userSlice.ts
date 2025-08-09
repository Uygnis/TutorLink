import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface UserObj {
  id: number;
  name: string;
  email: string;
  role: string;
  token: string;
}

interface UserState {
  user: UserObj | null;
}

const initialState: UserState = {
  user: null,
};

export const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<UserObj | null>) => {
      state.user = action.payload;
    },
  },
});

export const { setUser } = userSlice.actions;

export default userSlice.reducer;
