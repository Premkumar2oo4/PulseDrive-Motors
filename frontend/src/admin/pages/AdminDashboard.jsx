import {
  useEffect,
  useState,
} from "react";

import api from "../../api/axios";

function AdminDashboard() {
  const [dashboard, setDashboard] =
    useState(null);

  const [inventory, setInventory] =
    useState(null);

  const [lowStockVehicles, setLowStockVehicles] =
    useState([]);

  const [unavailableVehicles, setUnavailableVehicles] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        const [
          dashboardResponse,
          inventoryResponse,
          lowStockResponse,
          unavailableResponse,
        ] = await Promise.all([
          api.get("/admin/dashboard"),
          api.get(
            "/admin/inventory/summary"
          ),
          api.get("/admin/inventory/low-stock", {
            params: { threshold: 2 },
          }),
          api.get("/admin/inventory/unavailable"),
        ]);

        setDashboard(
          dashboardResponse.data
        );

        setInventory(
          inventoryResponse.data
        );
        setLowStockVehicles(lowStockResponse.data || []);
        setUnavailableVehicles(unavailableResponse.data || []);
      } catch (error) {
        console.error(error);

        setError(
          error.response?.data?.message ||
            "Unable to load admin dashboard."
        );
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  if (loading) {
    return (
      <div className="text-zinc-500">
        Loading admin dashboard...
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
          p-5 text-red-300
        "
      >
        {error}
      </div>
    );
  }

  const cards = [
    [
      "Total Vehicles",
      inventory?.totalVehicles ?? 0,
    ],
    [
      "Available",
      inventory?.availableVehicles ?? 0,
    ],
    [
      "Customers",
      dashboard?.totalCustomers ?? 0,
    ],
    [
      "Orders",
      dashboard?.totalOrders ?? 0,
    ],
  ];

  const availabilityRate = inventory?.totalVehicles
    ? Math.round((inventory.availableVehicles / inventory.totalVehicles) * 100)
    : 0;
  const deliveryRate = dashboard?.totalOrders
    ? Math.round((dashboard.deliveredOrders / dashboard.totalOrders) * 100)
    : 0;
  const cancellationRate = dashboard?.totalOrders
    ? Math.round((dashboard.cancelledOrders / dashboard.totalOrders) * 100)
    : 0;
  const testDriveCompletionRate = dashboard?.totalTestDrives
    ? Math.round(((dashboard.totalTestDrives - dashboard.pendingTestDrives) / dashboard.totalTestDrives) * 100)
    : 0;

  const healthMetrics = [
    { label: "Availability", shortLabel: "Inventory", value: availabilityRate, tone: "blue" },
    { label: "Delivery rate", shortLabel: "Delivery", value: deliveryRate, tone: "green" },
    { label: "Test-drive completion", shortLabel: "Test drives", value: testDriveCompletionRate, tone: "amber" },
    { label: "Cancellation rate", shortLabel: "Cancellations", value: cancellationRate, tone: "red", inverse: true },
  ];
  const totalRevenue = Number(dashboard?.totalRevenue ?? 0);
  const averageOrderValue = dashboard?.totalOrders ? totalRevenue / dashboard.totalOrders : 0;
  const salesMetrics = [
    { label: "Total sales", shortLabel: "Total sales", value: totalRevenue, tone: "blue" },
    { label: "Delivered sales (est.)", shortLabel: "Delivered est.", value: dashboard?.totalOrders ? totalRevenue * (dashboard.deliveredOrders / dashboard.totalOrders) : 0, tone: "green" },
    { label: "Average order", shortLabel: "Avg. order", value: averageOrderValue, tone: "amber" },
    { label: "Orders", shortLabel: "Orders", value: dashboard?.totalOrders ?? 0, tone: "red", format: "number" },
  ];

  return (
    <div>
      <div>
        <p
          className="
            text-xs font-bold
            tracking-[0.3em]
            text-blue-400
          "
        >
          OVERVIEW
        </p>

        <h1
          className="
            mt-3 text-4xl
            font-black
            tracking-tight
          "
        >
          Admin Dashboard
        </h1>

        <p className="mt-2 text-zinc-500">
          Monitor your PulseDrive platform.
        </p>
      </div>

      <div
        className="
          mt-10 grid
          grid-cols-1 gap-5
          sm:grid-cols-2
          xl:grid-cols-4
        "
      >
        {cards.map(
          ([label, value]) => (
            <div
              key={label}
              className="
                rounded-2xl
                border border-white/10
                bg-white/[0.03]
                p-6
              "
            >
              <p className="text-sm text-zinc-500">
                {label}
              </p>

              <h2 className="mt-3 text-4xl font-black">
                {value}
              </h2>
            </div>
          )
        )}
      </div>

      <HealthAnalysis metrics={healthMetrics} salesMetrics={salesMetrics} monthlySales={dashboard?.monthlySales} yearlySales={dashboard?.yearlySales} />

      <section className="analysis-columns">
        <div className="analysis-panel-large">
          <p className="text-xs font-bold tracking-[0.25em] text-blue-400">SALES FUNNEL</p>
          <h2 className="mt-2 text-2xl font-bold">Order analysis</h2>
          <div className="funnel-list">
            <FunnelRow label="All orders" value={dashboard?.totalOrders} max={dashboard?.totalOrders} tone="blue" />
            <FunnelRow label="Pending / placed" value={dashboard?.pendingOrders} max={dashboard?.totalOrders} tone="amber" />
            <FunnelRow label="Delivered" value={dashboard?.deliveredOrders} max={dashboard?.totalOrders} tone="green" />
            <FunnelRow label="Cancelled" value={dashboard?.cancelledOrders} max={dashboard?.totalOrders} tone="red" />
          </div>
          <div className="analysis-total"><span>Revenue generated</span><strong>₹{Number(dashboard?.totalRevenue ?? 0).toLocaleString("en-IN")}</strong></div>
        </div>

        <div className="analysis-panel-large">
          <p className="text-xs font-bold tracking-[0.25em] text-blue-400">INVENTORY RISK</p>
          <h2 className="mt-2 text-2xl font-bold">Stock attention needed</h2>
          <div className="risk-summary"><div><strong>{lowStockVehicles.length}</strong><span>Low stock</span></div><div><strong>{unavailableVehicles.length}</strong><span>Unavailable</span></div></div>
          <div className="risk-list">{lowStockVehicles.slice(0, 4).map((vehicle) => <div className="risk-row" key={vehicle.id}><span>{vehicle.brand} {vehicle.model}</span><b>{vehicle.stock ?? 0} left</b></div>)}{lowStockVehicles.length === 0 && <p className="text-sm text-zinc-500">No vehicles below the stock threshold.</p>}</div>
        </div>
      </section>

      <div
        className="
          mt-8 grid
          grid-cols-1 gap-5
          xl:grid-cols-2
        "
      >
        <div
          className="
            rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-6
          "
        >
          <p className="text-sm text-zinc-500">
            Revenue
          </p>

          <h2 className="mt-3 text-3xl font-black">
            ₹
            {Number(
              dashboard?.totalRevenue ?? 0
            ).toLocaleString("en-IN")}
          </h2>
        </div>

        <div
          className="
            rounded-2xl
            border border-white/10
            bg-white/[0.03]
            p-6
          "
        >
          <p className="text-sm text-zinc-500">
            Low Stock
          </p>

          <h2 className="mt-3 text-3xl font-black">
            {inventory?.lowStockCount ??
              0}
          </h2>
        </div>
      </div>
    </div>
  );
}

