import type { QueryClient } from "@tanstack/react-query";
import { type RouteObject } from "react-router";

import Home, { action as homeAction } from "./routes/Home.tsx";
import ErrorElement from "./routes/ErrorElement.tsx";

import GuestBookings, {
  loader as guestBookingsLoader,
} from "./routes/guest/GuestBookings.tsx";
import GuestDashboard, {
  loader as guestDashboardLoader,
} from "./routes/guest/GuestDashboard.tsx";
import GuestLayout from "./routes/guest/GuestLayout.tsx";
import GuestProperties, {
  loader as guestPropertiesLoader,
} from "./routes/guest/GuestProperties.tsx";
import GuestProperty, {
  loader as guestPropertyLoader,
  action as guestPropertyAction,
} from "./routes/guest/GuestProperty.tsx";
import GuestPropertyReviews, {
  loader as guestPropertyReviewsLoader,
} from "./routes/guest/GuestPropertyReviews.tsx";
import GuestReviews, {
  loader as guestReviewsLoader,
} from "./routes/guest/GuestReviews.tsx";

import HostBookings, {
  loader as hostBookingsLoader,
} from "./routes/host/HostBookings.tsx";
import HostCreateProperty from "./routes/host/HostCreateProperty.tsx";
import HostDashboard, {
  loader as hostDashboardLoader,
} from "./routes/host/HostDashboard.tsx";
import HostLayout from "./routes/host/HostLayout.tsx";
import HostProperties, {
  loader as hostPropertiesLoader,
} from "./routes/host/HostProperties.tsx";
import HostProperty, {
  loader as hostPropertyLoader,
  action as hostPropertyAction,
} from "./routes/host/HostProperty.tsx";
import HostPropertyImages, {
  loader as hostPropertyImagesLoader,
} from "./routes/host/HostPropertyImages.tsx";
import HostReviews, {
  loader as hostReviewsLoader,
} from "./routes/host/HostReviews.tsx";
import GuestPaymentSuccess, {
  loader as paymentSuccessLoader,
} from "./routes/guest/GuestPaymentSuccess.tsx";
import GuestPaymentFail from "./routes/guest/GuestPaymentFail.tsx";

export function createAppRoutes(queryClient: QueryClient): RouteObject[] {
  return [
    {
      path: "/",
      element: <Home />,
      action: homeAction(queryClient),
    },
    {
      path: "/guest",
      element: <GuestLayout />,
      errorElement: <ErrorElement />,
      children: [
        {
          path: "dashboard",
          element: <GuestDashboard />,
          loader: guestDashboardLoader(queryClient),
        },
        {
          path: "bookings",
          children: [
            {
              index: true,
              element: <GuestBookings />,
              loader: guestBookingsLoader(queryClient),
            },
            {
              path: "payment/success/:id",
              element: <GuestPaymentSuccess />,
              loader: paymentSuccessLoader(queryClient),
            },
            {
              path: "payment/fail",
              element: <GuestPaymentFail />,
            },
          ],
        },
        {
          path: "properties",
          children: [
            {
              index: true,
              element: <GuestProperties />,
              loader: guestPropertiesLoader(queryClient),
            },
            {
              path: ":id",
              element: <GuestProperty />,
              loader: guestPropertyLoader(queryClient),
              action: guestPropertyAction(queryClient),
            },
            {
              path: ":id/reviews",
              element: <GuestPropertyReviews />,
              loader: guestPropertyReviewsLoader(queryClient),
            },
          ],
        },
        {
          path: "reviews",
          element: <GuestReviews />,
          loader: guestReviewsLoader(queryClient),
        },
      ],
    },
    {
      path: "/host",
      element: <HostLayout />,
      errorElement: <ErrorElement />,
      children: [
        {
          path: "dashboard",
          element: <HostDashboard />,
          loader: hostDashboardLoader(queryClient),
        },
        {
          path: "bookings",
          element: <HostBookings />,
          loader: hostBookingsLoader(queryClient),
        },
        {
          path: "properties/create",
          element: <HostCreateProperty />,
        },
        {
          path: "properties",
          element: <HostProperties />,
          loader: hostPropertiesLoader(queryClient),
        },
        {
          path: "properties/:id",
          element: <HostProperty />,
          loader: hostPropertyLoader(queryClient),
          action: hostPropertyAction(queryClient),
        },
        {
          path: "properties/:id/images",
          element: <HostPropertyImages />,
          loader: hostPropertyImagesLoader(queryClient),
        },
        {
          path: "reviews",
          element: <HostReviews />,
          loader: hostReviewsLoader(queryClient),
        },
      ],
    },
  ];
}
