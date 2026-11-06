import { HttpResponse, http } from "msw";
import { describe, expect, test } from "vitest";
import { currentUserQuery, requireRole } from "@/api/users";
import { guestUser, hostUser } from "@/test/fixtures/users";
import { server } from "@/test/msw/server";
import { createTestQueryClient } from "@/test/queryClient";
import { currentUserHandler } from "@/test/msw/handlers";

describe("currentUserQuery", () => {
  test("fetches the current user with the Cognito ID token", async () => {
    server.use(
      http.get("*/users", ({ request }) => {
        expect(request.headers.get("authorization")).toBe(
          "Bearer test-id-token"
        );
        return HttpResponse.json(guestUser);
      })
    );

    const queryClient = createTestQueryClient();

    const user = await queryClient.query(currentUserQuery());

    expect(user).toEqual(guestUser);
  });
});

describe("requireRole", () => {
  test("allows a guest to access a guest resource", async () => {
    server.use(currentUserHandler(guestUser));
    const queryClient = createTestQueryClient();
    await expect(requireRole(queryClient, ["GUEST"])).resolves.toBeUndefined();
  });

  test("allows a host to access a host resource", async () => {
    server.use(currentUserHandler(hostUser));
    const queryClient = createTestQueryClient();
    await expect(requireRole(queryClient, ["HOST"])).resolves.toBeUndefined();
  });

  test("forbids a guest from accessing a host resource", async () => {
    server.use(currentUserHandler(guestUser));
    const queryClient = createTestQueryClient();
    const result = requireRole(queryClient, ["HOST"]);
    await expect(result).rejects.toHaveProperty("status", 403);
    await expect(result).rejects.toHaveProperty("statusText", "Forbidden");
  });

  test("forbids a host from accessing a guest resource", async () => {
    server.use(currentUserHandler(hostUser));
    const queryClient = createTestQueryClient();
    const result = requireRole(queryClient, ["GUEST"]);
    await expect(result).rejects.toHaveProperty("status", 403);
    await expect(result).rejects.toHaveProperty("statusText", "Forbidden");
  });
});
