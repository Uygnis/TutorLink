import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export enum Role {
  ADMIN = "ADMIN",
  STUDENT = "STUDENT",
  TUTOR = "TUTOR",
  USER = "USER",
}

interface UserObj {
  id: string;
  name: string;
  email: string;
  role: Role;
  status?: string;
  token?: string;
  permissions?: string[];
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
