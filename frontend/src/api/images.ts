import type { AddImagesType } from "@/lib/schemas";
import type { APISuccessResponse, PropertyImage, S3Upload } from "@/lib/types";
import { queryOptions } from "@tanstack/react-query";
import { ax } from "./axiosInstance";
import { validateFile } from "@/lib/file-utils";

export async function addImagesToProperty({
  propertyId,
  images,
}: AddImagesType) {
  try {
    const request = await Promise.all(
      images.map(async (image) => {
        // Validate file
        const { validFile, validExt } = await validateFile(image);
        // Create object key
        const objectKey = `images/properties/${propertyId}/${crypto.randomUUID()}.${validExt}`;
        // Get presigned URL
        const presignResponse = await ax.post<S3Upload>(
          `/properties/${propertyId}/images/upload-url`,
          {
            objectKey,
          }
        );
        // Upload file to S3
        await ax.put(presignResponse.data.url, validFile);
        // Return the uploaded object key
        return { objectKey };
      })
    );
    // Send the list of uploaded object keys
    await ax.put(`/properties/${propertyId}/images`, request);
  } catch (error) {
    throw new Error("Failed to upload files.");
  }
}

export async function deleteImage(id: number): Promise<void> {
  await ax.delete(`/properties/${id}/images`);
}

export function imageListQuery(propertyId: number, params: string) {
  return queryOptions({
    queryKey: ["images", "list", propertyId, params],
    queryFn: async (): Promise<APISuccessResponse<PropertyImage[]>> => {
      const response = await ax.get<APISuccessResponse<PropertyImage[]>>(
        `/properties/${propertyId}/images${params.length > 0 ? `?${params}` : ""}`
      );
      return { ...response.data };
    },
  });
}
