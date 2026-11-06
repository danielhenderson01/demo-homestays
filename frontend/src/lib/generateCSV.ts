import type { Booking, Property, RentalStats, ReportType } from "./types";

export function createCsvReport(report: ReportType): {
  filename: string;
  report: string;
} {
  let generatedReport: { filename: string; report: string };
  switch (report.kind) {
    case "bookings":
      generatedReport = createBookingsReport(report.data, report.timestamp);
      break;
    case "listings":
      generatedReport = createListingsReport(report.data, report.timestamp);
      break;
    case "rentals":
      generatedReport = createRentalsReport(report.data, report.timestamp);
      break;
  }
  return generatedReport;
}

function generateCSV(data: string[][]): string {
  return data
    .map((row) =>
      row
        .map((cell) => {
          const escaped = cell.replace(/"/g, '""');
          if (/[",\n]/.test(escaped)) {
            return `"${escaped}"`;
          }
          return escaped;
        })
        .join(","),
    )
    .join("\n");
}

function createBookingsReport(data: Booking[], timestamp: string) {
  const filename = "bookings-report.csv";
  const reportData = [
    [
      "ID",
      "Property",
      "Guest",
      "Nights",
      "Total",
      "Check-in",
      "Check-out",
      "Status",
      "Timestamp",
    ],
    ...data.map((item) => [
      item.id.toString(),
      item.propertyName,
      item.guest.email,
      item.totalNights.toString(),
      `$${item.orderTotal.toFixed(2)}`,
      item.checkIn,
      item.checkOut,
      item.bookingStatus === "CONFIRMED"
        ? "Confirmed"
        : item.bookingStatus === "CANCELED"
          ? "Canceled"
          : "Pending",
      timestamp,
    ]),
  ];
  return {
    filename: filename,
    report: generateCSV(reportData),
  };
}

function createRentalsReport(data: RentalStats[], timestamp: string) {
  const filename = "rentals-report.csv";
  const reportData = [
    ["ID", "Name", "Price", "Total Nights", "Order Total", "Timestamp"],
    ...data.map((item) => [
      item.id.toString(),
      item.name,
      `$${item.price.toFixed(2)}`,
      item.totalNightsSum.toString(),
      `$${item.orderTotalSum.toFixed(2)}`,
      timestamp,
    ]),
  ];
  return {
    filename,
    report: generateCSV(reportData),
  };
}

function createListingsReport(data: Property[], timestamp: string) {
  const filename = "listings-report.csv";
  const reportData = [
    [
      "ID",
      "Host",
      "Property",
      "Category",
      "Location",
      "Beds/Baths",
      "Price",
      "Timestamp",
    ],
    ...data.map((item) => [
      item.id.toString(),
      item.host.name,
      item.name,
      item.category,
      `${item.street}, ${item.city}, ${item.state}, ${item.zipcode}`,
      `${item.beds} Beds / ${item.baths} Baths`,
      `$${item.price.toFixed(2)}`,
      timestamp,
    ]),
  ];
  return {
    filename,
    report: generateCSV(reportData),
  };
}
