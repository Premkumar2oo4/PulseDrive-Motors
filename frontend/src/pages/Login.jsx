import { useState } from "react";
import {
  Link,
  useNavigate,
} from "react-router-dom";

import { useAuth } from "../context/AuthContext";

function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [form, setForm] =
    useState({
      email: "",
      password: "",
    });

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const handleChange = (event) => {
    setForm({
      ...form,
      [event.target.name]:
        event.target.value,
    });
  };

  const handleSubmit =
    async (event) => {
      event.preventDefault();

      try {
        setLoading(true);
        setError("");

        const response =
          await login(
            form.email,
            form.password
          );

        if (response.role === "ADMIN") {
          navigate("/admin");
        } else {
          navigate("/home");
        }
      } catch (error) {
        setError(
          error.response?.data
            ?.message ||
            "Unable to login."
        );
      } finally {
        setLoading(false);
      }
    };

  return (
    <main
      className="
        flex min-h-screen
        items-center justify-center
        bg-[#050607]
        px-5 text-white
      "
    >
      <div
        className="
          w-full max-w-md
          rounded-3xl
          border border-white/10
          bg-white/[0.03]
          p-8
          shadow-2xl
          shadow-black/40
          backdrop-blur-xl
        "
      >
        <Link
          to="/"
          className="
            text-2xl font-black
          "
        >
          Pulse
          <span className="text-blue-400">
            Drive
          </span>
        </Link>

        <h1
          className="
            mt-10 text-4xl
            font-black
            tracking-tight
          "
        >
          Welcome back.
        </h1>

        <p className="mt-3 text-sm text-zinc-500">
          Sign in to continue your
          PulseDrive journey.
        </p>

        {error && (
          <div
            className="
              mt-6 rounded-xl
              border border-red-400/20
              bg-red-500/10
              p-3
              text-sm text-red-300
            "
          >
            {error}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="mt-8 space-y-5"
        >
          <div>
            <label className="text-xs text-zinc-400">
              Email
            </label>

            <input
              type="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              className="
                mt-2 w-full
                rounded-xl
                border border-white/10
                bg-white/5
                px-4 py-3
                outline-none
                transition
                focus:border-blue-400/60
              "
            />
          </div>

          <div>
            <label className="text-xs text-zinc-400">
              Password
            </label>

            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
              className="
                mt-2 w-full
                rounded-xl
                border border-white/10
                bg-white/5
                px-4 py-3
                outline-none
                transition
                focus:border-blue-400/60
              "
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="
              w-full rounded-xl
              bg-white
              py-3.5
              font-bold text-black
              transition
              hover:-translate-y-0.5
              disabled:opacity-50
            "
          >
            {loading
              ? "Signing in..."
              : "Sign In"}
          </button>
        </form>

        <Link to="/forgot-password" className="mt-5 block text-center text-sm text-blue-400">
          Forgot password?
        </Link>

        <p
          className="
            mt-6 text-center
            text-sm text-zinc-500
          "
        >
          New to PulseDrive?{" "}

          <Link
            to="/register"
            className="text-blue-400"
          >
            Create account
          </Link>
        </p>
      </div>
    </main>
  );
}

export default Login;