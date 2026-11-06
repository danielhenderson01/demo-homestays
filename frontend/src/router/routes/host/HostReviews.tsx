import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import PageHeader from "@/components/common/PageHeader";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import ReviewDetails from "@/components/reviews/ReviewDetails";
import { useLoaderData, type LoaderFunctionArgs } from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { hostReviewListQuery } from "@/api/reviews";
import { requireRole } from "@/api/users";
import EmptyResource from "@/components/common/EmptyResource";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["HOST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    await queryClient.query({
      ...hostReviewListQuery(searchParams.toString()),
      staleTime: "static",
    });
    return { searchParams };
  };

export default function HostReviews() {
  const { searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    hostReviewListQuery(searchParams.toString())
  );

  const { data: reviews, meta } = result;

  return (
    <>
      <PageHeader
        title="Reviews"
        subtitle="Manage reviews for your properties"
      />
      <div className="flex flex-col rounded-lg border p-4">
        {reviews.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Property</TableHead>
                <TableHead>Guest</TableHead>
                <TableHead>Rating</TableHead>
                <TableHead className="text-center">Details</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
              {reviews.map((review) => (
                <TableRow key={`${review.id}-${review.propertyId}`}>
                  <TableCell className="truncate">
                    {review.propertyName}
                  </TableCell>
                  <TableCell className="truncate">
                    {review.guest.name}
                  </TableCell>
                  <TableCell>{review.rating}</TableCell>
                  <TableCell className="text-center">
                    <ReviewDetails review={review} />
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <EmptyResource kind="review" />
        )}
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
