import { useEffect, useState } from "react";
import { Link, useParams ,useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";
import api from "../api/axios";
import ThreeShowroom from "../components/ThreeShowroom";

function VehicleDetails() {
  const { id } = useParams();

  const [vehicle, setVehicle] = useState(null);
  const [images, setImages] = useState([]);
  const [features, setFeatures] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: "" });
  const [editingReviewId, setEditingReviewId] = useState(null);
  const [reviewMessage, setReviewMessage] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const navigate = useNavigate();

const {
  isAuthenticated,
  user,
} = useAuth();

const [wishlistLoading, setWishlistLoading] =
  useState(false);

const [wishlistMessage, setWishlistMessage] =
  useState("");
  const [cartMessage, setCartMessage] = useState("");
  const [dealerships, setDealerships] = useState([]);
  const [showBooking, setShowBooking] = useState(false);
  const [booking, setBooking] = useState({ dealershipId: "", date: "", time: "", notes: "" });
  const [bookingLoading, setBookingLoading] = useState(false);

  useEffect(() => {
    const loadDetails = async () => {
      try {
        setLoading(true);

        const [
          vehicleResponse,
          imagesResponse,
          featuresResponse,
          reviewsResponse,
        ] = await Promise.all([
          api.get(`/vehicles/${id}`),
          api.get(`/vehicle-images/vehicle/${id}`),
          api.get(`/vehicle-features/vehicle/${id}`),
          api.get(`/reviews/vehicle/${id}`),
        ]);

        setVehicle(vehicleResponse.data);
        setImages(imagesResponse.data || []);
        setFeatures(featuresResponse.data || []);
        setReviews(reviewsResponse.data || []);
      } catch (err) {
        console.error(err);
        setError("Unable to load vehicle details.");
      } finally {
        setLoading(false);
      }
    };

    loadDetails();
  }, [id]);

  const submitReview = async (event) => {
    event.preventDefault();
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    try {
      const payload = { vehicleId: Number(id), rating: Number(reviewForm.rating), comment: reviewForm.comment };
      const response = editingReviewId
        ? await api.put(`/reviews/${editingReviewId}`, payload)
        : await api.post("/reviews", payload);
      setReviews((current) => editingReviewId ? current.map((review) => review.id === editingReviewId ? response.data : review) : [response.data, ...current]);
      setReviewForm({ rating: 5, comment: "" });
      setEditingReviewId(null);
      setReviewMessage("Review saved.");
    } catch (requestError) {
      setReviewMessage(requestError.response?.data?.message || "Unable to save your review.");
    }
  };

  const deleteReview = async (reviewId) => {
    try {
      await api.delete(`/reviews/${reviewId}`);
      setReviews((current) => current.filter((review) => review.id !== reviewId));
    } catch (requestError) {
      setReviewMessage(requestError.response?.data?.message || "Unable to delete your review.");
    }
  };

  const loadDealerships = async () => {
    try {
      const response = await api.get("/dealerships");
      setDealerships(response.data || []);
    } catch {
      setWishlistMessage("Unable to load dealerships.");
    }
  };

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    try {
      await api.post("/cart", { vehicleId: vehicle.id, quantity: 1 });
      setCartMessage("Vehicle added to your cart.");
    } catch (error) {
      setCartMessage(error.response?.data?.message || "Unable to add vehicle to cart.");
    }
  };

  const handleBookingSubmit = async (event) => {
    event.preventDefault();
    try {
      setBookingLoading(true);
      await api.post("/test-drives", {
        ...booking,
        vehicleId: vehicle.id,
        dealershipId: Number(booking.dealershipId),
      });
      setShowBooking(false);
      setBooking({ dealershipId: "", date: "", time: "", notes: "" });
      setCartMessage("Test drive booked successfully.");
    } catch (error) {
      setCartMessage(error.response?.data?.message || "Unable to book this test drive.");
    } finally {
      setBookingLoading(false);
    }
  };
