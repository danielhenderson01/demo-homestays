import GuestReviewCard from "@/components/reviews/GuestReviewCard";
import PageHeader from "@/components/common/PageHeader";
import { Outlet, useLoaderData, type LoaderFunctionArgs } from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import { guestReviewListQuery } from "@/api/reviews";
import { requireRole } from "@/api/users";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    await queryClient.query({
      ...guestReviewListQuery(searchParams.toString()),
      staleTime: "static",
    });
    return { searchParams };
  };

export default function GuestReviews() {
  const { searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    guestReviewListQuery(searchParams.toString())
  );

  const { data: reviews, meta } = result;

  return (
    <>
      <PageHeader title="Reviews" subtitle="Manage your reviews" />

      <Outlet />

      <div className="mt-4 grid gap-8 md:grid-cols-2 xl:grid-cols-3">
        {reviews.map((review) => (
          <GuestReviewCard key={review.id} review={review} />
        ))}
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
