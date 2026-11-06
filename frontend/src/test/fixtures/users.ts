import type { AppUser } from "@/lib/types";

export const guestUser = {
  id: 1001,
  name: "Test Guest",
  email: "guest@example.test",
  cognitoSub: "test-guest-cognito-sub",
  role: "GUEST",
} satisfies AppUser;

export const hostUser = {
  id: 1002,
  name: "Test Host",
  email: "host@example.test",
  cognitoSub: "test-host-cognito-sub",
  role: "HOST",
} satisfies AppUser;

export function buildGuestUser(overrides: Partial<AppUser> = {}): AppUser {
  return {
    ...guestUser,
    ...overrides,
  };
}

export function buildHostUser(overrides: Partial<AppUser> = {}): AppUser {
  return {
    ...hostUser,
    ...overrides,
  };
}
