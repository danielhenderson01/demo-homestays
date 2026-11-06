import type { HostDashboard } from "@/lib/types";
import { ax } from "./axiosInstance";
import { queryOptions } from "@tanstack/react-query";

export function hostDashboardQuery() {
  return queryOptions({
    queryKey: [],
    queryFn: async (): Promise<HostDashboard> => {
      const response = await ax.get<HostDashboard>(`/analytics`);
      return { ...response.data };
    },
  });
}
