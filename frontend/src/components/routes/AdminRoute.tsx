import React, { useEffect, useState, useRef } from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAppSelector } from "@/redux/store";
import { toast } from "react-toastify";
import Loading from "@/components/Loading";
import { CurrentAdmin } from "@/api/userAPI";

const AdminRoute = () => {
  //States
  const [loading, setLoading] = useState(true);
  const [isAdmin, setIsAdmin] = useState(false);

  //Variables
  const { user } = useAppSelector((state) => state.user);
  const isToastDisplayedRef = useRef(false);

  useEffect(() => {
    setTimeout(() => {
      setLoading(false);
    }, 1000);
  }, []);

  useEffect(() => {
    if (user && user.token) {
      CurrentAdmin(user.token)
        .then((res) => {
          setIsAdmin(true);
        })
        .catch((err) => {
          setIsAdmin(false);
        });
    }
  }, [user]);

  if (loading) {
    return <Loading />;
  }

  if (!isAdmin) {
    if (!isToastDisplayedRef.current) {
      toast.error("Please login as admin to continue");
      isToastDisplayedRef.current = true;
    }
    return <Navigate to="/login" />;
  }

  return <Outlet />;
};

export default AdminRoute;
