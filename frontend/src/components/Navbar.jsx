import { useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useTheme } from "../context/ThemeContext";

function Navbar() {
  const [open, setOpen] = useState(false);
  const { isAuthenticated, isAdmin, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <nav
      className="
        fixed top-0 left-0 z-50
        flex h-[82px] w-full
        items-center justify-between
        border-b border-white/10
        bg-[#050607]/75
        px-[6%]
        backdrop-blur-xl
      "
    >
      <Link
        to="/"
        className="
          font-black tracking-[-1px]
          text-2xl
        "
      >
        Pulse<span className="text-blue-400">Drive</span>
      </Link>

      <div className="hidden items-center gap-8 lg:flex">
        <Link
          to="/home"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          Home
        </Link>

        <Link
          to="/home#vehicles"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          Vehicles
        </Link>

        <Link
          to="/home#categories"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          Categories
        </Link>

        <Link
          to="/home#test-drive"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          Test Drive
        </Link>

        <Link
          to="/home#about"
          className="text-sm text-zinc-400 transition hover:text-white"
        >
          About
        </Link>
      </div>

      <div className="flex items-center gap-3">
        <button
          type="button"
          onClick={toggleTheme}
          aria-label={`Switch to ${theme === "dark" ? "light" : "dark"} theme`}
          title={`Switch to ${theme === "dark" ? "light" : "dark"} theme`}
          className="theme-toggle"
        >
          {theme === "dark" ? "☼" : "☾"}
        </button>
        {isAuthenticated && (
          <button onClick={logout} className="hidden text-sm text-zinc-400 transition hover:text-white sm:block">Sign out</button>
        )}
        <Link
  to={isAuthenticated ? (isAdmin ? "/admin" : "/account") : "/login"}
  className="
    px-4 py-2
    text-sm text-white
    transition
    hover:text-blue-400
  "
>
  {isAuthenticated ? "Account" : "Login"}
</Link>

        <Link
          to="/home"
          className="
            hidden rounded-lg
            border border-white/15
            bg-white/10
            px-5 py-2.5
            text-sm
            transition
            hover:bg-white hover:text-black
            sm:inline-flex
          "
        >
          Explore Cars
        </Link>

        <button type="button" aria-label="Toggle menu" onClick={() => setOpen(!open)} className="text-2xl text-zinc-300 lg:hidden">{open ? "×" : "☰"}</button>
      </div>
      {open && <div className="absolute left-0 top-[82px] w-full border-b border-white/10 bg-[#080b0e] p-6 lg:hidden">
        <div className="grid gap-5 text-sm text-zinc-300">
          <Link to="/home" onClick={() => setOpen(false)}>Home</Link>
          <Link to="/home#vehicles" onClick={() => setOpen(false)}>Vehicles</Link>
          <Link to="/home#categories" onClick={() => setOpen(false)}>Categories</Link>
          <Link to="/home#test-drive" onClick={() => setOpen(false)}>Test Drive</Link>
          <Link to="/home#about" onClick={() => setOpen(false)}>About</Link>
        </div>
      </div>}
    </nav>
  );
}

export default Navbar;