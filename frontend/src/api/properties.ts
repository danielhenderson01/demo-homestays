import { ax } from "./axiosInstance";
import { queryOptions } from "@tanstack/react-query";
import type { CreatePropertyType, UpdatePropertyType } from "../lib/schemas";
import type { Property, APISuccessResponse } from "../lib/types";

export async function createProperty(data: CreatePropertyType) {
  await ax.post<Property>("/properties", data);
}

export async function updateProperty(
  property: UpdatePropertyType
): Promise<Property | null> {
  const response = await ax.put<Property>(
    `/properties/${property.id}`,
    property
  );
  return { ...response.data };
}

export async function deleteProperty(id: number): Promise<void> {
  await ax.delete(`/properties/${id}`);
}

export function hostPropertyDetailQuery(id: number) {
  return queryOptions({
    queryKey: ["properties", "detail", id],
    queryFn: async (): Promise<Property> => {
      try {
        const response = await ax.get<Property>(`/properties/host/${id}`);
        return { ...response.data };
      } catch (error) {
        throw new Response("", {
          status: 404,
          statusText: "Not Found",
        });
      }
    },
  });
}

export function hostPropertyListQuery(params: string = "") {
  return queryOptions({
    queryKey: ["properties", "list", params],
    queryFn: async (): Promise<APISuccessResponse<Property[]>> => {
      const response = await ax.get<APISuccessResponse<Property[]>>(
        `/properties/host?${params}`
      );
      return { ...response.data };
    },
  });
}

export function guestPropertyDetailQuery(id: number) {
  return queryOptions({
    queryKey: ["properties", "detail", id],
    queryFn: async (): Promise<Property> => {
      try {
        const response = await ax.get<Property>(`/properties/${id}`);
        return { ...response.data };
      } catch (error) {
        throw new Response("", {
          status: 404,
          statusText: "Not Found",
        });
      }
    },
  });
}

export function guestPropertyListQuery(params: string = "") {
  return queryOptions({
    queryKey: ["properties", "list", params],
    queryFn: async (): Promise<APISuccessResponse<Property[]>> => {
      const response = await ax.get<APISuccessResponse<Property[]>>(
        `/properties?${params}`
      );
      return { ...response.data };
    },
  });
}

export function featuredPropertiesListQuery() {
  return queryOptions({
    queryKey: ["properties", "featured"],
    queryFn: async (): Promise<Property[]> => {
      const response = await ax.get<Property[]>("/properties/recent?limit=3");
      return response.data;
    },
  });
}

export function countPropertiesQuery() {
  return queryOptions({
    queryKey: ["properties", "count"],
    queryFn: async (): Promise<number> => {
      const response = await ax.get<{ count: number }>("/properties/count");
      return response.data.count;
    },
  });
}
