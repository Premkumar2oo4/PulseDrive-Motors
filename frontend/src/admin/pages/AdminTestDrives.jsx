import {
  useEffect,
  useState,
} from "react";

import api from "../../api/axios";

const statuses = [
  "PENDING",
  "CONFIRMED",
  "COMPLETED",
  "CANCELLED",
];

function AdminTestDrives() {
  const [testDrives, setTestDrives] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [filter, setFilter] =
    useState("ALL");

  const [updating, setUpdating] =
    useState(null);

  const loadTestDrives = async () => {
    try {
      setLoading(true);
      setError("");

      const response =
        await api.get(
          "/admin/test-drives"
        );

      setTestDrives(
        response.data || []
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load test drives."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTestDrives();
  }, []);

  const updateStatus = async (
    testDriveId,
    status
  ) => {
    try {
      setUpdating(testDriveId);

      const response =
        await api.put(
          `/admin/test-drives/${testDriveId}/status`,
          null,
          {
            params: {
              status,
            },
          }
        );

      setTestDrives(
        (current) =>
          current.map(
            (testDrive) =>
              testDrive.id ===
              testDriveId
                ? response.data
                : testDrive
          )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to update test drive."
      );
    } finally {
      setUpdating(null);
    }
  };

  const filtered =
    filter === "ALL"
      ? testDrives
      : testDrives.filter(
          (testDrive) =>
            testDrive.status ===
            filter
        );

  return (
    <div>
      <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
        BOOKINGS
      </p>

      <h1 className="mt-3 text-4xl font-black">
        Test Drives
      </h1>

      <p className="mt-2 text-zinc-500">
        Manage customer vehicle test-drive
        bookings.
      </p>

      {/* FILTERS */}

      <div className="mt-8 flex flex-wrap gap-2">
        {[
          "ALL",
          ...statuses,
        ].map((status) => (
          <button
            key={status}
            onClick={() =>
              setFilter(status)
            }
            className={`
              rounded-xl
              px-4 py-2
              text-xs
              font-semibold
              transition

              ${
                filter === status
                  ? "bg-white text-black"
                  : "border border-white/10 bg-white/[0.03] text-zinc-500 hover:text-white"
              }
            `}
          >
            {status}
          </button>
        ))}
      </div>

      {loading && (
        <p className="mt-8 text-zinc-500">
          Loading test drives...
        </p>
      )}

      {error && (
        <div className="mt-8 rounded-xl border border-red-500/20 bg-red-500/5 p-4 text-red-300">
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
              <table className="w-full min-w-[1050px]">
                <thead>
                  <tr className="border-b border-white/10 text-left text-xs uppercase tracking-wider text-zinc-600">
                    <th className="px-5 py-4">
                      Customer
                    </th>

                    <th className="px-5 py-4">
                      Vehicle
                    </th>

                    <th className="px-5 py-4">
                      Dealership
                    </th>

                    <th className="px-5 py-4">
                      Date
                    </th>

                    <th className="px-5 py-4">
                      Status
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {filtered.map(
                    (testDrive) => (
                      <tr
                        key={
                          testDrive.id
                        }
                        className="
                          border-b
                          border-white/5
                          hover:bg-white/[0.03]
                        "
                      >
                        <td className="px-5 py-5">
                          <p className="font-semibold">
                            {testDrive.customerName ||
                              testDrive.userName ||
                              "Customer"}
                          </p>

                          <p className="mt-1 text-xs text-zinc-600">
                            {testDrive.customerEmail ||
                              testDrive.userEmail ||
                              "—"}
                          </p>
                        </td>

                        <td className="px-5 py-5">
                          <p className="text-sm">
                            {testDrive.vehicleBrand}{" "}
                            {testDrive.vehicleModel}
                          </p>

                          {!testDrive.vehicleBrand && (
                            <p className="text-xs text-zinc-600">
                              Vehicle #
                              {
                                testDrive.vehicleId
                              }
                            </p>
                          )}
                        </td>

                        <td className="px-5 py-5 text-sm text-zinc-400">
                          {testDrive.dealershipName ||
                            `#${testDrive.dealershipId ?? "—"}`}
                        </td>

                        <td className="px-5 py-5">
                          <p className="text-sm">
                            {testDrive.testDriveDate ||
                              testDrive.date ||
                              "—"}
                          </p>

                          <p className="mt-1 text-xs text-zinc-600">
                            {testDrive.time ||
                              testDrive.timeSlot ||
                              ""}
                          </p>
                        </td>

                        <td className="px-5 py-5">
                          <select
                            value={
                              testDrive.status ||
                              "PENDING"
                            }
                            disabled={
                              updating ===
                              testDrive.id
                            }
                            onChange={(
                              event
                            ) =>
                              updateStatus(
                                testDrive.id,
                                event.target
                                  .value
                              )
                            }
                            className="
                              rounded-lg
                              border border-white/10
                              bg-[#0b0d10]
                              px-3 py-2
                              text-xs
                              outline-none
                            "
                          >
                            {statuses.map(
                              (status) => (
                                <option
                                  key={
                                    status
                                  }
                                  value={
                                    status
                                  }
                                >
                                  {status}
                                </option>
                              )
                            )}
                          </select>
                        </td>
                      </tr>
                    )
                  )}
                </tbody>
              </table>
            </div>

            {filtered.length ===
              0 && (
              <div className="p-10 text-center text-zinc-600">
                No test-drive bookings found.
              </div>
            )}
          </div>
        )}
    </div>
  );
}

export default AdminTestDrives;