const handleAddToWishlist = async () => {
  if (!isAuthenticated) {
    navigate("/login");

    return;
  }

  try {
    setWishlistLoading(true);
    setWishlistMessage("");

    await api.post(
      `/wishlist/${vehicle.id}`
    );

    setWishlistMessage(
      "Vehicle added to your wishlist."
    );
  } catch (error) {
    console.error(
      "Wishlist error:",
      error
    );

    setWishlistMessage(
      error.response?.data?.message ||
        "Unable to add vehicle to wishlist."
    );
  } finally {
    setWishlistLoading(false);
  }
};
  if (loading) {
    return (
      <>
        <Navbar />

        <div className="flex min-h-screen items-center justify-center bg-[#050607] text-zinc-400">
          Loading vehicle...
        </div>
      </>
    );
  }

  if (error || !vehicle) {
    return (
      <>
        <Navbar />

        <div className="flex min-h-screen items-center justify-center bg-[#050607] text-red-300">
          {error || "Vehicle not found."}
        </div>
      </>
    );
  }

  const mainImage =
    images.length > 0
      ? images[0].imageUrl
      : null;

  return (
    <>
      <Navbar />

      <main className="min-h-screen bg-[#050607] px-[7%] pb-24 pt-32 text-white">

        <Link
          to="/home"
          className="text-sm text-zinc-500 transition hover:text-white"
        >
          ← Back to vehicles
        </Link>

        <section className="mt-10 grid grid-cols-1 gap-12 lg:grid-cols-[1.2fr_0.8fr]">

          <div
            className="
              flex min-h-[360px]
              items-center justify-center
              overflow-hidden
              rounded-3xl
              border border-white/10
              bg-[#0b0e11]
              lg:min-h-[520px]
            "
          >
            {mainImage ? (
              <img
                src={mainImage}
                alt={`${vehicle.brand} ${vehicle.model}`}
                className="h-full w-full object-cover"
              />
            ) : (
              <span className="text-5xl font-black text-white/10">
                {vehicle.brand}
              </span>
            )}
          </div>

          <div className="flex flex-col justify-center">

            <span
              className="
                text-xs font-bold
                uppercase
                tracking-[0.3em]
                text-blue-400
              "
            >
              {vehicle.brand}
            </span>

            <h1
              className="
                mt-4 text-5xl
                font-black
                tracking-[-3px]
                sm:text-6xl
              "
            >
              {vehicle.model}

              {vehicle.variant &&
                ` ${vehicle.variant}`}
            </h1>

            <p className="mt-6 max-w-xl leading-8 text-zinc-400">
              {vehicle.description}
            </p>

            <div className="mt-8 text-3xl font-bold">
              ₹{Number(vehicle.price).toLocaleString("en-IN")}
            </div>

            <div className="mt-8 flex flex-col gap-3 sm:flex-row">

              <button
  onClick={handleAddToWishlist}
  disabled={wishlistLoading}
  className="
    rounded-xl
    bg-white
    px-6 py-4
    font-bold
    text-black
    transition
    hover:-translate-y-1
    disabled:cursor-not-allowed
    disabled:opacity-50
  "
>
  {wishlistLoading
    ? "Adding..."
    : "♡ Add to Wishlist"}
</button>

              <button
                onClick={handleAddToCart}
                className="rounded-xl border border-blue-300/30 bg-blue-400/10 px-6 py-4 font-semibold text-blue-200 transition hover:bg-blue-400/20"
              >
                Add to Cart
              </button>

              <button
                onClick={() => {
                  setShowBooking(true);
                  loadDealerships();
                }}
                className="
                  rounded-xl
                  border border-white/10
                  bg-white/5
                  px-6 py-4
                  font-semibold
                  transition
                  hover:bg-white/10
                "
              >
                Book Test Drive
              </button>

            </div>
            {wishlistMessage && (
  <p className="mt-4 text-sm text-blue-300">
    {wishlistMessage}
  </p>
)}
            {cartMessage && <p className="mt-3 text-sm text-blue-300">{cartMessage}</p>}
          </div>

        </section>

        {showBooking && (
          <div className="mt-10 max-w-2xl rounded-2xl border border-blue-300/20 bg-blue-400/[0.05] p-6">
            <div className="flex items-start justify-between gap-4">
              <div><span className="text-xs font-bold tracking-[0.25em] text-blue-300">BOOK A DRIVE</span><h2 className="mt-2 text-2xl font-bold">Take {vehicle.model} for a spin</h2></div>
              <button type="button" onClick={() => setShowBooking(false)} className="text-2xl text-zinc-500">×</button>
            </div>
            <form onSubmit={handleBookingSubmit} className="mt-6 grid gap-4 sm:grid-cols-2">
              <select required value={booking.dealershipId} onChange={(event) => setBooking({ ...booking, dealershipId: event.target.value })} className="filter-control"><option value="">Choose dealership</option>{dealerships.map((dealership) => <option key={dealership.id} value={dealership.id}>{dealership.name} · {dealership.city}</option>)}</select>
              <input required type="date" min={new Date().toISOString().split("T")[0]} value={booking.date} onChange={(event) => setBooking({ ...booking, date: event.target.value })} className="filter-control" />
              <input required type="time" value={booking.time} onChange={(event) => setBooking({ ...booking, time: event.target.value })} className="filter-control" />
              <input value={booking.notes} onChange={(event) => setBooking({ ...booking, notes: event.target.value })} placeholder="Notes (optional)" className="filter-control" />
              <button disabled={bookingLoading} className="rounded-xl bg-white px-5 py-3 font-bold text-black disabled:opacity-50 sm:col-span-2">{bookingLoading ? "Booking..." : "Confirm Test Drive"}</button>
            </form>
          </div>
        )}

        <section className="mt-24">

          <div className="mb-10">
            <span className="text-xs font-bold tracking-[0.3em] text-blue-400">
              INTERACTIVE SHOWROOM
            </span>
            <h2 className="mt-3 text-4xl font-bold tracking-tight">
              See it from every angle
            </h2>
            <div className="mt-6 max-w-4xl">
              <ThreeShowroom vehicle={vehicle} />
            </div>
          </div>

          <div>
            <span className="text-xs font-bold tracking-[0.3em] text-blue-400">
              PERFORMANCE
            </span>

            <h2 className="mt-3 text-4xl font-bold tracking-tight">
              Specifications
            </h2>
          </div>

          <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-3 xl:grid-cols-5">

            {[
              ["Fuel", vehicle.fuelType],
              ["Transmission", vehicle.transmission],
              ["Engine", vehicle.engine],
              ["Horsepower", `${vehicle.horsepower} HP`],
              ["Torque", `${vehicle.torque} Nm`],
              ["Mileage", `${vehicle.mileage} km/l`],
              ["Body", vehicle.bodyType],
              ["Drive", vehicle.driveType],
              ["Seats", vehicle.seatingCapacity],
              ["Year", vehicle.year],
            ].map(([label, value]) => (
              <div
                key={label}
                className="
                  rounded-2xl
                  border border-white/10
                  bg-white/[0.03]
                  p-5
                "
              >
                <span className="text-xs text-zinc-600">
                  {label}
                </span>

                <strong className="mt-2 block">
                  {value ?? "—"}
                </strong>
              </div>
            ))}

          </div>

        </section>

        <section className="mt-24">

          <span className="text-xs font-bold tracking-[0.3em] text-blue-400">
            HIGHLIGHTS
          </span>

          <h2 className="mt-3 text-4xl font-bold tracking-tight">
            Features
          </h2>

          {features.length > 0 ? (
            <div className="mt-8 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {features.map((feature) => (
                <div
                  key={feature.id}
                  className="
                    rounded-2xl
                    border border-white/10
                    bg-white/[0.03]
                    p-5
                    text-zinc-300
                  "
                >
                  {feature.featureName}
                </div>
              ))}
            </div>
          ) : (
            <p className="mt-6 text-zinc-600">
              No features available for this vehicle.
            </p>
          )}

        </section>

        {images.length > 1 && (
          <section className="mt-24">

            <span className="text-xs font-bold tracking-[0.3em] text-blue-400">
              GALLERY
            </span>

            <h2 className="mt-3 text-4xl font-bold tracking-tight">
              Vehicle Gallery
            </h2>

            <div className="mt-8 grid grid-cols-1 gap-5 md:grid-cols-2">
              {images.map((image) => (
                <div
                  key={image.id}
                  className="
                    overflow-hidden
                    rounded-2xl
                    border border-white/10
                    bg-[#0b0e11]
                  "
                >
                  <img
                    src={image.imageUrl}
                    alt={`${vehicle.brand} ${vehicle.model}`}
                    className="h-72 w-full object-cover transition duration-500 hover:scale-105"
                  />
                </div>
              ))}
            </div>

          </section>
        )}

        <section className="mt-24 max-w-4xl">
          <span className="text-xs font-bold tracking-[0.3em] text-blue-400">OWNER NOTES</span>
          <h2 className="mt-3 text-4xl font-bold tracking-tight">Reviews</h2>
          <div className="mt-8 grid gap-4">
            {reviews.length ? reviews.map((review) => <article key={review.id} className="rounded-2xl border border-white/10 bg-white/[0.03] p-5">
              <div className="flex flex-wrap items-center justify-between gap-3"><strong>{review.userName || "PulseDrive driver"}</strong><span className="text-blue-300">{"★".repeat(review.rating)}{"☆".repeat(5 - review.rating)}</span></div>
              {review.comment && <p className="mt-3 text-zinc-400">{review.comment}</p>}
              {user?.id === review.userId && <div className="mt-4 flex gap-4 text-sm"><button type="button" className="text-blue-300" onClick={() => { setEditingReviewId(review.id); setReviewForm({ rating: review.rating, comment: review.comment || "" }); }}>Edit</button><button type="button" className="text-red-300" onClick={() => deleteReview(review.id)}>Delete</button></div>}
            </article>) : <p className="text-zinc-500">No reviews yet. Be the first to share your experience.</p>}
          </div>
          <form onSubmit={submitReview} className="mt-8 rounded-2xl border border-white/10 bg-white/[0.03] p-6">
            <h3 className="text-xl font-bold">{editingReviewId ? "Edit your review" : "Leave a review"}</h3>
            <div className="mt-5 grid gap-4 sm:grid-cols-[160px_1fr]"><select className="filter-control" value={reviewForm.rating} onChange={(event) => setReviewForm({ ...reviewForm, rating: event.target.value })}><option value="5">5 stars</option><option value="4">4 stars</option><option value="3">3 stars</option><option value="2">2 stars</option><option value="1">1 star</option></select><textarea className="filter-control p-3" rows="3" maxLength="2000" placeholder={isAuthenticated ? "Share your experience (optional)" : "Sign in to review this vehicle"} value={reviewForm.comment} onChange={(event) => setReviewForm({ ...reviewForm, comment: event.target.value })} /></div>
            {reviewMessage && <p className="mt-3 text-sm text-blue-300">{reviewMessage}</p>}
            <button className="mt-4 rounded-xl bg-white px-5 py-3 font-bold text-black">{isAuthenticated ? (editingReviewId ? "Update review" : "Post review") : "Sign in to review"}</button>
          </form>
        </section>

      </main>
    </>
  );
}

export default VehicleDetails;