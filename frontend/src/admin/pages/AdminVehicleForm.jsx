import {
  useEffect,
  useState,
} from "react";

import {
  useNavigate,
  useParams,
} from "react-router-dom";

import api from "../../api/axios";

const initialForm = {
  brand: "",
  model: "",
  variant: "",
  year: "",
  price: "",
  discount: 0,
  fuelType: "",
  transmission: "",
  engine: "",
  horsepower: "",
  torque: "",
  mileage: "",
  bodyType: "",
  color: "",
  seatingCapacity: "",
  driveType: "",
  safetyRating: "",
  description: "",
  stock: 0,
  available: true,
  categoryId: "",
};

function AdminVehicleForm() {
  const { id } = useParams();

  const navigate = useNavigate();

  const editing = Boolean(id);

  const [form, setForm] =
    useState(initialForm);

  const [categories, setCategories] =
    useState([]);

  const [loading, setLoading] =
    useState(editing);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  useEffect(() => {
    const initialize = async () => {
      try {
        const categoryResponse =
          await api.get(
            "/categories"
          );

        setCategories(
          categoryResponse.data || []
        );

        if (editing) {
          const vehicleResponse =
            await api.get(
              `/vehicles/${id}`
            );

          const vehicle =
            vehicleResponse.data;

          setForm({
            brand:
              vehicle.brand ?? "",
            model:
              vehicle.model ?? "",
            variant:
              vehicle.variant ?? "",
            year:
              vehicle.year ?? "",
            price:
              vehicle.price ?? "",
            discount:
              vehicle.discount ?? 0,
            fuelType:
              vehicle.fuelType ?? "",
            transmission:
              vehicle.transmission ??
              "",
            engine:
              vehicle.engine ?? "",
            horsepower:
              vehicle.horsepower ?? "",
            torque:
              vehicle.torque ?? "",
            mileage:
              vehicle.mileage ?? "",
            bodyType:
              vehicle.bodyType ?? "",
            color:
              vehicle.color ?? "",
            seatingCapacity:
              vehicle.seatingCapacity ??
              "",
            driveType:
              vehicle.driveType ?? "",
            safetyRating:
              vehicle.safetyRating ??
              "",
            description:
              vehicle.description ?? "",
            stock:
              vehicle.stock ?? 0,
            available:
              vehicle.available ??
              true,
            categoryId:
              vehicle.categoryId ?? "",
          });
        }
      } catch (error) {
        setError(
          error.response?.data
            ?.message ||
            "Unable to load vehicle form."
        );
      } finally {
        setLoading(false);
      }
    };

    initialize();
  }, [editing, id]);

  const handleChange = (event) => {
    const {
      name,
      value,
      type,
      checked,
    } = event.target;

    setForm((current) => ({
      ...current,

      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  };

  const handleSubmit =
    async (event) => {
      event.preventDefault();

      try {
        setSaving(true);
        setError("");

        const payload = {
          ...form,

          year:
            Number(form.year),

          price:
            Number(form.price),

          discount:
            Number(form.discount),

          horsepower:
            form.horsepower
              ? Number(
                  form.horsepower
                )
              : null,

          torque:
            form.torque
              ? Number(form.torque)
              : null,

          mileage:
            form.mileage
              ? Number(form.mileage)
              : null,

          seatingCapacity:
            form.seatingCapacity
              ? Number(
                  form.seatingCapacity
                )
              : null,

          safetyRating:
            form.safetyRating
              ? Number(
                  form.safetyRating
                )
              : null,

          stock:
            Number(form.stock),

          categoryId:
            Number(
              form.categoryId
            ),
        };

        if (editing) {
          await api.put(
            `/vehicles/${id}`,
            payload
          );
        } else {
          await api.post(
            "/vehicles",
            payload
          );
        }

        navigate(
          "/admin/vehicles"
        );
      } catch (error) {
        setError(
          error.response?.data
            ?.message ||
            "Unable to save vehicle."
        );
      } finally {
        setSaving(false);
      }
    };

  if (loading) {
    return (
      <div className="text-zinc-500">
        Loading vehicle...
      </div>
    );
  }

  const fields = [
    ["brand", "Brand", "text"],
    ["model", "Model", "text"],
    ["variant", "Variant", "text"],
    ["year", "Year", "number"],
    ["price", "Price", "number"],
    ["discount", "Discount", "number"],
    [
      "fuelType",
      "Fuel Type",
      "text",
    ],
    [
      "transmission",
      "Transmission",
      "text",
    ],
    ["engine", "Engine", "text"],
    [
      "horsepower",
      "Horsepower",
      "number",
    ],
    ["torque", "Torque", "number"],
    [
      "mileage",
      "Mileage",
      "number",
    ],
    [
      "bodyType",
      "Body Type",
      "text",
    ],
    ["color", "Color", "text"],
    [
      "seatingCapacity",
      "Seating Capacity",
      "number",
    ],
    [
      "driveType",
      "Drive Type",
      "text",
    ],
    [
      "safetyRating",
      "Safety Rating",
      "number",
    ],
    ["stock", "Stock", "number"],
  ];

  return (
    <div className="max-w-6xl">
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
        "
      >
        {editing
          ? "Edit Vehicle"
          : "Add Vehicle"}
      </h1>

      {error && (
        <div
          className="
            mt-6 rounded-xl
            border border-red-500/20
            bg-red-500/5
            p-4 text-red-300
          "
        >
          {error}
        </div>
      )}

      <form
        onSubmit={handleSubmit}
        className="
          mt-8 grid
          grid-cols-1 gap-5
          rounded-2xl
          border border-white/10
          bg-white/[0.03]
          p-6
          md:grid-cols-2
          xl:grid-cols-3
        "
      >
        {fields.map(
          ([name, label, type]) => (
            <div key={name}>
              <label className="text-xs text-zinc-500">
                {label}
              </label>

              <input
                type={type}
                name={name}
                value={form[name]}
                onChange={handleChange}
                required={[
                  "brand",
                  "model",
                  "year",
                  "price",
                  "fuelType",
                  "transmission",
                  "bodyType",
                  "stock",
                ].includes(name)}
                className="
                  mt-2 w-full
                  rounded-xl
                  border border-white/10
                  bg-black/20
                  px-4 py-3
                  outline-none
                  focus:border-blue-400/40
                "
              />
            </div>
          )
        )}

        <div>
          <label className="text-xs text-zinc-500">
            Category
          </label>

          <select
            name="categoryId"
            value={form.categoryId}
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
          >
            <option value="">
              Select category
            </option>

            {categories.map(
              (category) => (
                <option
                  key={category.id}
                  value={category.id}
                >
                  {category.name}
                </option>
              )
            )}
          </select>
        </div>

        <div className="flex items-center gap-3 pt-7">
          <input
            type="checkbox"
            name="available"
            checked={
              form.available
            }
            onChange={
              handleChange
            }
          />

          <span className="text-sm">
            Vehicle available
          </span>
        </div>

        <div className="md:col-span-2 xl:col-span-3">
          <label className="text-xs text-zinc-500">
            Description
          </label>

          <textarea
            name="description"
            value={
              form.description
            }
            onChange={
              handleChange
            }
            rows="5"
            className="
              mt-2 w-full
              resize-none
              rounded-xl
              border border-white/10
              bg-black/20
              px-4 py-3
              outline-none
            "
          />
        </div>

        <div
          className="
            flex gap-3
            md:col-span-2
            xl:col-span-3
          "
        >
          <button
            type="button"
            onClick={() =>
              navigate(
                "/admin/vehicles"
              )
            }
            className="
              rounded-xl
              border border-white/10
              px-5 py-3
              text-zinc-400
            "
          >
            Cancel
          </button>

          <button
            disabled={saving}
            className="
              rounded-xl
              bg-white
              px-6 py-3
              font-bold
              text-black
              disabled:opacity-50
            "
          >
            {saving
              ? "Saving..."
              : editing
              ? "Update Vehicle"
              : "Create Vehicle"}
          </button>
        </div>
      </form>
    </div>
  );
}

export default AdminVehicleForm;