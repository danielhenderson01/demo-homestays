import { HttpResponse, http } from "msw";

import type { AppUser } from "@/lib/types";

const USERS_ENDPOINT = "*/users";

export function currentUserHandler(user: AppUser) {
  return http.get(USERS_ENDPOINT, () => HttpResponse.json(user));
}

export function unauthenticatedUserHandler() {
  return http.get(
    USERS_ENDPOINT,
    () =>
      new HttpResponse(null, {
        status: 401,
      })
  );
}

export function currentUserServerErrorHandler() {
  return http.get(USERS_ENDPOINT, () =>
    HttpResponse.json(
      {
        message: "Internal server error",
      },
      {
        status: 500,
      }
    )
  );
}
