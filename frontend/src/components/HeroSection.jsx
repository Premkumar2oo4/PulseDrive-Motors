import { Link } from "react-router-dom";
import heroCar from "../assets/hero.png";

function HeroSection() {
  return (
    <section
      className="
        relative flex min-h-screen
        items-center overflow-hidden
        bg-[#050607]
        px-[6%] pb-14 pt-32
      "
    >
      <div
        className="
          absolute -right-32 top-20
          h-[500px] w-[500px]
          rounded-full
          bg-blue-500/20
          blur-[120px]
        "
      />

      <div
        className="
          absolute -bottom-40 -left-64
          h-[420px] w-[420px]
          rounded-full
          bg-indigo-500/10
          blur-[120px]
        "
      />

      <div
        className="
          relative z-10
          grid w-full
          grid-cols-1
          items-center gap-12
          lg:grid-cols-[0.9fr_1.1fr]
        "
      >
        <div className="text-center lg:text-left">
          <p
            className="
              mb-5 text-xs font-bold
              tracking-[0.3em]
              text-blue-400
            "
          >
            PREMIUM AUTOMOTIVE EXPERIENCE
          </p>

          <h1
            className="
              text-[54px]
              font-black
              leading-[0.93]
              tracking-[-4px]
              sm:text-7xl
              lg:text-[96px]
            "
          >
            Drive Beyond

            <span
              className="
                block
                bg-gradient-to-r
                from-white to-blue-400
                bg-clip-text
                text-transparent
              "
            >
              Limits.
            </span>
          </h1>

          <p
            className="
              mx-auto mt-7 max-w-xl
              text-base leading-8
              text-zinc-400
              lg:mx-0
            "
          >
            Discover premium vehicles engineered for
            performance, comfort and unforgettable driving
            experiences.
          </p>

          <div
            className="
              mt-8 flex flex-col
              justify-center gap-3
              sm:flex-row
              lg:justify-start
            "
          >
            <Link
              to="/home"
              className="
                inline-flex items-center
                justify-center gap-3
                rounded-lg
                bg-white
                px-6 py-4
                font-bold text-black
                transition
                hover:-translate-y-1
                hover:shadow-2xl
              "
            >
              Explore Vehicles
              <span className="text-xl">→</span>
            </Link>

            <button
              className="
                rounded-lg
                border border-white/15
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

          <div
            className="
              mt-12 flex
              justify-center gap-8
              lg:justify-start
            "
          >
            <div>
              <h3 className="text-2xl font-bold">50+</h3>
              <p className="mt-1 text-xs text-zinc-500">
                Premium Cars
              </p>
            </div>

            <div>
              <h3 className="text-2xl font-bold">15+</h3>
              <p className="mt-1 text-xs text-zinc-500">
                Luxury Brands
              </p>
            </div>

            <div>
              <h3 className="text-2xl font-bold">100%</h3>
              <p className="mt-1 text-xs text-zinc-500">
                Verified Vehicles
              </p>
            </div>
          </div>
        </div>

        <div
          className="
            relative flex
            min-h-[320px]
            items-center justify-center
            lg:min-h-[560px]
          "
        >
          <div
            className="
              absolute bottom-24
              h-24 w-[65%]
              bg-blue-500/40
              blur-[80px]
            "
          />

          <img
            src={heroCar}
            alt="PulseDrive premium vehicle"
            className="
              relative z-10
              w-full max-w-[950px]
              drop-shadow-2xl
              transition duration-500
              hover:scale-[1.02]
            "
          />

          <div
            className="
              absolute right-4 top-24
              hidden rounded-xl
              border border-white/10
              bg-black/50
              px-4 py-3
              backdrop-blur-xl
              lg:block
            "
          >
            <span className="block text-xs text-zinc-500">
              Performance
            </span>

            <strong className="text-lg">
              503 HP
            </strong>
          </div>

          <div
            className="
              absolute bottom-28 left-3
              hidden rounded-xl
              border border-white/10
              bg-black/50
              px-4 py-3
              backdrop-blur-xl
              lg:block
            "
          >
            <span className="block text-xs text-zinc-500">
              0 - 100 km/h
            </span>

            <strong className="text-lg">
              3.8 sec
            </strong>
          </div>
        </div>
      </div>
    </section>
  );
}

export default HeroSection;