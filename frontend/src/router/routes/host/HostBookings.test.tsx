import { QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import {
  createMemoryRouter,
  isRouteErrorResponse,
  RouterProvider,
  useRouteError,
} from "react-router";
import { describe, expect, test } from "vitest";
import { guestUser } from "@/test/fixtures/users";
import { currentUserHandler } from "@/test/msw/handlers";
import { server } from "@/test/msw/server";
import { createTestQueryClient } from "@/test/queryClient";
import { loader } from "./HostBookings";

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

describe("HostBookings loader", () => {
  test("forbids a guest before loading host bookings", async () => {
    server.use(currentUserHandler(guestUser));

    const queryClient = createTestQueryClient();

    const router = createMemoryRouter(
      [
        {
          path: "/host/bookings",
          loader: loader(queryClient),
          element: <div>Host bookings</div>,
          errorElement: <TestErrorPage />,
        },
      ],
      {
        initialEntries: ["/host/bookings?page=2"],
      }
    );

    render(
      <QueryClientProvider client={queryClient}>
        <RouterProvider router={router} />
      </QueryClientProvider>
    );

    expect(await screen.findByText("403 Forbidden")).toBeVisible();

    expect(screen.queryByText("Host bookings")).not.toBeInTheDocument();
  });
});
