import "@testing-library/jest-dom/vitest";

import { cleanup } from "@testing-library/react";
import { afterAll, afterEach, beforeAll, vi } from "vitest";

import { server } from "./msw/server";

// Mock Amplify at the authentication boundary
vi.mock(import("aws-amplify/auth"), async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    fetchAuthSession: vi.fn().mockResolvedValue({
      tokens: {
        idToken: "test-id-token",
      },
    }),
  };
});

beforeAll(() => {
  server.listen({
    onUnhandledRequest: "error",
  });
});

afterEach(() => {
  cleanup();
  server.resetHandlers();
});

afterAll(() => {
  server.close();
});
