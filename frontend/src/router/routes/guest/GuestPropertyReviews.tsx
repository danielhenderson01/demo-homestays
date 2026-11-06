import PaginationContainer from "@/components/navigation/PaginationContainer";
import PropertyReviewCard from "@/components/reviews/PropertyReviewCard";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { GUEST_PROPERTIES_PATH, IMAGES_URL } from "@/lib/constants";
import { ArrowLeft } from "lucide-react";
import { Link, useLoaderData, type LoaderFunctionArgs } from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { propertyReviewListQuery } from "@/api/reviews";
import PageHeader from "@/components/common/PageHeader";
import { requireRole } from "@/api/users";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request, params }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    if (!params.id) {
      throw new Response("", {
        status: 404,
        statusText: "Not Found",
      });
    }
    const propertyId = Number(params.id);
    await queryClient.query({
      ...propertyReviewListQuery(propertyId, searchParams.toString()),
      staleTime: "static",
    });
    return { propertyId, searchParams };
  };

export default function GuestPropertyReviews() {
  const { propertyId, searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    propertyReviewListQuery(propertyId, searchParams.toString())
  );

  const { data: reviews, meta } = result;

  return (
    <>
      <div className="flex items-center justify-between">
        <PageHeader
          title="Reviews"
          subtitle="All reviews for this property"
          separator={false}
        />
        <Button variant="link" size="sm" className="font-bold" asChild>
          <Link to={`${GUEST_PROPERTIES_PATH}/${propertyId}`}>
            <ArrowLeft />
            Back to Property
          </Link>
        </Button>
      </div>
      <Separator />
      <div className="mt-4 grid gap-8 md:grid-cols-2 xl:grid-cols-3">
        {reviews.map((review) => (
          <PropertyReviewCard
            key={review.id}
            comment={review.comment}
            rating={review.rating}
            guestName={review.guest.name}
            profileImage={`${IMAGES_URL}/samples/profile-default.jpg`}
          />
        ))}
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
