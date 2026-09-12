import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../components/Navbar";
import api from "../api/axios";

const money = (value) => `₹${Number(value || 0).toLocaleString("en-IN")}`;

function Account() {
  const [activeTab, setActiveTab] = useState("overview");
  const [data, setData] = useState({ profile: null, wishlist: [], cart: null, orders: [], testDrives: [], notifications: [], unreadCount: 0 });
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [checkout, setCheckout] = useState({ shippingAddress: "", couponCode: "" });
  const [checkingOut, setCheckingOut] = useState(false);
  const [profileForm, setProfileForm] = useState({ firstName: "", lastName: "", phone: "", address: "" });
  const [passwordForm, setPasswordForm] = useState({ currentPassword: "", newPassword: "" });
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [payment, setPayment] = useState(null);

  const loadAccount = async () => {
    const [profile, wishlist, cart, orders, testDrives, notifications, unread] = await Promise.all([
      api.get("/users/me"),
      api.get("/wishlist"),
      api.get("/cart"),
      api.get("/orders"),
      api.get("/test-drives"),
      api.get("/notifications"),
      api.get("/notifications/unread-count"),
    ]);
    setData({
      profile: profile.data,
      wishlist: wishlist.data || [],
      cart: cart.data || { items: [], totalItems: 0, totalAmount: 0 },
      orders: orders.data || [],
      testDrives: testDrives.data || [],
      notifications: notifications.data || [],
      unreadCount: unread.data?.unreadCount || 0,
    });
    setProfileForm({ firstName: profile.data?.firstName || "", lastName: profile.data?.lastName || "", phone: profile.data?.phone || "", address: profile.data?.address || "" });
  };

  useEffect(() => {
    loadAccount()
      .catch((error) => setMessage(error.response?.data?.message || "Unable to load your account right now."))
      .finally(() => setLoading(false));
  }, []);

  const removeWishlist = async (vehicleId) => {
    try {
      await api.delete(`/wishlist/${vehicleId}`);
      setData((current) => ({ ...current, wishlist: current.wishlist.filter((item) => item.vehicleId !== vehicleId) }));
    } catch {
      setMessage("Unable to remove that vehicle.");
    }
  };

  const cancelTestDrive = async (id) => {
    try {
      await api.delete(`/test-drives/${id}`);
      setData((current) => ({ ...current, testDrives: current.testDrives.filter((item) => item.id !== id) }));
    } catch {
      setMessage("Unable to cancel this test drive.");
    }
  };

  const updateCart = async (vehicleId, quantity) => {
    try {
      const response = quantity > 0 ? await api.put(`/cart/${vehicleId}`, null, { params: { quantity } }) : await api.delete(`/cart/${vehicleId}`);
      if (quantity > 0) setData((current) => ({ ...current, cart: response.data }));
      else setData((current) => ({ ...current, cart: { ...current.cart, items: current.cart.items.filter((item) => item.vehicleId !== vehicleId) } }));
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update your cart.");
    }
  };

  const cancelOrder = async (orderId) => {
    try {
      const response = await api.put(`/orders/${orderId}/cancel`);
      setData((current) => ({ ...current, orders: current.orders.map((order) => order.orderId === orderId ? response.data : order) }));
      setSelectedOrder(response.data);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to cancel this order.");
    }
  };

  const showOrder = async (orderId) => {
    try {
      const [orderResponse, paymentResponse] = await Promise.allSettled([api.get(`/orders/${orderId}`), api.get(`/payments/order/${orderId}`)]);
      if (orderResponse.status === "fulfilled") setSelectedOrder(orderResponse.value.data);
      setPayment(paymentResponse.status === "fulfilled" ? paymentResponse.value.data : null);
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to load order details.");
    }
  };

  const saveProfile = async (event) => {
    event.preventDefault();
    try {
      const response = await api.put("/users/me", profileForm);
      setData((current) => ({ ...current, profile: response.data }));
      setMessage("Profile updated.");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update your profile.");
    }
  };

  const changePassword = async (event) => {
    event.preventDefault();
    try {
      await api.put("/users/me/password", passwordForm);
      setPasswordForm({ currentPassword: "", newPassword: "" });
      setMessage("Password changed successfully.");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to change your password.");
    }
  };

  const markNotification = async (notificationId) => {
    try {
      const response = await api.put(`/notifications/${notificationId}/read`);
      setData((current) => ({ ...current, notifications: current.notifications.map((item) => item.id === notificationId ? response.data : item), unreadCount: Math.max(0, current.unreadCount - 1) }));
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update notification.");
    }
  };

  const markAllNotifications = async () => {
    try {
      await api.put("/notifications/read-all");
      setData((current) => ({ ...current, notifications: current.notifications.map((item) => ({ ...item, read: true })), unreadCount: 0 }));
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to update notifications.");
    }
  };

  const handleCheckout = async (event) => {
    event.preventDefault();
    try {
      setCheckingOut(true);
      const response = await api.post("/orders/checkout", checkout);
      setCheckout({ shippingAddress: "", couponCode: "" });
      const razorpayOrder = await api.post(`/payments/razorpay/order/${response.data.orderId}`);

      if (!window.Razorpay) {
        await loadRazorpay();
      }

      const payment = new window.Razorpay({
        key: razorpayOrder.data.keyId,
        amount: razorpayOrder.data.amount,
        currency: razorpayOrder.data.currency,
        name: "PulseDrive Motors",
        description: `Order #${response.data.orderId}`,
        order_id: razorpayOrder.data.razorpayOrderId,
        prefill: { name: `${data.profile?.firstName || ""} ${data.profile?.lastName || ""}`.trim(), email: data.profile?.email },
        handler: async (result) => {
          try {
            await api.post("/payments/razorpay/verify", {
              orderId: response.data.orderId,
              razorpayPaymentId: result.razorpay_payment_id,
              razorpayOrderId: result.razorpay_order_id,
              razorpaySignature: result.razorpay_signature,
            });
            await loadAccount();
            setMessage(`Payment received for order #${response.data.orderId}.`);
          } catch (error) {
            setMessage(error.response?.data?.message || "Payment verification failed. Please contact support.");
          } finally {
            setCheckingOut(false);
          }
        },
        modal: { ondismiss: () => setCheckingOut(false) },
      });
      payment.open();
      setMessage(`Order #${response.data.orderId} created. Complete payment to confirm it.`);
      setActiveTab("orders");
    } catch (error) {
      setMessage(error.response?.data?.message || "Unable to place your order.");
      setCheckingOut(false);
    }
  };

  const loadRazorpay = () => new Promise((resolve, reject) => {
    const existingScript = document.querySelector('script[src="https://checkout.razorpay.com/v1/checkout.js"]');
    if (existingScript) {
      existingScript.addEventListener("load", resolve, { once: true });
      if (window.Razorpay) resolve();
      return;
    }
    const script = document.createElement("script");
    script.src = "https://checkout.razorpay.com/v1/checkout.js";
    script.onload = resolve;
    script.onerror = () => reject(new Error("Unable to load the payment gateway"));
    document.body.appendChild(script);
  });

  if (loading) return <><Navbar /><div className="account-state">Loading your garage...</div></>;

  const tabs = [
    ["overview", "Overview"],
    ["wishlist", `Wishlist (${data.wishlist.length})`],
    ["cart", `Cart (${data.cart?.totalItems || 0})`],
    ["orders", "Orders"],
    ["testDrives", "Test drives"],
    ["profile", "Profile"],
    ["notifications", `Alerts (${data.unreadCount})`],
  ];

  return (
    <><Navbar /><main className="account-page">
      <header className="account-header">
        <div><span className="eyebrow">YOUR PULSE</span><h1>Welcome back, {data.profile?.firstName || "driver"}.</h1><p>Keep track of the vehicles and experiences waiting for you.</p></div>
        <Link className="account-cta" to="/home#vehicles">Browse vehicles <span>↗</span></Link>
      </header>
      {message && <div className="account-message">{message}</div>}
      <nav className="account-tabs">{tabs.map(([id, label]) => <button key={id} className={activeTab === id ? "active" : ""} onClick={() => setActiveTab(id)}>{label}</button>)}</nav>

      {activeTab === "overview" && <section className="account-grid">
        <div className="account-panel account-panel-wide"><span className="eyebrow">PROFILE</span><h2>{data.profile?.firstName} {data.profile?.lastName}</h2><p>{data.profile?.email}</p><p>{data.profile?.phone || "Add a phone number to your profile"}</p></div>
        <div className="account-panel"><span className="eyebrow">SAVED</span><strong className="account-stat">{data.wishlist.length}</strong><p>vehicles in wishlist</p></div>
        <div className="account-panel"><span className="eyebrow">ORDERS</span><strong className="account-stat">{data.orders.length}</strong><p>orders placed</p></div>
        <div className="account-panel account-panel-wide"><span className="eyebrow">NEXT UP</span>{data.testDrives[0] ? <><h2>{data.testDrives[0].date}</h2><p>{data.testDrives[0].time} · {data.testDrives[0].status || "Scheduled"}</p></> : <><h2>No test drives booked</h2><p>Choose a vehicle and find your preferred drive time.</p></>}</div>
      </section>}

      {activeTab === "wishlist" && <Collection items={data.wishlist} onRemove={removeWishlist} />}
      {activeTab === "cart" && <section className="account-panel"><span className="eyebrow">YOUR CART</span><h2>{data.cart?.totalItems || 0} vehicles selected</h2>{data.cart?.items?.map((item) => <div className="account-row" key={item.vehicleId || item.cartItemId}><div><strong>{item.brand || "Selected vehicle"}</strong><p>{item.model} · {money(item.subtotal || item.unitPrice)}</p><div className="mt-3 flex items-center gap-3"><button type="button" className="text-button" onClick={() => updateCart(item.vehicleId, item.quantity - 1)}>-</button><span>{item.quantity}</span><button type="button" className="text-button" onClick={() => updateCart(item.vehicleId, item.quantity + 1)}>+</button><button type="button" className="text-button text-red-300" onClick={() => updateCart(item.vehicleId, 0)}>Remove</button></div></div></div>)}<div className="account-total"><span>Total</span><strong>{money(data.cart?.totalAmount)}</strong></div>{data.cart?.items?.length > 0 && <form onSubmit={handleCheckout} className="mt-8 grid gap-3"><textarea required minLength="5" value={checkout.shippingAddress} onChange={(event) => setCheckout({ ...checkout, shippingAddress: event.target.value })} placeholder="Shipping address" rows="3" className="filter-control p-3" /><input value={checkout.couponCode} onChange={(event) => setCheckout({ ...checkout, couponCode: event.target.value })} placeholder="Coupon code (optional)" className="filter-control" /><button disabled={checkingOut} className="rounded-xl bg-white px-5 py-3 font-bold text-black disabled:opacity-50">{checkingOut ? "Placing order..." : "Place Order"}</button></form>}</section>}
      {activeTab === "orders" && <section className="account-panel"><span className="eyebrow">ORDER HISTORY</span>{data.orders.length ? data.orders.map((item) => <div className="account-row" key={item.orderId}><button type="button" className="text-left" onClick={() => showOrder(item.orderId)}><strong>Order #{item.orderId}</strong><p>{item.createdAt ? new Date(item.createdAt).toLocaleString() : ""}</p></button><span>{item.status || "Processing"}</span><b>{money(item.totalAmount)}</b></div>) : <p className="empty-copy">No orders yet.</p>}{selectedOrder && <div className="mt-6 border-t border-white/10 pt-6"><h3 className="text-xl font-bold">Order #{selectedOrder.orderId}</h3><p className="mt-2 text-zinc-400">{selectedOrder.shippingAddress}</p><p className="mt-2">{selectedOrder.paymentStatus || "Payment pending"} · {selectedOrder.status}</p>{payment?.transactionId && <p className="mt-2 text-sm text-zinc-500">Transaction: {payment.transactionId}</p>}{!["CANCELLED", "COMPLETED"].includes(selectedOrder.status) && <button type="button" className="mt-4 text-sm text-red-300" onClick={() => cancelOrder(selectedOrder.orderId)}>Cancel order</button>}</div>}</section>}
      {activeTab === "testDrives" && <ListPanel eyebrow="TEST DRIVE BOOKINGS" empty="No test drives booked." items={data.testDrives} onRemove={cancelTestDrive} render={(item) => <><div><strong>{item.vehicleName || `Vehicle #${item.vehicleId}`}</strong><p>{item.date} · {item.time}</p></div><span>{item.status || "Scheduled"}</span></>} />}
      {activeTab === "profile" && <section className="account-panel"><span className="eyebrow">PROFILE SETTINGS</span><form onSubmit={saveProfile} className="mt-6 grid gap-4 sm:grid-cols-2"><input className="filter-control" placeholder="First name" value={profileForm.firstName} onChange={(event) => setProfileForm({ ...profileForm, firstName: event.target.value })} /><input className="filter-control" placeholder="Last name" value={profileForm.lastName} onChange={(event) => setProfileForm({ ...profileForm, lastName: event.target.value })} /><input className="filter-control" placeholder="Phone" value={profileForm.phone} onChange={(event) => setProfileForm({ ...profileForm, phone: event.target.value })} /><textarea className="filter-control p-3 sm:col-span-2" placeholder="Address" value={profileForm.address} onChange={(event) => setProfileForm({ ...profileForm, address: event.target.value })} /><button className="rounded-xl bg-white px-5 py-3 font-bold text-black sm:col-span-2">Save profile</button></form><form onSubmit={changePassword} className="mt-10 grid gap-4 border-t border-white/10 pt-8"><h2>Change password</h2><input className="filter-control" type="password" placeholder="Current password" value={passwordForm.currentPassword} onChange={(event) => setPasswordForm({ ...passwordForm, currentPassword: event.target.value })} required /><input className="filter-control" type="password" placeholder="New password" minLength="8" value={passwordForm.newPassword} onChange={(event) => setPasswordForm({ ...passwordForm, newPassword: event.target.value })} required /><button className="rounded-xl border border-white/10 px-5 py-3 font-bold">Change password</button></form></section>}
      {activeTab === "notifications" && <section className="account-panel"><div className="flex items-center justify-between gap-4"><span className="eyebrow">NOTIFICATIONS</span>{data.unreadCount > 0 && <button type="button" className="text-button" onClick={markAllNotifications}>Mark all read</button>}</div>{data.notifications.length ? data.notifications.map((item) => <button type="button" key={item.id} onClick={() => !item.read && markNotification(item.id)} className={`account-row w-full text-left ${item.read ? "opacity-60" : ""}`}><div><strong>{item.title}</strong><p>{item.message}</p></div><span className="text-xs text-zinc-500">{item.createdAt ? new Date(item.createdAt).toLocaleDateString() : ""}</span></button>) : <p className="mt-6 empty-copy">You have no notifications.</p>}</section>}
    </main></>
  );
}

function Collection({ items, onRemove }) {
  return <section className="account-panel"><span className="eyebrow">WISHLIST</span><h2>Vehicles worth another look</h2>{items.length ? items.map((item) => <div className="account-row" key={item.vehicleId}><Link to={`/vehicles/${item.vehicleId}`}><strong>{item.brand} {item.model}</strong><p>{item.variant || "PulseDrive collection"}</p></Link><div><strong>{money(item.price)}</strong><button className="text-button" onClick={() => onRemove(item.vehicleId)}>Remove</button></div></div>) : <p className="empty-copy">Your wishlist is ready for its first favourite.</p>}</section>;
}

function ListPanel({ eyebrow, empty, items, render, onRemove }) {
  return <section className="account-panel"><span className="eyebrow">{eyebrow}</span>{items.length ? items.map((item) => <div className="account-row" key={item.id}>{render(item)}{onRemove && <button className="text-button" onClick={() => onRemove(item.id)}>Cancel</button>}</div>) : <p className="empty-copy">{empty}</p>}</section>;
}

export default Account;
