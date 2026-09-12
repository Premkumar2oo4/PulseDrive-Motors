import { Link } from "react-router-dom";

function Footer() {
	return (
		<footer className="company-footer">
			<div className="company-footer-main">
				<div className="company-footer-brand">
					<Link to="/" className="company-footer-logo">Pulse<span>Drive</span></Link>
					<p>Curated cars. Clear decisions. A better way to find your next drive.</p>
				</div>

				<div className="company-footer-column">
					<span>Explore</span>
					<Link to="/home">Showroom</Link>
					<Link to="/home#categories">Categories</Link>
					<Link to="/home#test-drive">Book a test drive</Link>
				</div>

				<div className="company-footer-column">
					<span>Company</span>
					<Link to="/home#about">About PulseDrive</Link>
					<a href="mailto:hello@pulsedrive.com">Contact us</a>
					<a href="tel:+919876543210">+91 98765 43210</a>
				</div>

				<div className="company-footer-contact">
					<span>Visit the showroom</span>
					<p>12 Motor Avenue<br />Bengaluru, Karnataka 560001</p>
					<small>Mon-Sat · 9:00 AM-7:00 PM</small>
				</div>
			</div>

			<div className="company-footer-bottom">
				<span>© {new Date().getFullYear()} PulseDrive Motors. All rights reserved.</span>
				<span>Drive something worth remembering.</span>
			</div>
		</footer>
	);
}

export default Footer;
