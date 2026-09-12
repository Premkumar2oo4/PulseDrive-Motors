import axios from "axios";

const api = axios.create({
  baseURL: (import.meta.env.VITE_API_URL || "http://localhost:8080/api").replace(/\/$/, ""),
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    if (config.data instanceof FormData) {
      delete config.headers["Content-Type"];
    }

    const token =
      localStorage.getItem(
        "accessToken"
      );

    if (token) {
      config.headers.Authorization =
        `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const request = error.config;
    const refreshToken = localStorage.getItem("refreshToken");

    if (
      error.response?.status !== 401 ||
      request?._retry ||
      !refreshToken ||
      request?.url?.includes("/auth/")
    ) {
      return Promise.reject(error);
    }

    request._retry = true;

    try {
      const response = await axios.post(
        `${api.defaults.baseURL}/auth/refresh`,
        { refreshToken },
        { headers: { "Content-Type": "application/json" } }
      );
      const nextToken = response.data.accessToken;

      if (!nextToken) {
        throw new Error("Refresh response did not contain an access token");
      }

      localStorage.setItem("accessToken", nextToken);
      request.headers.Authorization = `Bearer ${nextToken}`;
      return api(request);
    } catch (refreshError) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("refreshToken");
      window.dispatchEvent(new Event("auth:expired"));
      return Promise.reject(refreshError);
    }
  }
);

export default api;
