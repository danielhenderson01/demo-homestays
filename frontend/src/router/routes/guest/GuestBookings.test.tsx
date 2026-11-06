import { QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import {
  createMemoryRouter,
  isRouteErrorResponse,
  RouterProvider,
  useRouteError,
} from "react-router";
import { describe, expect, test } from "vitest";
import { hostUser } from "@/test/fixtures/users";
import { currentUserHandler } from "@/test/msw/handlers";
import { server } from "@/test/msw/server";
import { createTestQueryClient } from "@/test/queryClient";
import { loader } from "./GuestBookings";

function TestErrorPage() {
  const error = useRouteError();

  if (isRouteErrorResponse(error)) {
    return (
      <div>
        {error.status} {error.statusText}
      </div>
    );
  }

  return <div>Unexpected error</div>;
}

describe("GuestBookings loader", () => {
  test("forbids a host before loading guest bookings", async () => {
    server.use(currentUserHandler(hostUser));

    const queryClient = createTestQueryClient();

    const router = createMemoryRouter(
      [
        {
          path: "/guest/bookings",
          loader: loader(queryClient),
          element: <div>Guest bookings</div>,
          errorElement: <TestErrorPage />,
        },
      ],
      {
        initialEntries: ["/guest/bookings?page=2"],
      }
    );

    render(
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
      </QueryClientProvider>
    );

    expect(await screen.findByText("403 Forbidden")).toBeVisible();

    expect(screen.queryByText("Guest bookings")).not.toBeInTheDocument();
  });
});
