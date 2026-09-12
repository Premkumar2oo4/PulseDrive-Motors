import {
  useEffect,
  useState,
} from "react";

import {
  Link,
  useParams,
} from "react-router-dom";

import api from "../../api/axios";

function AdminVehicleMedia() {
  const { id } = useParams();

  const [vehicle, setVehicle] =
    useState(null);

  const [images, setImages] =
    useState([]);

  const [features, setFeatures] =
    useState([]);

  const [featureName, setFeatureName] =
    useState("");

  const [imageUrl, setImageUrl] =
    useState("");

  const [selectedFiles, setSelectedFiles] =
    useState([]);

  const [uploading, setUploading] =
    useState(false);

  const [imageType, setImageType] =
    useState("EXTERIOR");

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        vehicleResponse,
        imageResponse,
        featureResponse,
      ] = await Promise.all([
        api.get(`/vehicles/${id}`),

        api.get(
          `/vehicle-images/vehicle/${id}`
        ),

        api.get(
          `/vehicle-features/vehicle/${id}`
        ),
      ]);

      setVehicle(
        vehicleResponse.data
      );

      setImages(
        imageResponse.data || []
      );

      setFeatures(
        featureResponse.data || []
      );
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
          "Unable to load vehicle media."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id]);

  const handleCreateImage =
    async (event) => {
      event.preventDefault();

      if (!imageUrl.trim()) {
        return;
      }

      try {
        const response =
          await api.post(
            "/vehicle-images",
            {
              imageUrl:
                imageUrl.trim(),

              imageType,

              vehicleId:
                Number(id),
            }
          );

        setImages(
          (current) => [
            ...current,
            response.data,
          ]
        );

        setImageUrl("");
      } catch (error) {
        alert(
          error.response?.data
            ?.message ||
            "Unable to save the external image URL."
        );
      }
    };

  const handleUploadImages = async (event) => {
    event.preventDefault();

    if (!selectedFiles.length) {
      return;
    }

    try {
      setUploading(true);

      for (const file of selectedFiles) {
        const formData = new FormData();
        formData.append("file", file);
        formData.append("imageType", imageType);

        const response = await api.post(
          `/vehicle-images/upload/${id}`,
          formData
        );

        setImages((current) => [
          ...current,
          response.data,
        ]);
      }

      setSelectedFiles([]);
      event.target.reset();
    } catch (error) {
      alert(
        error.response?.data?.message ||
          "Unable to upload image."
      );
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteImage =
    async (imageId) => {
      const confirmed =
        window.confirm(
          "Delete this image?"
        );

      if (!confirmed) {
        return;
      }

      try {
        await api.delete(
          `/vehicle-images/${imageId}`
        );

        setImages(
          (current) =>
            current.filter(
              (image) =>
                image.id !==
                imageId
            )
        );
      } catch (error) {
        alert(
          error.response?.data
            ?.message ||
            "Unable to delete image."
        );
      }
    };

  const handleCreateFeature =
    async (event) => {
      event.preventDefault();

      if (!featureName.trim()) {
        return;
      }

      try {
        const response =
          await api.post(
            "/vehicle-features",
            {
              featureName:
                featureName.trim(),

              vehicleId:
                Number(id),
            }
          );

        setFeatures(
          (current) => [
            ...current,
            response.data,
          ]
        );

        setFeatureName("");
      } catch (error) {
        alert(
          error.response?.data
            ?.message ||
            "Unable to create feature."
        );
      }
    };

  const handleDeleteFeature =
    async (featureId) => {
      const confirmed =
        window.confirm(
          "Delete this feature?"
        );

      if (!confirmed) {
        return;
      }

      try {
        await api.delete(
          `/vehicle-features/${featureId}`
        );

        setFeatures(
          (current) =>
            current.filter(
              (feature) =>
                feature.id !==
                featureId
            )
        );
      } catch (error) {
        alert(
          error.response?.data
            ?.message ||
            "Unable to delete feature."
        );
      }
    };

  if (loading) {
    return (
      <div className="text-zinc-500">
        Loading vehicle media...
      </div>
    );
  }

  if (error) {
    return (
      <div
        className="
          rounded-2xl
          border border-red-500/20
          bg-red-500/5
          p-5
          text-red-300
        "
      >
        {error}
      </div>
    );
  }

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
            VEHICLE MEDIA
          </p>

          <h1
            className="
              mt-3 text-4xl
              font-black
              tracking-tight
            "
          >
            {vehicle?.brand}{" "}
            {vehicle?.model}
          </h1>

          <p className="mt-2 text-zinc-500">
            Manage images and vehicle
            features.
          </p>

          <p className="mt-3 text-sm text-blue-300">
            Upload Images for {vehicle?.brand} {vehicle?.model}
          </p>
        </div>

        <Link
          to={`/admin/vehicles/${id}/edit`}
          className="
            rounded-xl
            border border-white/10
            bg-white/5
            px-5 py-3
            text-sm
            text-zinc-300
          "
        >
          Edit Vehicle
        </Link>
      </div>

      {/* IMAGE MANAGEMENT */}

      <section className="mt-10">
        <div className="flex items-end justify-between">
          <div>
            <p
              className="
                text-xs font-bold
                tracking-[0.25em]
                text-blue-400
              "
            >
              GALLERY
            </p>

            <h2 className="mt-2 text-2xl font-bold">
              Vehicle Images
            </h2>
          </div>

          <span className="text-sm text-zinc-600">
            {images.length} images
          </span>
        </div>

        <form
          onSubmit={handleUploadImages}
          className="
            mt-6 rounded-2xl
            border border-dashed
            border-blue-400/20
            bg-blue-500/[0.03]
            p-6
          "
        >
          <p className="font-semibold">Upload from your computer</p>

          <p className="mt-2 text-sm text-zinc-500">
            Images are uploaded to Cloudinary and the returned URL is saved
            against this vehicle automatically.
          </p>

          <div className="mt-4 flex flex-col gap-3 sm:flex-row">
            <input
              type="file"
              multiple
              accept="image/jpeg,image/png,image/webp"
              onChange={(event) =>
                setSelectedFiles(
                  Array.from(event.target.files || [])
                )
              }
              className="
                flex-1 rounded-xl
                border border-white/10
                bg-black/20
                px-4 py-3
                text-sm
                text-zinc-600
              "
            />

            <button
              type="submit"
              disabled={uploading || !selectedFiles.length}
              className="
                rounded-xl
                bg-blue-500/20
                px-5 py-3
                text-sm
                text-blue-300
                disabled:cursor-not-allowed
                disabled:opacity-50
              "
            >
              {uploading ? "Uploading..." : "Upload Images"}
            </button>
          </div>
        </form>

        {/* EXTERNAL URL IMAGE FORM */}

        <form
          onSubmit={
            handleCreateImage
          }
          className="
            mt-6 grid
            grid-cols-1 gap-3
            rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-5
            lg:grid-cols-[1fr_180px_auto]
          "
        >
          <div className="lg:col-span-3">
            <p className="font-semibold">Add an external image URL</p>
            <p className="mt-1 text-sm text-zinc-500">
              Use this only for an image already hosted online. To upload a file, use the form above.
            </p>
          </div>

          <input
            type="url"
            value={imageUrl}
            onChange={(event) =>
              setImageUrl(
                event.target.value
              )
            }
            placeholder="https://example.com/car.jpg"
            required
            className="
              rounded-xl
              border border-white/10
              bg-black/20
              px-4 py-3
              text-sm
              outline-none
              focus:border-blue-400/40
            "
          />

          <select
            value={imageType}
            onChange={(event) =>
              setImageType(
                event.target.value
              )
            }
            className="
              rounded-xl
              border border-white/10
              bg-[#0b0d10]
              px-4 py-3
            "
          >
            <option value="EXTERIOR">
              Exterior
            </option>

            <option value="INTERIOR">
              Interior
            </option>

            <option value="DASHBOARD">
              Dashboard
            </option>

            <option value="ENGINE">
              Engine
            </option>

            <option value="OTHER">
              Other
            </option>
          </select>

          <button
            type="submit"
            className="
              rounded-xl
              bg-white
              px-5 py-3
              text-sm
              font-bold
              text-black
            "
          >
            Add URL Image
          </button>
        </form>

        <div
          className="
            mt-6 grid
            grid-cols-1 gap-5
            md:grid-cols-2
            xl:grid-cols-3
          "
        >
          {images.map(
            (image) => (
              <div
                key={image.id}
                className="
                  overflow-hidden
                  rounded-2xl
                  border border-white/10
                  bg-white/[0.03]
                "
              >
                <div className="h-52 bg-[#0b0d10]">
                  <img
                    src={
                      image.imageUrl
                    }
                    alt={
                      image.imageType
                    }
                    className="
                      h-full w-full
                      object-cover
                    "
                  />
                </div>

                <div className="p-4">
                  <div
                    className="
                      flex items-center
                      justify-between
                    "
                  >
                    <div>
                      <p className="text-xs font-semibold text-blue-300">
                        {
                          image.imageType
                        }
                      </p>

                      <p
                        className="
                          mt-1 max-w-[220px]
                          truncate
                          text-xs
                          text-zinc-600
                        "
                      >
                        {image.imageUrl}
                      </p>
                    </div>

                    <button
                      onClick={() =>
                        handleDeleteImage(
                          image.id
                        )
                      }
                      className="
                        rounded-lg
                        border
                        border-red-400/20
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
            )
          )}
        </div>

        {images.length === 0 && (
          <div
            className="
              mt-6 rounded-2xl
              border border-white/10
              p-10
              text-center
              text-zinc-600
            "
          >
            No images added yet.
          </div>
        )}
      </section>

      {/* FEATURES */}

      <section className="mt-16">
        <div>
          <p
            className="
              text-xs font-bold
              tracking-[0.25em]
              text-blue-400
            "
          >
            SPECIFICATION
          </p>

          <h2 className="mt-2 text-2xl font-bold">
            Vehicle Features
          </h2>
        </div>

        <form
          onSubmit={
            handleCreateFeature
          }
          className="
            mt-6 flex
            flex-col gap-3
            rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-5
            sm:flex-row
          "
        >
          <input
            type="text"
            value={
              featureName
            }
            onChange={(event) =>
              setFeatureName(
                event.target.value
              )
            }
            placeholder="Example: Adaptive Cruise Control"
            required
            className="
              flex-1
              rounded-xl
              border border-white/10
              bg-black/20
              px-4 py-3
              outline-none
              focus:border-blue-400/40
            "
          />

          <button
            className="
              rounded-xl
              bg-white
              px-5 py-3
              text-sm
              font-bold
              text-black
            "
          >
            + Add Feature
          </button>
        </form>

        <div
          className="
            mt-6 grid
            grid-cols-1 gap-3
            md:grid-cols-2
            xl:grid-cols-3
          "
        >
          {features.map(
            (feature) => (
              <div
                key={
                  feature.id
                }
                className="
                  flex
                  items-center
                  justify-between
                  rounded-xl
                  border border-white/10
                  bg-white/[0.03]
                  p-4
                "
              >
                <span className="text-sm text-zinc-300">
                  {
                    feature.featureName
                  }
                </span>

                <button
                  onClick={() =>
                    handleDeleteFeature(
                      feature.id
                    )
                  }
                  className="
                    text-xs
                    text-red-300
                    transition
                    hover:text-red-200
                  "
                >
                  Delete
                </button>
              </div>
            )
          )}
        </div>

        {features.length ===
          0 && (
          <p className="mt-6 text-zinc-600">
            No features added yet.
          </p>
        )}
      </section>
    </div>
  );
}

export default AdminVehicleMedia;