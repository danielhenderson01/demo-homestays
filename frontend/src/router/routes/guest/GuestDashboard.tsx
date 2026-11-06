import {
  countPropertiesQuery,
  featuredPropertiesListQuery,
} from "@/api/properties";
import { requireRole, userResourcesQuery } from "@/api/users";
import GuestPropertyCard from "@/components/properties/GuestPropertyCard";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
  CardAction,
} from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import {
  GUEST_BOOKINGS_PATH,
  GUEST_PROPERTIES_PATH,
  GUEST_REVIEWS_PATH,
} from "@/lib/constants";
import { useSuspenseQuery, type QueryClient } from "@tanstack/react-query";
import { Link } from "react-router";

export const loader = (queryClient: QueryClient) => async () => {
  await requireRole(queryClient, ["GUEST"]);
  await queryClient.query({ ...userResourcesQuery(), staleTime: "static" });
  await queryClient.query({
    ...featuredPropertiesListQuery(),
    staleTime: "static",
  });
  await queryClient.query({ ...countPropertiesQuery(), staleTime: "static" });
};

export default function GuestDashboard() {
  const { data: resources } = useSuspenseQuery(userResourcesQuery());

  const { data: properties } = useSuspenseQuery(featuredPropertiesListQuery());

  const { data: count } = useSuspenseQuery(countPropertiesQuery());

  return (
    <div className="flex flex-1 flex-col">
      <div className="@container/main flex flex-1 flex-col gap-2">
        <div className="flex flex-col gap-8 py-4 md:gap-12 md:py-6">
          <div className="grid grid-cols-1 gap-6 px-3 lg:px-5 @xl/main:grid-cols-2 @5xl/main:grid-cols-3">
            {/* --- Properties --- */}
            <Card>
              <CardHeader>
                <CardDescription>Properties</CardDescription>
                <CardTitle className="text-xl tabular-nums sm:text-base md:text-xl lg:text-2xl">
                  Browse {count} listings
                </CardTitle>
                <CardAction>
                  <Button
                    variant="outline"
                    className="p-4 text-sm font-bold shadow-sm"
                    size="xs"
                    asChild
                  >
                    <Link to={GUEST_PROPERTIES_PATH}>Browse</Link>
                  </Button>
                </CardAction>
              </CardHeader>
            </Card>

            {/* --- Bookings --- */}
            <Card>
              <CardHeader>
                <CardDescription>My Bookings</CardDescription>
                <CardTitle className="text-xl tabular-nums sm:text-base md:text-xl lg:text-2xl">
                  Manage {resources.numBookings} bookings
                </CardTitle>
                <CardAction>
                  <Button
                    variant="outline"
                    className="p-4 text-sm font-bold shadow-sm"
                    size="xs"
                    asChild
                  >
                    <Link to={GUEST_BOOKINGS_PATH}>Manage</Link>
                  </Button>
                </CardAction>
              </CardHeader>
            </Card>

            {/* --- Reviews --- */}
            <Card>
              <CardHeader>
                <CardDescription>My Reviews</CardDescription>
                <CardTitle className="text-xl tabular-nums sm:text-base md:text-xl lg:text-2xl">
                  Manage {resources.numReviews} reviews
                </CardTitle>
                <CardAction>
                  <Button
                    variant="outline"
                    className="p-4 text-sm font-bold shadow-sm"
                    size="xs"
                    asChild
                  >
                    <Link to={GUEST_REVIEWS_PATH}>Manage</Link>
                  </Button>
                </CardAction>
              </CardHeader>
            </Card>
          </div>

          <div className="px-4 lg:px-6">
            <div className="flex flex-col overflow-hidden">
              <h3 className="text-xl leading-snug tracking-wider lg:text-2xl">
                Featured Properties
              </h3>
              <Separator className="my-2" />
              <div>
                <div className="my-4 grid gap-10 md:grid-cols-2 md:gap-8 xl:grid-cols-3 xl:gap-6">
                  {properties.map((p) => (
                    <GuestPropertyCard key={p.id} property={p} />
                  ))}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
