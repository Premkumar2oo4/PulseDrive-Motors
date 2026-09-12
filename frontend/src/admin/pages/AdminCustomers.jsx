import {
  useEffect,
  useState,
} from "react";

import api from "../../api/axios";

function AdminCustomers() {
  const [customers, setCustomers] =
    useState([]);

  const [search, setSearch] =
    useState("");

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [selectedUser, setSelectedUser] =
    useState(null);

  const loadCustomers = async () => {
    try {
      setLoading(true);

      const response =
        await api.get(
          "/admin/users/customers"
        );

      setCustomers(
        response.data || []
      );
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load customers."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCustomers();
  }, []);

  const searchCustomers =
    async () => {
      if (!search.trim()) {
        loadCustomers();
        return;
      }

      try {
        setLoading(true);

        const response =
          await api.get(
            "/admin/users/search",
            {
              params: {
                keyword:
                  search.trim(),
              },
            }
          );

        setCustomers(
          response.data || []
        );
      } catch (error) {
        alert(
          error.response?.data?.message ||
            "Unable to search customers."
        );
      } finally {
        setLoading(false);
      }
    };

  const openUser =
    async (userId) => {
      try {
        const response =
          await api.get(
            `/admin/users/${userId}`
          );

        setSelectedUser(
          response.data
        );
      } catch (error) {
        alert(
          error.response?.data?.message ||
            "Unable to load customer."
        );
      }
    };

  const changeEnabledState =
    async (customer) => {
      const endpoint =
        customer.enabled
          ? "disable"
          : "enable";

      const action =
        customer.enabled
          ? "disable"
          : "enable";

      if (
        !window.confirm(
          `Are you sure you want to ${action} this customer?`
        )
      ) {
        return;
      }

      try {
        const response =
          await api.put(
            `/admin/users/${customer.id}/${endpoint}`
          );

        setCustomers(
          (current) =>
            current.map(
              (item) =>
                item.id ===
                customer.id
                  ? response.data
                  : item
            )
        );

        if (
          selectedUser?.id ===
          customer.id
        ) {
          setSelectedUser(
            response.data
          );
        }
      } catch (error) {
        alert(
          error.response?.data?.message ||
            `Unable to ${action} customer.`
        );
      }
    };

  return (
    <div>
      <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
        USERS
      </p>

      <h1 className="mt-3 text-4xl font-black">
        Customers
      </h1>

      <p className="mt-2 text-zinc-500">
        View and manage PulseDrive customer accounts.
      </p>

      <div
        className="
          mt-8 flex flex-col gap-3
          rounded-2xl
          border border-white/10
          bg-white/[0.03]
          p-4
          sm:flex-row
        "
      >
        <input
          value={search}
          onChange={(event) =>
            setSearch(
              event.target.value
            )
          }
          onKeyDown={(event) => {
            if (
              event.key === "Enter"
            ) {
              searchCustomers();
            }
          }}
          placeholder="Search name or email..."
          className="
            flex-1 rounded-xl
            border border-white/10
            bg-black/20
            px-4 py-3
            outline-none
            placeholder:text-zinc-600
          "
        />

        <button
          onClick={searchCustomers}
          className="
            rounded-xl
            bg-white
            px-6 py-3
            font-bold text-black
          "
        >
          Search
        </button>

        {search && (
          <button
            onClick={() => {
              setSearch("");
              loadCustomers();
            }}
            className="
              rounded-xl
              border border-white/10
              px-5 py-3
              text-zinc-400
            "
          >
            Reset
          </button>
        )}
      </div>

      {error && (
        <div className="mt-8 rounded-2xl border border-red-500/20 bg-red-500/5 p-5 text-red-300">
          {error}
        </div>
      )}

      <div className="mt-8 grid grid-cols-1 gap-6 xl:grid-cols-[1fr_360px]">
        <div className="overflow-hidden rounded-2xl border border-white/10 bg-white/[0.02]">
          {loading ? (
            <div className="p-8 text-zinc-500">
              Loading customers...
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full min-w-[800px]">
                  <thead>
                    <tr className="border-b border-white/10 text-left text-xs uppercase text-zinc-600">
                      <th className="px-5 py-4">
                        Customer
                      </th>

                      <th className="px-5 py-4">
                        Role
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
                    {customers.map(
                      (customer) => (
                        <tr
                          key={customer.id}
                          className="border-b border-white/5 hover:bg-white/[0.03]"
                        >
                          <td className="px-5 py-5">
                            <p className="font-semibold">
                              {customer.firstName}{" "}
                              {customer.lastName}
                            </p>

                            <p className="mt-1 text-xs text-zinc-600">
                              {customer.email}
                            </p>
                          </td>

                          <td className="px-5 py-5 text-sm text-zinc-400">
                            {customer.role}
                          </td>

                          <td className="px-5 py-5">
                            <span
                              className={`
                                rounded-full
                                px-3 py-1
                                text-xs
                                ${
                                  customer.enabled
                                    ? "bg-green-500/10 text-green-300"
                                    : "bg-red-500/10 text-red-300"
                                }
                              `}
                            >
                              {customer.enabled
                                ? "Enabled"
                                : "Disabled"}
                            </span>
                          </td>

                          <td className="px-5 py-5">
                            <div className="flex gap-2">
                              <button
                                onClick={() =>
                                  openUser(
                                    customer.id
                                  )
                                }
                                className="
                                  rounded-lg
                                  border border-white/10
                                  px-3 py-2
                                  text-xs
                                "
                              >
                                View
                              </button>

                              <button
                                onClick={() =>
                                  changeEnabledState(
                                    customer
                                  )
                                }
                                className={`
                                  rounded-lg
                                  border px-3 py-2
                                  text-xs
                                  ${
                                    customer.enabled
                                      ? "border-red-400/20 bg-red-500/5 text-red-300"
                                      : "border-green-400/20 bg-green-500/5 text-green-300"
                                  }
                                `}
                              >
                                {customer.enabled
                                  ? "Disable"
                                  : "Enable"}
                              </button>
                            </div>
                          </td>
                        </tr>
                      )
                    )}
                  </tbody>
                </table>
              </div>

              {customers.length ===
                0 && (
                <div className="p-10 text-center text-zinc-600">
                  No customers found.
                </div>
              )}
            </>
          )}
        </div>

        <aside
          className="
            h-fit rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-6
          "
        >
          <h2 className="text-xl font-bold">
            Customer Details
          </h2>

          {!selectedUser ? (
            <p className="mt-5 text-sm text-zinc-600">
              Select a customer to view their details.
            </p>
          ) : (
            <div className="mt-6 space-y-5">
              <div>
                <p className="text-xs text-zinc-600">
                  Name
                </p>

                <p className="mt-1">
                  {selectedUser.firstName}{" "}
                  {selectedUser.lastName}
                </p>
              </div>

              <div>
                <p className="text-xs text-zinc-600">
                  Email
                </p>

                <p className="mt-1 break-all">
                  {selectedUser.email}
                </p>
              </div>

              <div>
                <p className="text-xs text-zinc-600">
                  Phone
                </p>

                <p className="mt-1">
                  {selectedUser.phone ||
                    "—"}
                </p>
              </div>

              <div>
                <p className="text-xs text-zinc-600">
                  Address
                </p>

                <p className="mt-1 text-sm text-zinc-400">
                  {selectedUser.address ||
                    "—"}
                </p>
              </div>

              <div>
                <p className="text-xs text-zinc-600">
                  Account
                </p>

                <p className="mt-1">
                  {selectedUser.enabled
                    ? "Enabled"
                    : "Disabled"}
                </p>
              </div>
            </div>
          )}
        </aside>
      </div>
    </div>
  );
}

export default AdminCustomers;