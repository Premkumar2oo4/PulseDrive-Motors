import { NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useTheme } from "../../context/ThemeContext";

const links = [
  ["Dashboard", "/admin"],
  ["Vehicles", "/admin/vehicles"],
  ["Categories", "/admin/categories"],
  ["Orders", "/admin/orders"],
  ["Customers", "/admin/customers"],
  ["Payments", "/admin/payments"],
  ["Coupons", "/admin/coupons"],
  ["Test Drives", "/admin/test-drives"],
  ["Dealerships", "/admin/dealerships"],
  ["Profile", "/admin/profile"],
];

function AdminSidebar() {
  const { logout } = useAuth();
  const { theme, toggleTheme } = useTheme();

  return (
    <aside
      className="
        fixed left-0 top-0 z-50
        h-screen w-64
        border-r border-white/10
        bg-[#07090c]
        p-5
      "
    >
      <div className="mb-10 text-2xl font-black">
        Pulse
        <span className="text-blue-400">
          Drive
        </span>

        <div className="mt-1 text-xs font-medium tracking-[0.3em] text-zinc-600">
          ADMIN
        </div>
      </div>

      <nav className="space-y-2">
        {links.map(([label, path]) => (
          <NavLink
            key={path}
            to={path}
            end={path === "/admin"}
            className={({ isActive }) =>
              `
              block rounded-xl px-4 py-3
              text-sm transition

              ${
                isActive
                  ? "bg-blue-500/15 text-blue-300"
                  : "text-zinc-500 hover:bg-white/5 hover:text-white"
              }
              `
            }
          >
            {label}
          </NavLink>
        ))}
      </nav>

      <button
        type="button"
        onClick={logout}
        className="
          mt-8 w-full rounded-xl
          border border-red-400/20
          bg-red-500/5 px-4 py-3
          text-left text-sm text-red-300
          transition hover:bg-red-500/10
        "
      >
        Sign out
      </button>

      <button
        type="button"
        onClick={toggleTheme}
        className="mt-3 w-full rounded-xl border border-white/10 px-4 py-3 text-left text-sm text-zinc-400 transition hover:bg-white/5 hover:text-white"
      >
        {theme === "dark" ? "☼  Light theme" : "☾  Dark theme"}
      </button>
    </aside>
  );
}

export default AdminSidebar;