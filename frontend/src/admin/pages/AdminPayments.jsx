import { useEffect, useMemo, useState } from "react";
import api from "../../api/axios";

function AdminPayments() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] =
    useState("ALL");

  const [selectedPayment, setSelectedPayment] =
    useState(null);
  const [refunding, setRefunding] = useState(false);

  const loadPayments = async () => {
    try {
      setLoading(true);
      setError("");

      const response =
        await api.get("/admin/payments");

      setPayments(response.data || []);
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
          "Unable to load payments."
      );
    } finally {
      setLoading(false);
    }
  };

  const refundPayment = async () => {
    if (!selectedPayment || !window.confirm("Refund this payment?")) return;
    try {
      setRefunding(true);
      const response = await api.put(`/admin/payments/${selectedPayment.paymentId}/refund`);
      setPayments((current) => current.map((payment) => payment.paymentId === response.data.paymentId ? response.data : payment));
      setSelectedPayment(response.data);
    } catch (error) {
      setError(error.response?.data?.message || "Unable to refund this payment.");
    } finally {
      setRefunding(false);
    }
  };

  useEffect(() => {
    loadPayments();
  }, []);

  const filteredPayments = useMemo(() => {
    return payments.filter((payment) => {
      const status =
        payment.status ||
        payment.paymentStatus ||
        "UNKNOWN";

      const matchesStatus =
        statusFilter === "ALL" ||
        status === statusFilter;

      const searchable = `
        ${payment.paymentId ?? ""}
        ${payment.orderId ?? ""}
        ${payment.razorpayPaymentId ?? ""}
        ${payment.razorpayOrderId ?? ""}
        ${payment.transactionId ?? ""}
        ${payment.provider ?? ""}
        ${status}
      `.toLowerCase();

      const matchesSearch =
        searchable.includes(
          search.toLowerCase()
        );

      return matchesStatus && matchesSearch;
    });
  }, [
    payments,
    search,
    statusFilter,
  ]);

  const getStatusStyle = (status) => {
    switch (status) {
      case "SUCCESS":
      case "PAID":
      case "COMPLETED":
        return "bg-green-500/10 text-green-300 border-green-500/20";

      case "FAILED":
        return "bg-red-500/10 text-red-300 border-red-500/20";

      case "PENDING":
      case "CREATED":
        return "bg-yellow-500/10 text-yellow-300 border-yellow-500/20";

      case "REFUNDED":
        return "bg-purple-500/10 text-purple-300 border-purple-500/20";

      default:
        return "bg-white/5 text-zinc-400 border-white/10";
    }
  };

  const totalAmount = payments.reduce(
    (total, payment) =>
      total +
      Number(
        payment.amount ||
          payment.paymentAmount ||
          0
      ),
    0
  );

  const successfulPayments =
    payments.filter((payment) => {
      const status =
        payment.status ||
        payment.paymentStatus;

      return [
        "SUCCESS",
        "PAID",
        "COMPLETED",
      ].includes(status);
    }).length;

  const failedPayments =
    payments.filter((payment) => {
      const status =
        payment.status ||
        payment.paymentStatus;

      return status === "FAILED";
    }).length;

  return (
    <div>
      {/* HEADER */}

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
            FINANCE
          </p>

          <h1
            className="
              mt-3 text-4xl
              font-black
              tracking-tight
            "
          >
            Payments
          </h1>

          <p className="mt-2 text-zinc-500">
            Monitor PulseDrive payment
            transactions.
          </p>
        </div>

        <button
          onClick={loadPayments}
          className="
            rounded-xl
            border border-white/10
            bg-white/5
            px-5 py-3
            text-sm
            text-zinc-300
            transition
            hover:bg-white/10
          "
        >
          Refresh
        </button>
      </div>

      {/* SUMMARY CARDS */}

      <div
        className="
          mt-8 grid
          grid-cols-1 gap-4
          sm:grid-cols-2
          xl:grid-cols-4
        "
      >
        <SummaryCard
          title="Transactions"
          value={payments.length}
        />

        <SummaryCard
          title="Successful"
          value={successfulPayments}
        />

        <SummaryCard
          title="Failed"
          value={failedPayments}
        />

        <SummaryCard
          title="Transaction Value"
          value={`₹${totalAmount.toLocaleString(
            "en-IN"
          )}`}
        />
      </div>

      {/* SEARCH / FILTER */}

      <div
        className="
          mt-8 flex flex-col gap-3
          rounded-2xl
          border border-white/10
          bg-white/[0.03]
          p-4
          lg:flex-row
        "
      >
        <input
          value={search}
          onChange={(event) =>
            setSearch(
              event.target.value
            )
          }
          placeholder="Search payment ID, order ID or transaction..."
          className="
            flex-1 rounded-xl
            border border-white/10
            bg-black/20
            px-4 py-3
            outline-none
            placeholder:text-zinc-600
            focus:border-blue-400/40
          "
        />

        <select
          value={statusFilter}
          onChange={(event) =>
            setStatusFilter(
              event.target.value
            )
          }
          className="
            rounded-xl
            border border-white/10
            bg-[#0b0d10]
            px-4 py-3
            text-sm
            outline-none
          "
        >
          <option value="ALL">
            All Status
          </option>

          <option value="CREATED">
            Created
          </option>

          <option value="PENDING">
            Pending
          </option>

          <option value="SUCCESS">
            Success
          </option>

          <option value="PAID">
            Paid
          </option>

          <option value="FAILED">
            Failed
          </option>

          <option value="REFUNDED">
            Refunded
          </option>
        </select>
      </div>

      {loading && (
        <p className="mt-8 text-zinc-500">
          Loading payments...
        </p>
      )}

      {error && (
        <div
          className="
            mt-8 rounded-2xl
            border border-red-500/20
            bg-red-500/5
            p-5 text-red-300
          "
        >
          {error}
        </div>
      )}

      {/* TABLE + DETAILS */}

      {!loading && !error && (
        <div
          className="
            mt-8 grid
            grid-cols-1 gap-6
            2xl:grid-cols-[1fr_380px]
          "
        >
          <div
            className="
              overflow-hidden
              rounded-2xl
              border border-white/10
              bg-white/[0.02]
            "
          >
            <div className="overflow-x-auto">
              <table className="w-full min-w-[1050px]">
                <thead>
                  <tr
                    className="
                      border-b
                      border-white/10
                      text-left
                      text-xs
                      uppercase
                      tracking-wider
                      text-zinc-600
                    "
                  >
                    <th className="px-5 py-4">
                      Payment
                    </th>

                    <th className="px-5 py-4">
                      Order
                    </th>

                    <th className="px-5 py-4">
                      Amount
                    </th>

                    <th className="px-5 py-4">
                      Provider
                    </th>

                    <th className="px-5 py-4">
                      Status
                    </th>

                    <th className="px-5 py-4">
                      Date
                    </th>

                    <th className="px-5 py-4">
                      Action
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {filteredPayments.map(
                    (payment) => {
                      const status =
                        payment.status ||
                        payment.paymentStatus ||
                        "UNKNOWN";

                      return (
                        <tr
                          key={payment.paymentId}
                          className="
                            border-b
                            border-white/5
                            transition
                            hover:bg-white/[0.03]
                          "
                        >
                          <td className="px-5 py-5">
                            <p className="font-semibold">
                              #{payment.paymentId}
                            </p>

                            <p
                              className="
                                mt-1
                                max-w-[170px]
                                truncate
                                text-xs
                                text-zinc-600
                              "
                            >
                              {payment.razorpayPaymentId ||
                                payment.transactionId ||
                                "—"}
                            </p>
                          </td>

                          <td className="px-5 py-5">
                            <span
                              className="
                                rounded-lg
                                bg-white/5
                                px-3 py-1
                                text-sm
                              "
                            >
                              #
                              {payment.orderId ??
                                "—"}
                            </span>
                          </td>

                          <td className="px-5 py-5 font-semibold">
                            ₹
                            {Number(
                              payment.amount ||
                                payment.paymentAmount ||
                                0
                            ).toLocaleString(
                              "en-IN"
                            )}
                          </td>

                          <td className="px-5 py-5 text-sm text-zinc-400">
                            {payment.provider ||
                              payment.paymentMethod ||
                              "Razorpay"}
                          </td>

                          <td className="px-5 py-5">
                            <span
                              className={`
                                rounded-full
                                border
                                px-3 py-1
                                text-xs
                                ${getStatusStyle(
                                  status
                                )}
                              `}
                            >
                              {status}
                            </span>
                          </td>

                          <td className="px-5 py-5 text-sm text-zinc-500">
                            {payment.createdAt
                              ? new Date(
                                  payment.createdAt
                                ).toLocaleString()
                              : "—"}
                          </td>

                          <td className="px-5 py-5">
                            <button
                              onClick={() =>
                                setSelectedPayment(
                                  payment
                                )
                              }
                              className="
                                rounded-lg
                                border
                                border-white/10
                                px-3 py-2
                                text-xs
                                transition
                                hover:bg-white/10
                              "
                            >
                              View
                            </button>
                          </td>
                        </tr>
                      );
                    }
                  )}
                </tbody>
              </table>
            </div>

            {filteredPayments.length ===
              0 && (
              <div className="p-12 text-center text-zinc-600">
                No payments found.
              </div>
            )}
          </div>

          {/* PAYMENT DETAILS */}

          <aside
            className="
              h-fit rounded-2xl
              border border-white/10
              bg-white/[0.03]
              p-6
            "
          >
            <p className="text-xs font-bold tracking-[0.25em] text-blue-400">
              TRANSACTION
            </p>

            <h2 className="mt-2 text-xl font-bold">
              Payment Details
            </h2>

            {!selectedPayment ? (
              <p className="mt-6 text-sm text-zinc-600">
                Select a transaction to
                inspect its details.
              </p>
            ) : (
              <div className="mt-7 space-y-5">
                <Detail
                  label="Payment ID"
                  value={
                    selectedPayment.paymentId
                  }
                />

                <Detail
                  label="Order ID"
                  value={
                    selectedPayment.orderId
                  }
                />

                <Detail
                  label="Amount"
                  value={`₹${Number(
                    selectedPayment.amount ||
                      selectedPayment.paymentAmount ||
                      0
                  ).toLocaleString(
                    "en-IN"
                  )}`}
                />

                <Detail
                  label="Status"
                  value={
                    selectedPayment.status ||
                    selectedPayment.paymentStatus ||
                    "UNKNOWN"
                  }
                />

                <Detail
                  label="Payment transaction"
                  value={
                    selectedPayment.transactionId
                  }
                />

                <Detail
                  label="Refund status"
                  value={
                    selectedPayment.refundStatus
                  }
                />

                <Detail
                  label="Payment method"
                  value={
                    selectedPayment.paymentMethod || "—"
                  }
                />

                <Detail
                  label="Created"
                  value={
                    selectedPayment.createdAt
                      ? new Date(
                          selectedPayment.createdAt
                        ).toLocaleString()
                      : "—"
                  }
                />

                {selectedPayment.status === "SUCCESS" && selectedPayment.refundStatus !== "REFUNDED" && (
                  <button
                    type="button"
                    onClick={refundPayment}
                    disabled={refunding}
                    className="w-full rounded-xl bg-red-500/15 px-4 py-3 text-sm font-semibold text-red-300 transition hover:bg-red-500/25 disabled:opacity-50"
                  >
                    {refunding ? "Refunding..." : "Refund payment"}
                  </button>
                )}
              </div>
            )}
          </aside>
        </div>
      )}
    </div>
  );
}

function SummaryCard({
  title,
  value,
}) {
  return (
    <div
      className="
        rounded-2xl
        border border-white/10
        bg-white/[0.03]
        p-5
      "
    >
      <p className="text-xs uppercase tracking-wider text-zinc-600">
        {title}
      </p>

      <h2 className="mt-3 text-3xl font-black">
        {value}
      </h2>
    </div>
  );
}

function Detail({
  label,
  value,
}) {
  return (
    <div>
      <p className="text-xs text-zinc-600">
        {label}
      </p>

      <p
        className="
          mt-1 break-all
          text-sm text-zinc-300
        "
      >
        {value ?? "—"}
      </p>
    </div>
  );
}

export default AdminPayments;