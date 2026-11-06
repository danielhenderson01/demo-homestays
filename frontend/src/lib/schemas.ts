import * as z from "zod";

export const propertyFiltersSchema = z.object({
  query: z.string().optional(),
  name: z.string().optional(),
  category: z.string().optional(),
  street: z.string().optional(),
  city: z.string().optional(),
  zipcode: z.string().optional(),
  beds: z.array(z.coerce.number<number>()).optional(),
  baths: z.array(z.coerce.number<number>()).optional(),
  squareFeet: z.array(z.coerce.number<number>()).optional(),
  priceRange: z.array(z.coerce.number<number>()).optional(),
  page: z.coerce.number<number>().optional(),
  size: z.coerce.number<number>().optional(),
});

export type PropertyFilters = z.infer<typeof propertyFiltersSchema>;

export const searchSchema = z.object({
  query: z.string(),
});

export type SearchType = z.infer<typeof searchSchema>;

export const createBookingSchema = z.object({
  propertyId: z.number(),
  checkIn: z.string(),
  checkOut: z.string(),
});

export type CreateBookingType = z.infer<typeof createBookingSchema>;

export const createPropertySchema = z.object({
  name: z.string().min(2),
  category: z.string().min(2),
  description: z.string().min(2),
  tagline: z.string().min(2),
  street: z.string().min(2),
  city: z.string().min(2),
  state: z.string().min(2),
  zipcode: z.string().min(2),
  beds: z.coerce.number<number>().nonnegative(),
  baths: z.coerce.number<number>().nonnegative(),
  squareFeet: z.coerce.number<number>().positive(),
  price: z.coerce.number<number>().positive(),
  amenities: z.array(z.string()).min(1),
});

export type CreatePropertyType = z.infer<typeof createPropertySchema>;

export const updatePropertySchema = createPropertySchema.extend({
  id: z.number(),
});

export type UpdatePropertyType = z.infer<typeof updatePropertySchema>;

export const createReviewSchema = z.object({
  propertyId: z.coerce.number<number>(),
  rating: z.coerce.number<number>().min(1).max(5),
  comment: z.string().min(1),
});

export type CreateReviewType = z.infer<typeof createReviewSchema>;

export const addImagesSchema = z.object({
  propertyId: z.number(),
  images: z.array(z.file()).min(1),
});

export type AddImagesType = z.infer<typeof addImagesSchema>;
