import HostPropertyCard from "@/components/properties/HostPropertyCard";
import PageHeader from "@/components/common/PageHeader";
import { Plus } from "lucide-react";
import { HOST_CREATE_PROPERTY_PATH } from "@/lib/constants";
import { Link, useLoaderData, type LoaderFunctionArgs } from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { hostPropertyListQuery } from "@/api/properties";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import SearchContainer from "@/components/properties/SearchContainer";
import { requireRole } from "@/api/users";
import TooltipButton from "@/components/common/TooltipButton";
import EmptyResource from "@/components/common/EmptyResource";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["HOST"]);
    const url = new URL(request.url);

    const searchParams = url.searchParams;

    await queryClient.query({
      ...hostPropertyListQuery(searchParams.toString()),
      staleTime: "static",
    });

    return { searchParams };
  };

export default function HostProperties() {
  const { searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    hostPropertyListQuery(searchParams.toString())
  );

  const { data, meta } = result;

  return (
    <>
      <div className="flex items-center justify-between">
        <div className="w-full">
          <PageHeader
            title="Properties"
            subtitle="Manage your properties"
            separator={false}
          />
        </div>
        <div>
          <TooltipButton
            variant="outline"
            tooltipText="Add new property"
            asChild
          >
            <Link to={HOST_CREATE_PROPERTY_PATH}>
              <Plus />
            </Link>
          </TooltipButton>
        </div>
      </div>

      <SearchContainer />

      <div className="mt-6 grid gap-10 md:grid-cols-2 md:gap-8 xl:grid-cols-3 xl:gap-6">
        {data.length > 0 ? (
          data.map((p) => <HostPropertyCard key={p.id} property={p} />)
        ) : (
          <EmptyResource kind="property" />
        )}
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
