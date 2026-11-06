export type Meta = {
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type APISuccessResponse<T> = {
  data: T;
  meta?: Meta;
};

export type APIErrorResponse = {
  detail: string;
  instance: string;
  status: number;
  title: string;
};

export type UserInfo = {
  id: number;
  name: string;
  email: string;
};

export type Role = "GUEST" | "HOST";

export type AppUser = UserInfo & {
  cognitoSub: string;
  role: Role;
};

export type UserResources = {
  numProperties: number;
  numReviews: number;
  numBookings: number;
};

export type Property = {
  id: number;
  host: UserInfo;
  name: string;
  category: string;
  description: string;
  tagline: string;
  street: string;
  city: string;
  state: string;
  zipcode: string;
  beds: number;
  baths: number;
  squareFeet: number;
  price: number;
  amenities: string[];
  images: PropertyImage[];
  reviews: Review[];
  rating: RatingResponse;
};

export type PropertyImage = {
  id: number;
  propertyId: number;
  imageUrl: string;
  objectKey: string;
};

export type Review = {
  id: number;
  propertyId: number;
  guest: UserInfo;
  propertyName: string;
  rating: number;
  comment: string;
  propertyImage: string;
};

export type Address = {
  street: string;
  city: string;
  state: string;
  zipcode: string;
};

export type Booking = {
  id: number;
  propertyId: number;
  guest: UserInfo;
  host: UserInfo;
  propertyImages: string[];
  propertyName: string;
  address: Address;
  totalNights: number;
  orderTotal: number;
  pricePerNight: number;
  checkIn: string;
  checkOut: string;
  bookingStatus: string;
};

export type RentalStats = {
  id: number;
  name: string;
  price: number;
  totalNightsSum: number;
  orderTotalSum: number;
};

export type StatsTotals = {
  usersCount: number;
  bookingsCount: number;
  propertiesCount: number;
};

export type ReservationsStats = {
  properties: number;
  nights: number;
  amount: number;
};

export type RatingResponse = {
  averageRating: number;
  numRatings: number;
};

export type RentalsReport = {
  kind: "rentals";
  data: RentalStats[];
  timestamp: string;
};

export type ListingsReport = {
  kind: "listings";
  data: Property[];
  timestamp: string;
};

export type BookingsReport = {
  kind: "bookings";
  data: Booking[];
  timestamp: string;
};

export type ReportType = RentalsReport | ListingsReport | BookingsReport;

export type BookingDates = {
  checkIn: string;
  checkOut: string;
};

export type BookingInfo = {
  propertyId: number;
  propertyPrice: number;
  bookingDates: BookingDates[];
};

export type StripeResponse = {
  paymentUrl: string;
};

export type S3Upload = {
  url: string;
};

export type ReviewKPIs = {
  totalReviews: number;
  averageRating: number;
};

export type RatingDistribution = {
  rating: number;
  count: number;
};

export type AverageRatingByProperty = {
  id: number;
  name: string;
  averageRating: number;
  reviewCount: number;
};

export type AverageRatingOverTime = {
  month: string;
  averageRating: number;
  reviewCount: number;
};

export type BookingKPIs = {
  totalBookings: number;
  totalRevenue: number;
  bookedNights: number;
  averageBookingValue: number;
};

export type RevenueByMonth = {
  month: string;
  revenue: number;
};

export type BookingsByMonth = {
  month: string;
  bookings: number;
};

export type RevenueByProperty = {
  id: number;
  name: string;
  bookingCount: number;
  revenue: number;
};

export type BookingStatusDistribution = {
  bookingStatus: string;
  count: number;
};

export type HostDashboard = {
  reviewKPIs: ReviewKPIs;
  ratingDistribution: RatingDistribution[];
  averageRatingByProperty: AverageRatingByProperty[];
  averageRatingOverTime: AverageRatingOverTime[];
  bookingKPIs: BookingKPIs;
  revenueByMonth: RevenueByMonth[];
  bookingsByMonth: BookingsByMonth[];
  revenueByProperty: RevenueByProperty[];
  bookingStatusDistribution: BookingStatusDistribution[];
};
