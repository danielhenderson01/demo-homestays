import { QueryClient, queryOptions } from "@tanstack/react-query";
import type { AppUser, Role, UserResources } from "@/lib/types";
import { ax } from "./axiosInstance";

export function currentUserQuery() {
  return queryOptions({
    queryKey: ["currentUser"],
    queryFn: async (): Promise<AppUser> => {
      const response = await ax.get<AppUser>("/users");
      return { ...response.data };
    },
    staleTime: Infinity,
    gcTime: Infinity,
    retry: false,
  });
}

export async function requireRole(queryClient: QueryClient, roles: Role[]) {
  const user = await queryClient.query(currentUserQuery());
  if (!roles.includes(user.role)) {
    throw new Response("", { status: 403, statusText: "Forbidden" });
  }
}

export function userResourcesQuery() {
  return queryOptions({
    queryKey: ["userResources"],
    queryFn: async (): Promise<UserResources> => {
      const response = await ax.get<UserResources>("/users/resources");
      return { ...response.data };
    },
  });
}
