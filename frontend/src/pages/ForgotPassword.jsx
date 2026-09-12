import { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/axios";

function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const response = await api.post("/auth/forgot-password", { email });
      setMessage(response.data || "Password reset instructions have been sent.");
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Unable to send reset instructions.");
    } finally {
      setLoading(false);
    }
  };

  return <AuthShell title="Reset your password." subtitle="We will send reset instructions to your email.">
    <form onSubmit={submit} className="mt-8 space-y-5">
      <label className="block text-xs text-zinc-400">Email<input className="filter-control mt-2 w-full" type="email" value={email} onChange={(event) => setEmail(event.target.value)} required /></label>
      {error && <p className="text-sm text-red-300">{error}</p>}
      {message && <p className="text-sm text-blue-300">{message}</p>}
      <button disabled={loading} className="w-full rounded-xl bg-white py-3.5 font-bold text-black disabled:opacity-50">{loading ? "Sending..." : "Send reset instructions"}</button>
    </form>
  </AuthShell>;
}

function AuthShell({ title, subtitle, children }) {
  return <main className="flex min-h-screen items-center justify-center bg-[#050607] px-5 text-white"><div className="w-full max-w-md rounded-3xl border border-white/10 bg-white/[0.03] p-8"><Link to="/" className="text-2xl font-black">Pulse<span className="text-blue-400">Drive</span></Link><h1 className="mt-10 text-4xl font-black tracking-tight">{title}</h1><p className="mt-3 text-zinc-500">{subtitle}</p>{children}<p className="mt-6 text-center text-sm text-zinc-500"><Link to="/login" className="text-blue-400">Back to sign in</Link></p></div></main>;
}

export default ForgotPassword;
