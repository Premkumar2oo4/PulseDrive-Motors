import { useState } from "react";
import {
  Link,
  useNavigate,
} from "react-router-dom";

import { useAuth } from "../context/AuthContext";

function Register() {
  const navigate = useNavigate();

  const { register } =
    useAuth();

  const [form, setForm] =
    useState({
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      phone: "",
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

        await register(form);

        navigate("/login");
      } catch (error) {
        setError(
          error.response?.data
            ?.message ||
            "Unable to register."
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
        px-5 py-16
        text-white
      "
    >
      <div
        className="
          w-full max-w-xl
          rounded-3xl
          border border-white/10
          bg-white/[0.03]
          p-8
          backdrop-blur-xl
        "
      >
        <Link
          to="/"
          className="text-2xl font-black"
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
          Create your account.
        </h1>

        <p className="mt-3 text-zinc-500">
          Join PulseDrive and discover
          your next vehicle.
        </p>

        {error && (
          <div
            className="
              mt-6 rounded-xl
              border border-red-400/20
              bg-red-500/10
              p-3 text-sm
              text-red-300
            "
          >
            {error}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="
            mt-8 grid
            grid-cols-1
            gap-5
            sm:grid-cols-2
          "
        >
          {[
            ["firstName", "First name", "text"],
            ["lastName", "Last name", "text"],
            ["email", "Email", "email"],
            ["phone", "Phone", "text"],
          ].map(
            ([name, label, type]) => (
              <div key={name}>
                <label className="text-xs text-zinc-400">
                  {label}
                </label>

                <input
                  type={type}
                  name={name}
                  value={form[name]}
                  onChange={handleChange}
                  required
                  className="
                    mt-2 w-full
                    rounded-xl
                    border border-white/10
                    bg-white/5
                    px-4 py-3
                    outline-none
                    focus:border-blue-400/60
                  "
                />
              </div>
            )
          )}

          <div className="sm:col-span-2">
            <label className="text-xs text-zinc-400">
              Password
            </label>

            <input
              type="password"
              name="password"
              value={form.password}
              onChange={handleChange}
              required
              minLength={8}
              className="
                mt-2 w-full
                rounded-xl
                border border-white/10
                bg-white/5
                px-4 py-3
                outline-none
                focus:border-blue-400/60
              "
            />
          </div>

          <button
            disabled={loading}
            className="
              sm:col-span-2
              rounded-xl
              bg-white
              py-3.5
              font-bold text-black
              disabled:opacity-50
            "
          >
            {loading
              ? "Creating account..."
              : "Create Account"}
          </button>
        </form>

        <p
          className="
            mt-6 text-center
            text-sm text-zinc-500
          "
        >
          Already registered?{" "}

          <Link
            to="/login"
            className="text-blue-400"
          >
            Sign in
          </Link>
        </p>
      </div>
    </main>
  );
}

export default Register;