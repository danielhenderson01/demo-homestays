import type { PropsWithChildren, ReactElement } from "react";
import { Authenticator } from "@aws-amplify/ui-react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, type RenderOptions } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import {
  createMemoryRouter,
  MemoryRouter,
  type RouteObject,
} from "react-router";
import { RouterProvider } from "react-router/dom";
import { createTestQueryClient } from "./queryClient";

type ProviderProps = PropsWithChildren<{
  queryClient: QueryClient;
}>;

function Providers({ children, queryClient }: ProviderProps) {
  return (
    <Authenticator.Provider>
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </Authenticator.Provider>
  );
}

type RenderWithProvidersOptions = Omit<RenderOptions, "wrapper"> & {
  initialEntries?: string[];
  queryClient?: QueryClient;
};

export function renderWithProviders(
  ui: ReactElement,
  {
    initialEntries = ["/"],
    queryClient = createTestQueryClient(),
    ...options
  }: RenderWithProvidersOptions = {}
) {
  function Wrapper({ children }: PropsWithChildren) {
    return (
      <Providers queryClient={queryClient}>
        <MemoryRouter initialEntries={initialEntries}>{children}</MemoryRouter>
      </Providers>
    );
  }

  return {
    user: userEvent.setup(),
    queryClient,

    ...render(ui, {
      wrapper: Wrapper,
      ...options,
    }),
  };
}

type RenderWithDataRouterOptions = {
  initialEntries?: string[];
  queryClient?: QueryClient;
};

export function renderWithDataRouter(
  createRoutes: (queryClient: QueryClient) => RouteObject[],
  {
    initialEntries = ["/"],
    queryClient = createTestQueryClient(),
  }: RenderWithDataRouterOptions = {}
) {
  const router = createMemoryRouter(createRoutes(queryClient), {
    initialEntries,
  });

  const result = render(
    <Providers queryClient={queryClient}>
      <RouterProvider router={router} />
    </Providers>
  );

  return {
    user: userEvent.setup(),
    queryClient,
    router,
    ...result,
  };
}
