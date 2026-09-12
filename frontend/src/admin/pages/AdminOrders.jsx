import { useEffect, useState } from "react";
import api from "../../api/axios";

const statusOptions = [
  "PLACED",
  "CONFIRMED",
  "PROCESSING",
  "READY_FOR_DELIVERY",
  "DELIVERED",
  "CANCELLED",
];

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [updatingId, setUpdatingId] = useState(null);
  const [error, setError] = useState("");

  const loadOrders = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/admin/orders");

      setOrders(response.data || []);
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
          "Unable to load orders."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const handleStatusChange = async (
    orderId,
    status
  ) => {
    try {
      setUpdatingId(orderId);

      const response = await api.put(
        `/admin/orders/${orderId}/status`,
        null,
        {
          params: {
            status,
          },
        }
      );

      setOrders((current) =>
        current.map((order) =>
          order.orderId === orderId
            ? response.data
            : order
        )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to update order status."
      );
    } finally {
      setUpdatingId(null);
    }
  };

  const filtered = orders.filter((order) => {
    const searchable = `
      ${order.orderId ?? ""}
      ${order.status ?? ""}
      ${order.paymentStatus ?? ""}
      ${order.userEmail ?? ""}
      ${order.email ?? ""}
    `.toLowerCase();

    return searchable.includes(
      search.toLowerCase()
    );
  });

  return (
    <div>
      <div>
        <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
          SALES
        </p>

        <h1 className="mt-3 text-4xl font-black tracking-tight">
          Orders
        </h1>

        <p className="mt-2 text-zinc-500">
          Monitor and update customer orders.
        </p>
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
          value={search}
          onChange={(event) =>
            setSearch(event.target.value)
          }
          placeholder="Search order id, status or customer..."
          className="
            w-full rounded-xl
            border border-white/10
            bg-black/20
            px-4 py-3
            outline-none
            placeholder:text-zinc-600
            focus:border-blue-400/40
          "
        />
      </div>

      {loading && (
        <p className="mt-8 text-zinc-500">
          Loading orders...
        </p>
      )}

      {error && (
        <div className="mt-8 rounded-2xl border border-red-500/20 bg-red-500/5 p-5 text-red-300">
          {error}
        </div>
      )}

      {!loading && !error && (
        <div className="mt-8 overflow-hidden rounded-2xl border border-white/10 bg-white/[0.02]">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[1100px]">
              <thead>
                <tr className="border-b border-white/10 text-left text-xs uppercase tracking-wider text-zinc-600">
                  <th className="px-5 py-4">
                    Order
                  </th>

                  <th className="px-5 py-4">
                    Customer
                  </th>

                  <th className="px-5 py-4">
                    Total
                  </th>

                  <th className="px-5 py-4">
                    Payment
                  </th>

                  <th className="px-5 py-4">
                    Status
                  </th>

                  <th className="px-5 py-4">
                    Created
                  </th>
                </tr>
              </thead>

              <tbody>
                {filtered.map((order) => (
                  <tr
                    key={order.orderId}
                    className="border-b border-white/5 hover:bg-white/[0.03]"
                  >
                    <td className="px-5 py-5">
                      <p className="font-semibold">
                        #{order.orderId}
                      </p>

                      <p className="mt-1 text-xs text-zinc-600">
                        {order.couponCode
                          ? `Coupon: ${order.couponCode}`
                          : "No coupon"}
                      </p>
                    </td>

                    <td className="px-5 py-5">
                      <p className="text-sm">
                        {order.userEmail ||
                          order.email ||
                          "Customer"}
                      </p>

                      {order.shippingAddress && (
                        <p className="mt-1 max-w-[230px] truncate text-xs text-zinc-600">
                          {order.shippingAddress}
                        </p>
                      )}
                    </td>

                    <td className="px-5 py-5 font-semibold">
                      ₹
                      {Number(
                        order.totalAmount ??
                          order.total ??
                          0
                      ).toLocaleString("en-IN")}
                    </td>

                    <td className="px-5 py-5">
                      <span
                        className="
                          rounded-full
                          bg-white/5
                          px-3 py-1
                          text-xs
                          text-zinc-400
                        "
                      >
                        {order.paymentStatus ||
                          "UNKNOWN"}
                      </span>
                    </td>

                    <td className="px-5 py-5">
                      <select
                        value={order.status || ""}
                        disabled={
                          updatingId === order.orderId
                        }
                        onChange={(event) =>
                          handleStatusChange(
                            order.orderId,
                            event.target.value
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
                        {statusOptions.map(
                          (status) => (
                            <option
                              key={status}
                              value={status}
                            >
                              {status}
                            </option>
                          )
                        )}
                      </select>
                    </td>

                    <td className="px-5 py-5 text-sm text-zinc-500">
                      {order.createdAt
                        ? new Date(
                            order.createdAt
                          ).toLocaleString()
                        : "—"}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {filtered.length === 0 && (
            <div className="p-10 text-center text-zinc-600">
              No orders found.
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AdminOrders;