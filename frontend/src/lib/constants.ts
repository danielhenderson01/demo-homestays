export const IMAGES_URL = import.meta.env.VITE_IMAGES_URL || "/images";

export const GUEST_DASHBOARD_PATH = "/guest/dashboard";
export const HOST_DASHBOARD_PATH = "/host/dashboard";

export const GUEST_PROPERTIES_PATH = "/guest/properties";
export const HOST_PROPERTIES_PATH = "/host/properties";

export const GUEST_REVIEWS_PATH = "/guest/reviews";
export const HOST_REVIEWS_PATH = "/host/reviews";

export const GUEST_BOOKINGS_PATH = "/guest/bookings";
export const HOST_BOOKINGS_PATH = "/host/bookings";

export const HOST_CREATE_PROPERTY_PATH = "/host/properties/create";

export const CATEGORIES = [
  { value: "CABIN", label: "Cabin" },
  { value: "APARTMENT", label: "Apartment" },
  { value: "CONDO", label: "Condo" },
  { value: "HOUSE", label: "House" },
  { value: "COTTAGE", label: "Cottage" },
  { value: "ROOM", label: "Room" },
  { value: "STUDIO", label: "Studio" },
  { value: "OTHER", label: "Other" },
] as const;

export const AMENITIES = [
  "WiFi",
  "Kitchen",
  "Laundry",
  "Free Parking",
  "Pool",
  "Hot Tub",
  "Elevator",
  "Dishwasher",
  "Smart TV",
  "Other",
] as const;

export const PROPERTY_DEFAULT_VALUES = {
  name: "Example Property",
  category: "HOUSE",
  description:
    "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Dignissimos error expedita harum impedit laboriosam provident reiciendis repudiandae! Commodi consectetur corporis ex fuga hic itaque molestiae praesentium provident, qui. Harum, maiores?",
  tagline: "Lorem ipsum dolor sit amet!",
  street: "123 Main Street",
  city: "Pawnee",
  state: "Indiana",
  zipcode: "12345",
  beds: 4,
  baths: 2,
  squareFeet: 1800,
  price: 140,
  amenities: [],
};
