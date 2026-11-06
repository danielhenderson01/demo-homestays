import GuestPropertyCard from "@/components/properties/GuestPropertyCard";
import { useLoaderData, type LoaderFunctionArgs } from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { guestPropertyListQuery } from "@/api/properties";
import PageHeader from "@/components/common/PageHeader";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import SearchContainer from "@/components/properties/SearchContainer";
import { requireRole } from "@/api/users";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    await queryClient.query({
      ...guestPropertyListQuery(searchParams.toString()),
      staleTime: "static",
    });
    return { searchParams };
  };

export default function GuestProperties() {
  const { searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    guestPropertyListQuery(searchParams.toString())
  );

  const { data, meta } = result;

  return (
    <>
      <PageHeader title="Properties" subtitle="Find your perfect homestay" />

      <div className="mx-auto w-full py-2">
        <SearchContainer />
      </div>

      <div className="my-4 grid gap-10 md:grid-cols-2 md:gap-8 xl:grid-cols-3 xl:gap-6">
        {data.map((p) => (
          <GuestPropertyCard key={p.id} property={p} />
        ))}
      </div>

      {meta && <PaginationContainer meta={meta} />}
    </>
  );
}
