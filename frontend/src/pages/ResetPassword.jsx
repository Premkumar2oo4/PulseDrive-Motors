import { useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import api from "../api/axios";

function ResetPassword() {
  const [params] = useSearchParams();
  const [token, setToken] = useState(params.get("token") || "");
  const [newPassword, setNewPassword] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    try {
      const response = await api.post("/auth/reset-password", { token, newPassword });
      setMessage(response.data || "Password reset successful.");
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Unable to reset your password.");
    } finally {
      setLoading(false);
    }
  };

  return <main className="flex min-h-screen items-center justify-center bg-[#050607] px-5 text-white"><div className="w-full max-w-md rounded-3xl border border-white/10 bg-white/[0.03] p-8"><Link to="/" className="text-2xl font-black">Pulse<span className="text-blue-400">Drive</span></Link><h1 className="mt-10 text-4xl font-black tracking-tight">Choose a new password.</h1><form onSubmit={submit} className="mt-8 space-y-5"><label className="block text-xs text-zinc-400">Reset token<input className="filter-control mt-2 w-full" value={token} onChange={(event) => setToken(event.target.value)} required /></label><label className="block text-xs text-zinc-400">New password<input className="filter-control mt-2 w-full" type="password" minLength="8" value={newPassword} onChange={(event) => setNewPassword(event.target.value)} required /></label>{error && <p className="text-sm text-red-300">{error}</p>}{message && <p className="text-sm text-blue-300">{message}</p>}<button disabled={loading} className="w-full rounded-xl bg-white py-3.5 font-bold text-black disabled:opacity-50">{loading ? "Saving..." : "Reset password"}</button></form><p className="mt-6 text-center text-sm text-zinc-500"><Link to="/login" className="text-blue-400">Back to sign in</Link></p></div></main>;
}

export default ResetPassword;
