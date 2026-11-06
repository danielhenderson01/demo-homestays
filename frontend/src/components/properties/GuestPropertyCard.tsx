import { GUEST_PROPERTIES_PATH } from "@/lib/constants";
import type { Property } from "@/lib/types";
import PropertyRating from "./PropertyRating";
import { Bath, Bed, Ruler } from "lucide-react";
import { Link } from "react-router";

export default function GuestPropertyCard({
  property,
}: {
  property: Property;
}) {
  return (
    <article className="group relative rounded-md border">
      <Link to={`${GUEST_PROPERTIES_PATH}/${property.id}`}>
        {/* image */}
        <div className="relative mb-2 overflow-hidden rounded-t-md">
          <img
            src={property.images[0]?.imageUrl || "/sample-image.jpg"}
            alt={property.name}
            className="aspect-video transform object-cover transition-transform duration-500 group-hover:scale-105"
          />
        </div>
        <div className="p-4">
          <div className="flex items-center justify-between">
            {/* name */}
            <h3 className="mt-2 text-sm font-semibold capitalize">
              {property.name.substring(0, 30)}
            </h3>
            {/* rating */}
            <PropertyRating rating={property.rating} size="sm" />
          </div>
          {/* tagline */}
          <p className="mt-1 text-sm text-muted-foreground capitalize">
            {property.tagline}
          </p>
          <div className="mt-1 flex items-center justify-between">
            {/* price */}
            <p className="mt-1 text-sm font-semibold">
              <span>${property.price}/night</span>
            </p>
            {/* location */}
            <p className="mt-1 text-xs font-medium">
              <span>
                {property.city}, {property.state}
              </span>
            </p>
          </div>
        </div>
        <div className="flex items-center justify-around rounded-b-md bg-neutral-100 p-3 text-xs font-semibold dark:bg-card">
          <span className="flex items-center gap-1">
            <Bath className="size-4" />
            {property.baths} Bath
          </span>
          <span className="flex items-center gap-1">
            <Ruler className="size-4" />
            {property.squareFeet} sq ft
          </span>
          <span className="flex items-center gap-1">
            <Bed className="size-4" />
            {property.beds} Bed
          </span>
        </div>
      </Link>
    </article>
  );
}
