import { useEffect, useState } from "react";
import api from "../../api/axios";

const emptyForm = {
  name: "",
  description: "",
  imageUrl: "",
};

function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const loadCategories = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/categories");
      setCategories(response.data || []);
    } catch (error) {
      setError(
        error.response?.data?.message ||
          "Unable to load categories."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleChange = (event) => {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    });
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleEdit = (category) => {
    setEditingId(category.id);

    setForm({
      name: category.name || "",
      description: category.description || "",
      imageUrl: category.imageUrl || "",
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);

      if (editingId) {
        const response = await api.put(
          `/categories/${editingId}`,
          form
        );

        setCategories((current) =>
          current.map((category) =>
            category.id === editingId
              ? response.data
              : category
          )
        );
      } else {
        const response = await api.post(
          "/categories",
          form
        );

        setCategories((current) => [
          ...current,
          response.data,
        ]);
      }

      resetForm();
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to save category."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (
      !window.confirm(
        "Delete this category?"
      )
    ) {
      return;
    }

    try {
      await api.delete(`/categories/${id}`);

      setCategories((current) =>
        current.filter(
          (category) => category.id !== id
        )
      );
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to delete category."
      );
    }
  };

  return (
    <div>
      <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
        INVENTORY
      </p>

      <h1 className="mt-3 text-4xl font-black">
        Categories
      </h1>

      <p className="mt-2 text-zinc-500">
        Manage vehicle categories used across PulseDrive.
      </p>

      <div className="mt-8 grid grid-cols-1 gap-6 xl:grid-cols-[380px_1fr]">
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
              ? "Edit Category"
              : "Add Category"}
          </h2>

          <div className="mt-6 space-y-4">
            <input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Category name"
              required
              className="
                w-full rounded-xl
                border border-white/10
                bg-black/20
                px-4 py-3
                outline-none
                focus:border-blue-400/40
              "
            />

            <textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Description"
              rows="4"
              className="
                w-full resize-none
                rounded-xl
                border border-white/10
                bg-black/20
                px-4 py-3
                outline-none
              "
            />

            <input
              name="imageUrl"
              value={form.imageUrl}
              onChange={handleChange}
              placeholder="Category image URL"
              className="
                w-full rounded-xl
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
                ? "Update Category"
                : "Create Category"}
            </button>
          </div>
        </form>

        <div
          className="
            overflow-hidden rounded-2xl
            border border-white/10
            bg-white/[0.02]
          "
        >
          {loading ? (
            <div className="p-8 text-zinc-500">
              Loading categories...
            </div>
          ) : error ? (
            <div className="p-8 text-red-300">
              {error}
            </div>
          ) : (
            <div className="divide-y divide-white/5">
              {categories.map((category) => (
                <div
                  key={category.id}
                  className="
                    flex items-center
                    justify-between gap-4
                    p-5
                    transition
                    hover:bg-white/[0.03]
                  "
                >
                  <div className="flex items-center gap-4">
                    <div
                      className="
                        flex h-14 w-14
                        items-center justify-center
                        overflow-hidden
                        rounded-xl
                        bg-white/5
                      "
                    >
                      {category.imageUrl ? (
                        <img
                          src={category.imageUrl}
                          alt={category.name}
                          className="h-full w-full object-cover"
                        />
                      ) : (
                        <span className="text-lg font-black text-white/10">
                          {category.name?.charAt(0)}
                        </span>
                      )}
                    </div>

                    <div>
                      <h3 className="font-semibold">
                        {category.name}
                      </h3>

                      <p className="mt-1 max-w-md text-sm text-zinc-600">
                        {category.description || "No description"}
                      </p>
                    </div>
                  </div>

                  <div className="flex gap-2">
                    <button
                      onClick={() =>
                        handleEdit(category)
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
                        handleDelete(category.id)
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
              ))}

              {categories.length === 0 && (
                <div className="p-10 text-center text-zinc-600">
                  No categories found.
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminCategories;