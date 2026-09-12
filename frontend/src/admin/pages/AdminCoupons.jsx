import { useEffect, useState } from "react";
import api from "../../api/axios";

const initialForm = {
  code: "",
  discountType: "PERCENTAGE",
  discountValue: "",
  minimumAmount: "",
  expiryDate: "",
  active: true,
};

function AdminCoupons() {
  const [coupons, setCoupons] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const loadCoupons = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/admin/coupons");
      setCoupons(response.data || []);
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
          "Unable to load coupons."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCoupons();
  }, []);

  const handleChange = (event) => {
    const { name, value, type, checked } =
      event.target;

    setForm((current) => ({
      ...current,
      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);

      const payload = {
        ...form,

        discountValue: Number(
          form.discountValue
        ),

        minimumAmount:
          form.minimumAmount
            ? Number(
                form.minimumAmount
              )
            : 0,
      };

      const response = await api.post(
        "/admin/coupons",
        payload
      );

      setCoupons((current) => [
        ...current,
        response.data,
      ]);

      setForm(initialForm);
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to create coupon."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleToggle = async (coupon) => {
    try {
      const response = await api.put(
        `/admin/coupons/${coupon.id}`,
        {
          ...coupon,
          active: !coupon.active,
        }
      );

      setCoupons((current) =>
        current.map((item) =>
          item.id === coupon.id
            ? response.data
            : item
        )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to update coupon."
      );
    }
  };

  const handleDelete = async (id) => {
    if (
      !window.confirm(
        "Are you sure you want to delete this coupon?"
      )
    ) {
      return;
    }

    try {
      await api.delete(
        `/admin/coupons/${id}`
      );

      setCoupons((current) =>
        current.filter(
          (coupon) =>
            coupon.id !== id
        )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to delete coupon."
      );
    }
  };

  return (
    <div>
      <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
        PROMOTIONS
      </p>

      <h1 className="mt-3 text-4xl font-black">
        Coupons
      </h1>

      <p className="mt-2 text-zinc-500">
        Create and manage promotional
        discounts.
      </p>

      <div
        className="
          mt-8 grid
          grid-cols-1 gap-6
          xl:grid-cols-[400px_1fr]
        "
      >
        {/* CREATE FORM */}

        <form
          onSubmit={handleSubmit}
          className="
            h-fit rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-6
          "
        >
          <h2 className="text-xl font-bold">
            Create Coupon
          </h2>

          <div className="mt-6 space-y-4">
            <div>
              <label className="text-xs text-zinc-500">
                Coupon Code
              </label>

              <input
                name="code"
                value={form.code}
                onChange={handleChange}
                placeholder="PULSE10"
                required
                className="
                  mt-2 w-full
                  rounded-xl
                  border border-white/10
                  bg-black/20
                  px-4 py-3
                  uppercase
                  outline-none
                "
              />
            </div>

            <div>
              <label className="text-xs text-zinc-500">
                Discount %
              </label>

              <input
                type="number"
                name="discountValue"
                value={
                  form.discountValue
                }
                onChange={handleChange}
                min="0"
                max="100"
                required
                className="
                  mt-2 w-full
                  rounded-xl
                  border border-white/10
                  bg-black/20
                  px-4 py-3
                  outline-none
                "
              />
            </div>

            <div>
              <label className="text-xs text-zinc-500">
                Minimum Order Amount
              </label>

              <input
                type="number"
                name="minimumAmount"
                value={
                  form.minimumAmount
                }
                onChange={handleChange}
                className="
                  mt-2 w-full
                  rounded-xl
                  border border-white/10
                  bg-black/20
                  px-4 py-3
                  outline-none
                "
              />
            </div>

            <div>
              <label className="text-xs text-zinc-500">
                Expiry Date
              </label>

              <input
                type="date"
                name="expiryDate"
                value={form.expiryDate}
                onChange={handleChange}
                required
                className="
                  mt-2 w-full
                  rounded-xl
                  border border-white/10
                  bg-[#0b0d10]
                  px-4 py-3
                  outline-none
                "
              />
            </div>

            <label className="flex items-center gap-3 text-sm">
              <input
                type="checkbox"
                name="active"
                checked={form.active}
                onChange={handleChange}
              />

              Active
            </label>
          </div>

          <button
            disabled={saving}
            className="
              mt-6 w-full
              rounded-xl
              bg-white
              px-5 py-3
              font-bold text-black
              disabled:opacity-50
            "
          >
            {saving
              ? "Creating..."
              : "Create Coupon"}
          </button>
        </form>

        {/* COUPON LIST */}

        <div>
          {loading && (
            <p className="text-zinc-500">
              Loading coupons...
            </p>
          )}

          {error && (
            <div className="rounded-xl border border-red-500/20 bg-red-500/5 p-4 text-red-300">
              {error}
            </div>
          )}

          {!loading &&
            !error &&
            coupons.length === 0 && (
              <div className="rounded-2xl border border-white/10 p-10 text-center text-zinc-600">
                No coupons created.
              </div>
            )}

          <div className="grid gap-4">
            {coupons.map((coupon) => (
              <div
                key={coupon.id}
                className="
                  rounded-2xl
                  border border-white/10
                  bg-white/[0.03]
                  p-5
                "
              >
                <div className="flex flex-col justify-between gap-5 sm:flex-row sm:items-center">
                  <div>
                    <div className="flex items-center gap-3">
                      <h3 className="text-xl font-black">
                        {coupon.code}
                      </h3>

                      <span
                        className={`
                          rounded-full
                          px-3 py-1
                          text-xs

                          ${
                            coupon.active
                              ? "bg-green-500/10 text-green-300"
                              : "bg-red-500/10 text-red-300"
                          }
                        `}
                      >
                        {coupon.active
                          ? "Active"
                          : "Inactive"}
                      </span>
                    </div>

                    <p className="mt-3 text-2xl font-black text-blue-300">
                      {
                        coupon.discountValue
                      }
                      % OFF
                    </p>

                    <p className="mt-2 text-xs text-zinc-600">
                      Expires:{" "}
                      {coupon.expiryDate ||
                        "—"}
                    </p>
                  </div>

                  <div className="flex gap-2">
                    <button
                      onClick={() =>
                        handleToggle(
                          coupon
                        )
                      }
                      className="
                        rounded-lg
                        border border-white/10
                        px-3 py-2
                        text-xs
                      "
                    >
                      {coupon.active
                        ? "Deactivate"
                        : "Activate"}
                    </button>

                    <button
                      onClick={() =>
                        handleDelete(
                          coupon.id
                        )
                      }
                      className="
                        rounded-lg
                        border border-red-400/20
                        bg-red-500/5
                        px-3 py-2
                        text-xs
                        text-red-300
                      "
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

export default AdminCoupons;