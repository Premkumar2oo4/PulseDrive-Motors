import { Outlet } from "react-router-dom";
import AdminSidebar from "../components/AdminSidebar";

function AdminLayout() {
  return (
    <div className="min-h-screen bg-[#050607] text-white">
      <AdminSidebar />

      <main className="min-h-screen md:ml-64">
        <div className="p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}

export default AdminLayout;