function HealthAnalysis({ metrics, salesMetrics, monthlySales, yearlySales }) {
  const [view, setView] = useState("bar");
  const [salesPeriod, setSalesPeriod] = useState("month");

  return (
    <section className="analysis-section">
      <div className="analysis-heading">
        <div>
          <p className="text-xs font-bold tracking-[0.3em] text-blue-400">ANALYSIS</p>
          <h2 className="mt-2 text-2xl font-bold">Platform health</h2>
        </div>
        <div className="analysis-controls" role="group" aria-label="Analysis view">
          {[['bar', 'Bar graph'], ['line', 'Line graph'], ['sales', 'Sales'], ['suggestions', 'Suggestions']].map(([mode, label]) => (
            <button
              key={mode}
              type="button"
              className={`analysis-control ${view === mode ? "active" : ""}`}
              aria-pressed={view === mode}
              onClick={() => setView(mode)}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      <div className="analysis-chart-panel">
        {view === "suggestions" ? <Suggestions metrics={metrics} /> : view === "sales" ? <SalesTrendChart data={salesPeriod === "month" ? monthlySales : yearlySales} period={salesPeriod} setPeriod={setSalesPeriod} /> : <HealthChart metrics={view === "sales" ? salesMetrics : metrics} type={view} financial={view === "sales"} />}
      </div>
    </section>
  );
}

function SalesTrendChart({ data = [], period, setPeriod }) {
  const [hoveredIndex, setHoveredIndex] = useState(null);
  const chartWidth = 760;
  const chartBottom = 218;
  const chartTop = 30;
  const chartHeight = chartBottom - chartTop;
  const visibleData = data.slice(-12);
  const maxRevenue = Math.max(...visibleData.map((item) => Number(item.revenue || 0)), 1);
  const points = visibleData.map((item, index) => ({
    ...item,
    x: visibleData.length === 1 ? chartWidth / 2 : 58 + index * (674 / (visibleData.length - 1)),
    y: chartBottom - (Number(item.revenue || 0) / maxRevenue) * chartHeight,
  }));
  const linePoints = points.map(({ x, y }) => `${x},${y}`).join(" ");
  const total = visibleData.reduce((sum, item) => sum + Number(item.revenue || 0), 0);

  return (
    <div className="trend-chart-wrap">
      <div className="trend-header">
        <div><span className="trend-label">PAID SALES TREND</span><h3>Revenue performance</h3><p>{period === "month" ? "Monthly view" : "Yearly view"} · Last {visibleData.length || 0} periods</p></div>
        <div className="trend-total"><span>Total sales</span><strong>{formatMoney(total)}</strong></div>
      </div>
      <div className="trend-periods" role="group" aria-label="Sales period"><button type="button" className={period === "month" ? "active" : ""} onClick={() => { setPeriod("month"); setHoveredIndex(null); }}>Monthly</button><button type="button" className={period === "year" ? "active" : ""} onClick={() => { setPeriod("year"); setHoveredIndex(null); }}>Yearly</button></div>
      {visibleData.length ? <div className="trend-plot">
        <svg className="health-chart" viewBox={`0 0 ${chartWidth} 260`} role="img" aria-label={`${period} paid sales chart`}>
          {[0, 25, 50, 75, 100].map((tick) => { const y = chartBottom - (tick / 100) * chartHeight; return <g key={tick}><line className="chart-gridline" x1="52" x2="740" y1={y} y2={y} /><text className="chart-axis-label" x="4" y={y + 4}>{formatCompactMoney((maxRevenue * tick) / 100)}</text></g>; })}
          <polyline className="chart-line trend-line" points={linePoints} />
          {points.map((point, index) => <g key={point.period} onMouseEnter={() => setHoveredIndex(index)} onMouseLeave={() => setHoveredIndex(null)}><circle className="trend-hit-area" cx={point.x} cy={point.y} r="16" /><circle className={`chart-point ${hoveredIndex === index ? "active" : ""}`} cx={point.x} cy={point.y} r="5" /><text className="chart-x-label" x={point.x} y="246">{period === "month" ? point.period.slice(5) : point.period}</text></g>)}
        </svg>
        {hoveredIndex !== null && <div className="chart-tooltip" style={{ left: `${(points[hoveredIndex].x / chartWidth) * 100}%` }}><strong>{points[hoveredIndex].period}</strong><span>{formatMoney(points[hoveredIndex].revenue)} sales</span><span>{points[hoveredIndex].orders} orders</span></div>}
      </div> : <div className="trend-empty"><strong>No paid sales history yet</strong><span>Sales trends will appear here after completed payments.</span></div>}
      <p className="trend-note">Paid sales are shown. Profit requires vehicle cost data.</p>
    </div>
  );
}

function HealthChart({ metrics, type, financial = false }) {
  const [hoveredIndex, setHoveredIndex] = useState(null);
  const chartWidth = 760;
  const chartHeight = 260;
  const chartBottom = 218;
  const scaleMax = financial ? Math.max(...metrics.map((metric) => metric.value), 1) : 100;
  const points = metrics.map((metric, index) => ({
    ...metric,
    x: 82 + index * 198,
    y: chartBottom - (metric.value / scaleMax) * 170,
  }));
  const linePoints = points.map(({ x, y }) => `${x},${y}`).join(" ");

  return (
    <div className="metric-chart-wrap">
      <div className="chart-summary"><span>{financial ? "Sales performance" : "Current operating score"}</span><strong>{financial ? formatMoney(metrics[0].value) : `${Math.round(metrics.reduce((sum, metric) => sum + (metric.inverse ? 100 - metric.value : metric.value), 0) / metrics.length)}%`}</strong></div>
      <div className="metric-chart-plot">
      <svg className="health-chart" viewBox={`0 0 ${chartWidth} ${chartHeight}`} role="img" aria-label={`${type} chart of platform health metrics`}>
        <defs>
          <linearGradient id="bar-blue" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#a5e6ff" /><stop offset="100%" stopColor="#3186b7" /></linearGradient>
          <linearGradient id="bar-green" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#a4edc1" /><stop offset="100%" stopColor="#3b9968" /></linearGradient>
          <linearGradient id="bar-amber" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#ffe0a0" /><stop offset="100%" stopColor="#b97724" /></linearGradient>
          <linearGradient id="bar-red" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#ffc1b9" /><stop offset="100%" stopColor="#ae4f4b" /></linearGradient>
        </defs>
        {[0, 25, 50, 75, 100].map((tick) => {
          const y = chartBottom - (tick / 100) * 170;
          return <g key={tick}><line className="chart-gridline" x1="52" x2="740" y1={y} y2={y} /><text className="chart-axis-label" x="20" y={y + 4}>{financial ? formatCompactMoney((scaleMax * tick) / 100) : tick}</text></g>;
        })}
        {type === "line" ? <><polyline className="chart-line" points={linePoints} />{points.map((point, index) => <g key={point.label} onMouseEnter={() => setHoveredIndex(index)} onMouseLeave={() => setHoveredIndex(null)}><circle className="trend-hit-area" cx={point.x} cy={point.y} r="18" /><circle className={`chart-point ${hoveredIndex === index ? "active" : ""}`} cx={point.x} cy={point.y} r="6" /></g>)}</> : points.map((point, index) => <g key={point.label} onMouseEnter={() => setHoveredIndex(index)} onMouseLeave={() => setHoveredIndex(null)}><rect className={`chart-bar ${point.tone} ${hoveredIndex === index ? "active" : ""}`} x={point.x - 34} y={point.y} width="68" height={chartBottom - point.y} rx="8" /><text className="chart-value-label" x={point.x} y={Math.max(point.y - 10, 18)}>{point.value}{financial ? "" : "%"}</text></g>)}
        {points.map((point) => <text className="chart-x-label" key={`${point.label}-label`} x={point.x} y="246">{point.shortLabel}</text>)}
      </svg>
      {hoveredIndex !== null && <div className="metric-tooltip" style={{ left: `${(points[hoveredIndex].x / chartWidth) * 100}%` }}><strong>{points[hoveredIndex].label}</strong><span>{financial ? formatMoney(points[hoveredIndex].value) : `${points[hoveredIndex].value}%`}</span><small>{points[hoveredIndex].inverse ? "Lower is better" : "Higher is better"}</small></div>}
      </div>
      <div className="chart-legend">{metrics.map((metric) => <span key={metric.label}><i className={`legend-dot ${metric.tone}`} />{metric.label}: <b>{metric.format === "number" ? metric.value : financial ? formatMoney(metric.value) : `${metric.value}%`}</b></span>)}</div>
    </div>
  );
}

function Suggestions({ metrics }) {
  const suggestions = metrics.flatMap((metric) => {
    const score = metric.inverse ? 100 - metric.value : metric.value;
    if (score >= 80) return [];
    const action = metric.inverse ? "Review cancellation reasons and follow up with affected customers." : `Improve ${metric.label.toLowerCase()} to keep operations above 80%.`;
    return [{ ...metric, action }];
  });

  return <div className="suggestion-list"><div className="chart-summary"><span>Suggested next actions</span><strong>{suggestions.length || "On track"}</strong></div>{suggestions.length ? suggestions.map((suggestion) => <div className="suggestion-row" key={suggestion.label}><i className={`legend-dot ${suggestion.tone}`} /><div><strong>{suggestion.label} is below target</strong><p>{suggestion.action}</p></div><b>{suggestion.value}%</b></div>) : <div className="suggestion-success"><strong>All key metrics are on track.</strong><p>Keep monitoring daily operations for changes.</p></div>}</div>;
}

function formatMoney(value) {
  return `₹${Number(value).toLocaleString("en-IN", { maximumFractionDigits: 0 })}`;
}

function formatCompactMoney(value) {
  if (value >= 10000000) return `₹${(value / 10000000).toFixed(1)}Cr`;
  if (value >= 100000) return `₹${(value / 100000).toFixed(1)}L`;
  if (value >= 1000) return `₹${(value / 1000).toFixed(0)}k`;
  return `₹${Math.round(value)}`;
}

function FunnelRow({ label, value = 0, max = 0, tone }) {
  const percent = max ? Math.round((value / max) * 100) : 0;
  return <div className="funnel-row"><div className="flex justify-between gap-4 text-sm"><span className="text-zinc-400">{label}</span><strong>{value ?? 0}</strong></div><div className="analysis-meter"><span className={tone} style={{ width: `${percent}%` }} /></div></div>;
}

export default AdminDashboard;