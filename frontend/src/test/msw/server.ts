import { setupServer } from "msw/node";

// Global server without default handlers.
// Tests should explicitly establish the backend state they depend on.
export const server = setupServer();
