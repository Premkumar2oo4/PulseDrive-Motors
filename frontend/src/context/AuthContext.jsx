import {
  createContext,
  useContext,
  useEffect,
  useState,
} from "react";

import api from "../api/axios";

const AuthContext = createContext(null);

/*
 * Decode JWT payload without another library.
 */
function decodeToken(token) {
  try {
    const payload = token.split(".")[1];

    const normalized = payload
      .replace(/-/g, "+")
      .replace(/_/g, "/");

    const decoded = JSON.parse(
      decodeURIComponent(
        atob(normalized)
          .split("")
          .map(
            (char) =>
              "%" +
              (
                "00" +
                char
                  .charCodeAt(0)
                  .toString(16)
              ).slice(-2)
          )
          .join("")
      )
    );

    return decoded;
  } catch (error) {
    console.error(
      "Unable to decode JWT:",
      error
    );

    return null;
  }
}

export function AuthProvider({
  children,
}) {
  const [user, setUser] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  /*
   * Restore login after browser refresh.
   */
  useEffect(() => {
    restoreSession();
    const handleExpiredSession = () => setUser(null);
    window.addEventListener("auth:expired", handleExpiredSession);
    return () => window.removeEventListener("auth:expired", handleExpiredSession);
  }, []);

  const restoreSession = async () => {
    const accessToken =
      localStorage.getItem(
        "accessToken"
      );

    if (!accessToken) {
      setUser(null);
      setLoading(false);

      return;
    }

    const payload =
      decodeToken(accessToken);

    if (!payload) {
      clearLocalSession();
      setLoading(false);

      return;
    }

    /*
     * JWT exp is seconds.
     * Date.now() is milliseconds.
     */
    const expired =
      payload.exp &&
      payload.exp * 1000 <
        Date.now();

    if (expired) {
      const refreshToken = localStorage.getItem("refreshToken");

      if (refreshToken) {
        try {
          const response = await api.post("/auth/refresh", { refreshToken });
          const nextToken = response.data.accessToken;
          localStorage.setItem("accessToken", nextToken);
          const nextPayload = decodeToken(nextToken);
          setUser({ email: nextPayload?.sub, role: nextPayload?.role });
          setLoading(false);
          return;
        } catch {
          clearLocalSession();
        }
      } else {
        clearLocalSession();
      }

      setLoading(false);
      return;
    }

    setUser({
      email: payload.sub,
      role: payload.role,
    });

    setLoading(false);
  };

  const login = async (
    email,
    password
  ) => {
    const response =
      await api.post(
        "/auth/login",
        {
          email,
          password,
        }
      );

    const data =
      response.data;

    /*
     * Your AuthResponseDTO currently uses token.
     */
    const accessToken =
      data.token ||
      data.accessToken;

    if (!accessToken) {
      throw new Error(
        "Access token missing from login response"
      );
    }

    localStorage.setItem(
      "accessToken",
      accessToken
    );

    if (data.refreshToken) {
      localStorage.setItem(
        "refreshToken",
        data.refreshToken
      );
    }

    const payload =
      decodeToken(accessToken);

    setUser({
      id: data.id,
      firstName:
        data.firstName,
      lastName:
        data.lastName,
      email:
        data.email ||
        payload?.sub,
      role:
        data.role ||
        payload?.role,
    });

    return data;
  };

  const register =
    async (payload) => {
      return api.post(
        "/auth/register",
        payload
      );
    };

  const logout = async () => {
    const refreshToken =
      localStorage.getItem(
        "refreshToken"
      );

    try {
      if (refreshToken) {
        await api.post(
          "/auth/logout",
          {
            refreshToken,
          }
        );
      }
    } catch (error) {
      console.error(
        "Backend logout failed:",
        error
      );
    }

    clearLocalSession();
  };

  const clearLocalSession = () => {
    localStorage.removeItem(
      "accessToken"
    );

    localStorage.removeItem(
      "refreshToken"
    );

    setUser(null);
  };

  const isAuthenticated =
    user !== null;

  const isAdmin =
    user?.role === "ADMIN" ||
    user?.role ===
      "ROLE_ADMIN";

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        login,
        register,
        logout,
        isAuthenticated,
        isAdmin,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context =
    useContext(AuthContext);

  if (!context) {
    throw new Error(
      "useAuth must be used inside AuthProvider"
    );
  }

  return context;
}