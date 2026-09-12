import { Link } from "react-router-dom";

function VehicleCard({ vehicle }) {
  return (
    <article
      className="
        overflow-hidden
        rounded-2xl
        border border-white/10
        bg-white/[0.03]
        transition duration-300
        hover:-translate-y-2
        hover:border-blue-400/30
        hover:shadow-2xl
        hover:shadow-black/30
      "
    >
      <div
        className="
          relative flex h-56
          items-center justify-center
          bg-[#0b0e11]
          p-5
        "
      >
        <span
          className="
            absolute left-4 top-4
            rounded-full
            border border-white/10
            bg-black/50
            px-3 py-1.5
            text-[11px]
            text-zinc-300
            backdrop-blur-xl
          "
        >
          {vehicle.available ? "Available" : "Unavailable"}
        </span>

        <div
          className="
            text-4xl font-black
            tracking-tight
            text-white/10
          "
        >
          {vehicle.brand}
        </div>
      </div>

      <div className="p-5">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p
              className="
                text-[11px]
                font-bold
                uppercase
                tracking-[0.25em]
                text-blue-400
              "
            >
              {vehicle.brand}
            </p>

            <h3
              className="
                mt-1 text-xl
                font-bold
                tracking-tight
              "
            >
              {vehicle.model}
              {vehicle.variant ? ` ${vehicle.variant}` : ""}
            </h3>
          </div>

          <span
            className="
              rounded-md
              bg-white/5
              px-2 py-1
              text-xs
              text-zinc-500
            "
          >
            {vehicle.year}
          </span>
        </div>

        <div className="mt-5 flex flex-wrap gap-2">
          <span className="rounded-md bg-white/5 px-3 py-1.5 text-xs text-zinc-400">
            {vehicle.fuelType}
          </span>

          <span className="rounded-md bg-white/5 px-3 py-1.5 text-xs text-zinc-400">
            {vehicle.transmission}
          </span>

          <span className="rounded-md bg-white/5 px-3 py-1.5 text-xs text-zinc-400">
            {vehicle.bodyType}
          </span>
        </div>

        <div
          className="
            mt-6 flex
            items-end justify-between
            gap-4
            border-t border-white/10
            pt-5
          "
        >
          <div>
            <p className="text-[10px] text-zinc-600">
              Starting from
            </p>

            <strong className="text-lg">
              ₹{Number(vehicle.price).toLocaleString("en-IN")}
            </strong>
          </div>

          <Link
            to={`/vehicles/${vehicle.id}`}
            className="
              rounded-lg
              border border-white/10
              bg-white/5
              px-4 py-2
              text-xs
              transition
              hover:bg-white
              hover:text-black
            "
          >
            View Details →
          </Link>
        </div>
      </div>
    </article>
  );
}

export default VehicleCard;