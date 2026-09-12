import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import api from "../../api/axios";

function AdminVehicles() {
  const [vehicles, setVehicles] = useState([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const pageSize = 10;

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadVehicles = async () => {
    try {
      setLoading(true);
      setError("");

      const response =
        await api.get("/vehicles");

      setVehicles(response.data || []);
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
          "Unable to load vehicles."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadVehicles();
  }, []);

  const handleDelete = async (id) => {
    const confirmed =
      window.confirm(
        "Are you sure you want to delete this vehicle?"
      );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(`/vehicles/${id}`);

      setVehicles((current) =>
        current.filter(
          (vehicle) =>
            vehicle.id !== id
        )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to delete vehicle."
      );
    }
  };

  const filteredVehicles =
    vehicles.filter((vehicle) => {
      const text =
        `${vehicle.brand} ${vehicle.model} ${vehicle.variant ?? ""}`
          .toLowerCase();

      return text.includes(
        search.toLowerCase()
      );
    });

  const totalPages = Math.max(
    1,
    Math.ceil(filteredVehicles.length / pageSize)
  );
  const visibleVehicles = filteredVehicles.slice(
    page * pageSize,
    (page + 1) * pageSize
  );

  return (
    <div>
      <div
        className="
          flex flex-col
          justify-between gap-5
          lg:flex-row lg:items-end
        "
      >
        <div>
          <p
            className="
              text-xs font-bold
              tracking-[0.3em]
              text-blue-400
            "
          >
            INVENTORY
          </p>

          <h1
            className="
              mt-3 text-4xl
              font-black
              tracking-tight
            "
          >
            Vehicle Management
          </h1>

          <p className="mt-2 text-zinc-500">
            Manage vehicle inventory,
            specifications and availability.
          </p>
        </div>

        <Link
          to="/admin/vehicles/new"
          className="
            rounded-xl
            bg-white
            px-5 py-3
            text-sm font-bold
            text-black
            transition
            hover:-translate-y-0.5
          "
        >
          + Add Vehicle
        </Link>
      </div>

      <div
        className="
          mt-8 rounded-2xl
          border border-white/10
          bg-white/[0.03]
          p-4
        "
      >
        <input
          type="text"
          value={search}
          onChange={(event) =>
            (() => {
              setPage(0);
              setSearch(event.target.value);
            })()
          }
          placeholder="Search brand, model or variant..."
          className="
            w-full rounded-xl
            border border-white/10
            bg-black/20
            px-4 py-3
            text-sm
            outline-none
            placeholder:text-zinc-600
            focus:border-blue-400/40
          "
        />
      </div>

      {loading && (
        <div className="mt-8 text-zinc-500">
          Loading vehicles...
        </div>
      )}

      {error && (
        <div
          className="
            mt-8 rounded-2xl
            border border-red-500/20
            bg-red-500/5
            p-5
            text-red-300
          "
        >
          {error}
        </div>
      )}

      {!loading &&
        !error && (
          <div
            className="
              mt-8 overflow-hidden
              rounded-2xl
              border border-white/10
              bg-white/[0.02]
            "
          >
            <div className="overflow-x-auto">
              <table className="w-full min-w-[1000px]">
                <thead>
                  <tr
                    className="
                      border-b border-white/10
                      text-left
                      text-xs
                      uppercase
                      tracking-wider
                      text-zinc-600
                    "
                  >
                    <th className="px-5 py-4">
                      Vehicle
                    </th>

                    <th className="px-5 py-4">
                      Year
                    </th>

                    <th className="px-5 py-4">
                      Price
                    </th>

                    <th className="px-5 py-4">
                      Stock
                    </th>

                    <th className="px-5 py-4">
                      Status
                    </th>

                    <th className="px-5 py-4">
                      Actions
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {visibleVehicles.map(
                    (vehicle) => (
                      <tr
                        key={vehicle.id}
                        className="
                          border-b
                          border-white/5
                          transition
                          hover:bg-white/[0.03]
                        "
                      >
                        <td className="px-5 py-5">
                          <div>
                            <p className="font-semibold">
                              {vehicle.brand}{" "}
                              {vehicle.model}
                            </p>

                            <p className="mt-1 text-xs text-zinc-600">
                              {vehicle.variant ||
                                "—"}
                            </p>
                          </div>
                        </td>

                        <td className="px-5 py-5 text-sm text-zinc-400">
                          {vehicle.year}
                        </td>

                        <td className="px-5 py-5 text-sm font-semibold">
                          ₹
                          {Number(
                            vehicle.price
                          ).toLocaleString(
                            "en-IN"
                          )}
                        </td>

                        <td className="px-5 py-5">
                          <span
                            className={`
                              rounded-full
                              px-3 py-1
                              text-xs

                              ${
                                vehicle.stock >
                                0
                                  ? "bg-green-500/10 text-green-300"
                                  : "bg-red-500/10 text-red-300"
                              }
                            `}
                          >
                            {vehicle.stock}
                          </span>
                        </td>

                        <td className="px-5 py-5">
                          <span
                            className={`
                              rounded-full
                              px-3 py-1
                              text-xs

                              ${
                                vehicle.available
                                  ? "bg-blue-500/10 text-blue-300"
                                  : "bg-zinc-500/10 text-zinc-500"
                              }
                            `}
                          >
                            {vehicle.available
                              ? "Available"
                              : "Unavailable"}
                          </span>
                        </td>

                        <td className="px-5 py-5">
                          <div className="flex gap-2">
                            <Link
                              to={`/admin/vehicles/${vehicle.id}/edit`}
                              className="
                                rounded-lg
                                border border-white/10
                                px-3 py-2
                                text-xs
                                text-zinc-300
                                transition
                                hover:bg-white/10
                              "
                            >
                              Edit
                            </Link>

                            <Link
                              to={`/admin/vehicles/${vehicle.id}/media`}
                              className="
                                rounded-lg
                                border border-blue-400/20
                                bg-blue-500/5
                                px-3 py-2
                                text-xs
                                text-blue-300
                              "
                            >
                              Upload Images
                            </Link>

                            <button
                              onClick={() =>
                                handleDelete(
                                  vehicle.id
                                )
                              }
                              className="
                                rounded-lg
                                border border-red-400/20
                                bg-red-500/5
                                px-3 py-2
                                text-xs
                                text-red-300
                                transition
                                hover:bg-red-500/10
                              "
                            >
                              Delete
                            </button>
                          </div>
                        </td>
                      </tr>
                    )
                  )}
                </tbody>
              </table>
            </div>

            {filteredVehicles.length > 0 && (
              <div className="flex items-center justify-between border-t border-white/10 px-5 py-4">
                <button
                  type="button"
                  disabled={page === 0}
                  onClick={() => setPage((current) => current - 1)}
                  className="page-button"
                >
                  ← Previous
                </button>
                <span className="text-sm text-zinc-500">
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  type="button"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage((current) => current + 1)}
                  className="page-button"
                >
                  Next →
                </button>
              </div>
            )}

            {filteredVehicles.length ===
              0 && (
              <div className="p-10 text-center text-zinc-600">
                No vehicles found.
              </div>
            )}
          </div>
        )}
    </div>
  );
}

export default AdminVehicles;