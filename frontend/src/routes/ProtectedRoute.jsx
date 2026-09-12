import { Navigate } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

function ProtectedRoute({
  children,
  adminOnly = false,
}) {
  const {
    user,
    loading,
  } = useAuth();

  if (loading) {
    return (
      <div
        className="
          flex min-h-screen
          items-center justify-center
          bg-[#050607]
          text-zinc-400
        "
      >
        Loading...
      </div>
    );
  }

  if (!user) {
    return (
      <Navigate
        to="/login"
        replace
      />
    );
  }

  if (
    adminOnly &&
    user.role !== "ADMIN" &&
    user.role !== "ROLE_ADMIN"
  ) {
    return (
      <Navigate
        to="/home"
        replace
      />
    );
  }

  return children;
}

export default ProtectedRoute;