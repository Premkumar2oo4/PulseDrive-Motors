import { useEffect, useState } from "react";

import Navbar from "../components/Navbar";
import VehicleCard from "../components/VehicleCard";
import ThreeShowroom from "../components/ThreeShowroom";
import Footer from "../components/Footer";

import api from "../api/axios";

function Home() {
  const [vehicles, setVehicles] = useState([]);
  const [filters, setFilters] = useState({
    brand: "",
    bodyType: "",
    fuelType: "",
    sortBy: "price",
    sortDirection: "asc",
  });
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadVehicles = async () => {
      try {
        setLoading(true);

        const params = {
          ...Object.fromEntries(
            Object.entries(filters).filter(([, value]) => value)
          ),
          page,
          size: 9,
        };
        const response = await api.get("/vehicles/search/page", { params });

        setVehicles(response.data.vehicles || []);
        setPageInfo(response.data);
      } catch (error) {
        console.error(error);

        setError(
          "Unable to load vehicles from the PulseDrive backend."
        );
      } finally {
        setLoading(false);
      }
    };

    loadVehicles();
  }, [filters, page]);

  const brands = [...new Set(vehicles.map((vehicle) => vehicle.brand).filter(Boolean))];
  const bodyTypes = [...new Set(vehicles.map((vehicle) => vehicle.bodyType).filter(Boolean))];
  const fuelTypes = [...new Set(vehicles.map((vehicle) => vehicle.fuelType).filter(Boolean))];

  const updateFilter = (event) => {
    setPage(0);
    setFilters((current) => ({ ...current, [event.target.name]: event.target.value }));
  };

  return (
    <>
      <Navbar />

      <main className="min-h-screen bg-[#050607] text-white">
        <section
          className="
            showroom-hero relative overflow-hidden
            px-[7%] pb-16 pt-32 lg:pb-24 lg:pt-40
          "
        >
          <div className="showroom-grid" />
          <div className="relative z-10 grid items-center gap-6 lg:grid-cols-[0.82fr_1.18fr] lg:gap-0">
            <div className="max-w-xl pb-8 lg:pb-0">
            <span
              className="
                mb-5 block
                text-xs font-bold
                tracking-[0.3em]
                text-blue-400
              "
            >
              THE PULSEDRIVE COLLECTION
            </span>

            <h1
              className="
                showroom-title text-5xl font-black
                leading-[0.88] sm:text-7xl lg:text-[86px]
              "
            >
              The road is

              <span className="showroom-accent block">
                yours.
              </span>
            </h1>

            <p
              className="
                mt-7 max-w-xl
                text-base leading-8
                text-zinc-400
              "
            >
              Explore premium performance and luxury vehicles
              available through PulseDrive Motors. Step into the
              collection and meet your next machine in 3D.
            </p>
            <div className="mt-8 flex flex-wrap items-center gap-3 text-xs uppercase tracking-[0.2em] text-zinc-500">
              <span className="status-dot" /> Live collection
              <span className="mx-1 h-1 w-1 rounded-full bg-zinc-600" />
              {pageInfo.totalElements || "--"} models indexed
            </div>
          </div>
          <div className="showroom-stage relative">
            <ThreeShowroom vehicle={vehicles[0]} />
            <div className="showroom-spec">
              <span>Selected for you</span>
              <strong>{vehicles[0] ? `${vehicles[0].brand} ${vehicles[0].model}` : "PulseDrive Signature"}</strong>
              <small>{vehicles[0]?.bodyType || "Performance collection"}</small>
            </div>
          </div>
          </div>
        </section>

        <section id="vehicles" className="px-[7%] pb-28">
          <div
            className="
              mb-10 flex
              items-end justify-between
              gap-5
            "
          >
            <div>
              <span
                className="
                  text-xs font-bold
                  tracking-[0.3em]
                  text-blue-400
                "
              >
                DISCOVER
              </span>

              <h2
                className="
                  mt-3 text-4xl
                  font-bold tracking-tight
                  sm:text-5xl
                "
              >
                Featured Vehicles
              </h2>
            </div>

            <span className="text-sm text-zinc-600">{pageInfo.totalElements} vehicles</span>
          </div>

          <div className="mb-10 grid gap-3 rounded-2xl border border-white/10 bg-white/[0.03] p-4 sm:grid-cols-2 lg:grid-cols-5">
            <select name="brand" value={filters.brand} onChange={updateFilter} className="filter-control">
              <option value="">All brands</option>
              {brands.map((brand) => <option key={brand} value={brand}>{brand}</option>)}
            </select>
            <select name="bodyType" value={filters.bodyType} onChange={updateFilter} className="filter-control">
              <option value="">All body styles</option>
              {bodyTypes.map((bodyType) => <option key={bodyType} value={bodyType}>{bodyType}</option>)}
            </select>
            <select name="fuelType" value={filters.fuelType} onChange={updateFilter} className="filter-control">
              <option value="">All fuel types</option>
              {fuelTypes.map((fuelType) => <option key={fuelType} value={fuelType}>{fuelType}</option>)}
            </select>
            <select name="sortBy" value={filters.sortBy} onChange={updateFilter} className="filter-control">
              <option value="price">Sort by price</option>
              <option value="year">Sort by year</option>
              <option value="brand">Sort by brand</option>
            </select>
            <select name="sortDirection" value={filters.sortDirection} onChange={updateFilter} className="filter-control">
              <option value="asc">Ascending</option>
              <option value="desc">Descending</option>
            </select>
          </div>

          {loading && (
            <div
              className="
                flex min-h-56
                items-center justify-center
                rounded-2xl
                border border-white/10
                bg-white/[0.02]
                text-zinc-500
              "
            >
              Loading vehicles...
            </div>
          )}

          {error && (
            <div
              className="
                flex min-h-56
                items-center justify-center
                rounded-2xl
                border border-red-400/20
                bg-red-500/5
                text-red-300
              "
            >
              {error}
            </div>
          )}

          {!loading &&
            !error &&
            vehicles.length === 0 && (
              <div
                className="
                  flex min-h-56
                  items-center justify-center
                  rounded-2xl
                  border border-white/10
                  bg-white/[0.02]
                  text-zinc-500
                "
              >
                No vehicles available.
              </div>
            )}

          {!loading &&
            !error &&
            vehicles.length > 0 && (
              <div
                className="
                  grid grid-cols-1
                  gap-6
                  md:grid-cols-2
                  xl:grid-cols-3
                "
              >
                {vehicles.map((vehicle) => (
                  <VehicleCard
                    key={vehicle.id}
                    vehicle={vehicle}
                  />
                ))}
              </div>
            )}

          {!loading && !error && pageInfo.totalPages > 1 && (
            <div className="mt-10 flex items-center justify-between border-t border-white/10 pt-5">
              <button
                type="button"
                disabled={pageInfo.first}
                onClick={() => setPage((current) => current - 1)}
                className="page-button"
              >
                ← Previous
              </button>
              <span className="text-sm text-zinc-500">
                Page {pageInfo.currentPage + 1} of {pageInfo.totalPages}
              </span>
              <button
                type="button"
                disabled={pageInfo.last}
                onClick={() => setPage((current) => current + 1)}
                className="page-button"
              >
                Next →
              </button>
            </div>
          )}
        </section>

        <section id="categories" className="home-info-section">
          <div>
            <span className="section-kicker">FIND YOUR FIT</span>
            <h2>Built around how you drive.</h2>
          </div>
          <div className="home-category-grid">
            {["Performance", "Luxury", "Electric", "Family"].map((category) => (
              <div className="home-category" key={category}>
                <span>0{["Performance", "Luxury", "Electric", "Family"].indexOf(category) + 1}</span>
                <strong>{category}</strong>
                <p>Curated vehicles for your next chapter.</p>
              </div>
            ))}
          </div>
        </section>

        <section id="test-drive" className="home-cta-section">
          <div>
            <span className="section-kicker">THE EXPERIENCE</span>
            <h2>Meet the car before you commit.</h2>
            <p>Explore the collection, choose a dealership, and book a drive from any vehicle detail page.</p>
          </div>
          <span className="home-cta-mark">DRIVE<br />FIRST</span>
        </section>

        <section id="about" className="home-about-section">
          <span className="section-kicker">ABOUT PULSEDRIVE</span>
          <h2>A more considered way to buy your next car.</h2>
          <p>PulseDrive brings verified vehicles, transparent details, and a more personal showroom experience together in one place.</p>
        </section>
      </main>
      <Footer />
    </>
  );
}

export default Home;