import {
  useEffect,
  useState,
} from "react";

import api from "../../api/axios";

function AdminProfile() {
  const [profile, setProfile] =
    useState(null);

  const [form, setForm] =
    useState({
      firstName: "",
      lastName: "",
      phone: "",
      address: "",
    });

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [message, setMessage] =
    useState("");

  const [error, setError] =
    useState("");

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const response =
          await api.get("/users/me");

        const user = response.data;

        setProfile(user);

        setForm({
          firstName:
            user.firstName || "",

          lastName:
            user.lastName || "",

          phone:
            user.phone || "",

          address:
            user.address || "",
        });
      } catch (error) {
        setError(
          error.response?.data
            ?.message ||
            "Unable to load profile."
        );
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, []);

  const handleChange = (event) => {
    setForm((current) => ({
      ...current,

      [event.target.name]:
        event.target.value,
    }));
  };

  const handleSubmit =
    async (event) => {
      event.preventDefault();

      try {
        setSaving(true);
        setMessage("");
        setError("");

        const response =
          await api.put(
            "/users/me",
            form
          );

        setProfile(
          response.data
        );

        setMessage(
          "Profile updated successfully."
        );
      } catch (error) {
        setError(
          error.response?.data
            ?.message ||
            "Unable to update profile."
        );
      } finally {
        setSaving(false);
      }
    };

  if (loading) {
    return (
      <p className="text-zinc-500">
        Loading profile...
      </p>
    );
  }

  return (
    <div className="max-w-5xl">
      <p className="text-xs font-bold tracking-[0.3em] text-blue-400">
        ACCOUNT
      </p>

      <h1 className="mt-3 text-4xl font-black">
        Admin Profile
      </h1>

      <p className="mt-2 text-zinc-500">
        Manage your PulseDrive administrator
        information.
      </p>

      <div
        className="
          mt-8 grid
          grid-cols-1 gap-6
          lg:grid-cols-[300px_1fr]
        "
      >
        {/* PROFILE CARD */}

        <div
          className="
            h-fit rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-6 text-center
          "
        >
          <div
            className="
              mx-auto flex
              h-24 w-24
              items-center
              justify-center
              rounded-full
              bg-blue-500/10
              text-3xl
              font-black
              text-blue-300
            "
          >
            {profile?.firstName
              ?.charAt(0)
              ?.toUpperCase() ||
              "A"}
          </div>

          <h2 className="mt-5 text-xl font-bold">
            {profile?.firstName}{" "}
            {profile?.lastName}
          </h2>

          <p className="mt-1 break-all text-sm text-zinc-500">
            {profile?.email}
          </p>

          <span
            className="
              mt-4 inline-block
              rounded-full
              border
              border-blue-400/20
              bg-blue-500/10
              px-3 py-1
              text-xs
              font-bold
              text-blue-300
            "
          >
            {profile?.role ||
              "ADMIN"}
          </span>

          <div className="mt-6 border-t border-white/10 pt-5">
            <p className="text-xs text-zinc-600">
              Account ID
            </p>

            <p className="mt-1 text-sm">
              #{profile?.id}
            </p>
          </div>
        </div>

        {/* PROFILE FORM */}

        <form
          onSubmit={handleSubmit}
          className="
            rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-6
          "
        >
          <h2 className="text-xl font-bold">
            Personal Information
          </h2>

          {message && (
            <div
              className="
                mt-5 rounded-xl
                border
                border-green-500/20
                bg-green-500/5
                p-4
                text-sm
                text-green-300
              "
            >
              {message}
            </div>
          )}

          {error && (
            <div
              className="
                mt-5 rounded-xl
                border
                border-red-500/20
                bg-red-500/5
                p-4
                text-sm
                text-red-300
              "
            >
              {error}
            </div>
          )}

          <div
            className="
              mt-6 grid
              grid-cols-1 gap-5
              sm:grid-cols-2
            "
          >
            <ProfileInput
              label="First Name"
              name="firstName"
              value={form.firstName}
              onChange={
                handleChange
              }
            />

            <ProfileInput
              label="Last Name"
              name="lastName"
              value={form.lastName}
              onChange={
                handleChange
              }
            />

            <ProfileInput
              label="Phone"
              name="phone"
              value={form.phone}
              onChange={
                handleChange
              }
            />

            <div>
              <label className="text-xs text-zinc-500">
                Email
              </label>

              <input
                value={
                  profile?.email ||
                  ""
                }
                disabled
                className="
                  mt-2 w-full
                  rounded-xl
                  border
                  border-white/5
                  bg-black/10
                  px-4 py-3
                  text-zinc-600
                "
              />
            </div>
          </div>

          <div className="mt-5">
            <label className="text-xs text-zinc-500">
              Address
            </label>

            <textarea
              name="address"
              value={form.address}
              onChange={
                handleChange
              }
              rows="4"
              className="
                mt-2 w-full
                resize-none
                rounded-xl
                border border-white/10
                bg-black/20
                px-4 py-3
                outline-none
                focus:border-blue-400/40
              "
            />
          </div>

          <button
            disabled={saving}
            className="
              mt-6 rounded-xl
              bg-white
              px-6 py-3
              font-bold text-black
              disabled:opacity-50
            "
          >
            {saving
              ? "Saving..."
              : "Save Changes"}
          </button>
        </form>
      </div>
    </div>
  );
}

function ProfileInput({
  label,
  name,
  value,
  onChange,
}) {
  return (
    <div>
      <label className="text-xs text-zinc-500">
        {label}
      </label>

      <input
        name={name}
        value={value}
        onChange={onChange}
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
  );
}

export default AdminProfile;