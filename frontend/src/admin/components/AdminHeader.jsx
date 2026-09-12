import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function AdminHeader() {
  const navigate = useNavigate();

  const {
    user,
    logout,
  } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

  return (
    <header
      className="
        sticky top-0 z-40
        flex h-20
        items-center justify-between
        border-b border-white/10
        bg-[#050607]/80
        px-8
        backdrop-blur-xl
      "
    >
      <div>
        <p className="text-xs tracking-[0.25em] text-blue-400">
          PULSEDRIVE ADMIN
        </p>

        <h2 className="mt-1 text-lg font-semibold">
          Management Console
        </h2>
      </div>

      <div className="flex items-center gap-4">
        <div className="hidden text-right sm:block">
          <p className="text-sm font-medium">
            {user?.firstName ||
              user?.email ||
              "Administrator"}
          </p>

          <p className="text-xs text-zinc-600">
            ADMIN
          </p>
        </div>

        <button
          onClick={handleLogout}
          className="
            rounded-xl
            border border-white/10
            bg-white/5
            px-4 py-2
            text-sm text-zinc-400
            transition
            hover:border-red-400/30
            hover:bg-red-500/10
            hover:text-red-300
          "
        >
          Logout
        </button>
      </div>
    </header>
  );
}

export default AdminHeader;