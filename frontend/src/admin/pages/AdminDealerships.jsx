import { useEffect, useState } from "react";
import api from "../../api/axios";

const emptyForm = {
  name: "",
  address: "",
  city: "",
  state: "",
  phone: "",
  email: "",
  openingHours: "",
  latitude: "",
  longitude: "",
};

function AdminDealerships() {
  const [dealerships, setDealerships] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const [search, setSearch] = useState("");
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);

  const loadDealerships = async () => {
    try {
      const response =
        await api.get("/dealerships");

      setDealerships(response.data || []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDealerships();
  }, []);

  const handleChange = (event) => {
    setForm({
      ...form,
      [event.target.name]:
        event.target.value,
    });
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleEdit = (dealership) => {
    setEditingId(dealership.id);

    setForm({
      name: dealership.name || "",
      address: dealership.address || "",
      city: dealership.city || "",
      state: dealership.state || "",
      phone: dealership.phone || "",
      email: dealership.email || "",
      openingHours:
        dealership.openingHours || "",
      latitude:
        dealership.latitude ?? "",
      longitude:
        dealership.longitude ?? "",
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const payload = {
      ...form,

      latitude:
        form.latitude !== ""
          ? Number(form.latitude)
          : null,

      longitude:
        form.longitude !== ""
          ? Number(form.longitude)
          : null,
    };

    try {
      setSaving(true);

      if (editingId) {
        const response =
          await api.put(
            `/dealerships/${editingId}`,
            payload
          );

        setDealerships((current) =>
          current.map((item) =>
            item.id === editingId
              ? response.data
              : item
          )
        );
      } else {
        const response =
          await api.post(
            "/dealerships",
            payload
          );

        setDealerships((current) => [
          ...current,
          response.data,
        ]);
      }

      resetForm();
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to save dealership."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (
      !window.confirm(
        "Delete this dealership?"
      )
    ) {
      return;
    }

    try {
      await api.delete(
        `/dealerships/${id}`
      );

      setDealerships((current) =>
        current.filter(
          (item) => item.id !== id
        )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to delete dealership."
      );
    }
  };

  const filtered =
    dealerships.filter((item) =>
      `${item.name} ${item.city} ${item.state}`
        .toLowerCase()
        .includes(search.toLowerCase())
    );

  const fields = [
    ["name", "Dealership Name", "text"],
    ["address", "Address", "text"],
    ["city", "City", "text"],
    ["state", "State", "text"],
    ["phone", "Phone", "text"],
    ["email", "Email", "email"],
    [
      "openingHours",
      "Opening Hours",
      "text",
    ],
    ["latitude", "Latitude", "number"],
    ["longitude", "Longitude", "number"],
  ];

  return (
    <div>
      <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
        LOCATIONS
      </p>

      <h1 className="mt-3 text-4xl font-black">
        Dealerships
      </h1>

      <p className="mt-2 text-zinc-500">
        Manage physical PulseDrive dealership locations.
      </p>

      <div className="mt-8 grid grid-cols-1 gap-6 xl:grid-cols-[430px_1fr]">
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
            {editingId
              ? "Edit Dealership"
              : "Add Dealership"}
          </h2>

          <div className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
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
                    step={
                      type === "number"
                        ? "any"
                        : undefined
                    }
                    required={[
                      "name",
                      "city",
                      "state",
                    ].includes(name)}
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
              )
            )}
          </div>

          <div className="mt-4">
            <label className="text-xs text-zinc-500">
              Address
            </label>

            <textarea
              name="address"
              value={form.address}
              onChange={handleChange}
              required
              rows="3"
              className="
                mt-2 w-full resize-none
                rounded-xl
                border border-white/10
                bg-black/20
                px-4 py-3
                outline-none
              "
            />
          </div>

          <div className="mt-5 flex gap-3">
            {editingId && (
              <button
                type="button"
                onClick={resetForm}
                className="
                  rounded-xl
                  border border-white/10
                  px-4 py-3
                  text-sm text-zinc-400
                "
              >
                Cancel
              </button>
            )}

            <button
              disabled={saving}
              className="
                flex-1 rounded-xl
                bg-white
                px-5 py-3
                font-bold text-black
                disabled:opacity-50
              "
            >
              {saving
                ? "Saving..."
                : editingId
                ? "Update Dealership"
                : "Create Dealership"}
            </button>
          </div>
        </form>

        <div>
          <div
            className="
              rounded-2xl
              border border-white/10
              bg-white/[0.03]
              p-4
            "
          >
            <input
              value={search}
              onChange={(event) =>
                setSearch(
                  event.target.value
                )
              }
              placeholder="Search dealership, city or state..."
              className="
                w-full rounded-xl
                border border-white/10
                bg-black/20
                px-4 py-3
                outline-none
              "
            />
          </div>

          <div className="mt-5 space-y-4">
            {loading ? (
              <p className="text-zinc-500">
                Loading dealerships...
              </p>
            ) : (
              filtered.map(
                (dealership) => (
                  <div
                    key={dealership.id}
                    className="
                      rounded-2xl
                      border border-white/10
                      bg-white/[0.03]
                      p-5
                    "
                  >
                    <div
                      className="
                        flex flex-col
                        justify-between gap-5
                        sm:flex-row
                      "
                    >
                      <div>
                        <h3 className="text-lg font-semibold">
                          {dealership.name}
                        </h3>

                        <p className="mt-2 text-sm text-zinc-500">
                          {dealership.address}
                        </p>

                        <p className="mt-1 text-sm text-zinc-600">
                          {dealership.city},{" "}
                          {dealership.state}
                        </p>

                        {dealership.openingHours && (
                          <p className="mt-3 text-xs text-blue-300">
                            {
                              dealership.openingHours
                            }
                          </p>
                        )}
                      </div>

                      <div className="flex gap-2">
                        <button
                          onClick={() =>
                            handleEdit(
                              dealership
                            )
                          }
                          className="
                            rounded-lg
                            border border-white/10
                            px-3 py-2
                            text-xs
                          "
                        >
                          Edit
                        </button>

                        <button
                          onClick={() =>
                            handleDelete(
                              dealership.id
                            )
                          }
                          className="
                            rounded-lg
                            border border-red-400/20
                            bg-red-500/5
                            px-3 py-2
                            text-xs text-red-300
                          "
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  </div>
                )
              )
            )}

            {!loading &&
              filtered.length === 0 && (
                <div className="rounded-2xl border border-white/10 p-10 text-center text-zinc-600">
                  No dealerships found.
                </div>
              )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default AdminDealerships;