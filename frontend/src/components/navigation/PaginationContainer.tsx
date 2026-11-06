import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import type { Meta } from "@/lib/types";
import { Link, useLocation } from "react-router";
import { Field, FieldLabel } from "../ui/field";
import {
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenu,
} from "../ui/dropdown-menu";
import { Button } from "../ui/button";
import { ChevronDown } from "lucide-react";

function constructUrl({
  pageNumber,
  size,
  search,
  pathname,
}: {
  pageNumber: number;
  size: number;
  search: string;
  pathname: string;
}): string {
  const searchParams = new URLSearchParams(search);
  searchParams.set("page", pageNumber.toString());
  searchParams.set("size", size.toString());
  return `${pathname}?${searchParams.toString()}`;
}

export default function PaginationContainer({ meta }: { meta: Meta }) {
  const { search, pathname } = useLocation();

  if (meta.totalPages <= 1) {
    return null;
  }

  const { number: currentPage, totalPages, size } = meta;

  const showPageNumber = (actualPageNumber: number) => actualPageNumber + 1;

  return (
    <div className="mx-auto my-8 flex w-full max-w-sm items-center justify-center">
      <div className="flex items-center justify-between gap-4">
        <Field orientation="horizontal" className="w-fit">
          <FieldLabel
            htmlFor="page-size"
            className="text-xs font-semibold text-muted-foreground"
          >
            Page Size
          </FieldLabel>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline">
                {size}
                <ChevronDown />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent>
              {[10, 20, 30].map((v, i) => (
                <DropdownMenuItem
                  key={`${v}-${i}-page-size`}
                  className="w-full"
                >
                  <Link
                    to={constructUrl({
                      pageNumber: 0,
                      size: v,
                      search,
                      pathname,
                    })}
                    className="w-full"
                  >
                    {v}
                  </Link>
                </DropdownMenuItem>
              ))}
            </DropdownMenuContent>
          </DropdownMenu>
          <Pagination>
            <PaginationContent>
              <PaginationItem>
                <PaginationPrevious
                  to={constructUrl({
                    size,
                    pageNumber:
                      currentPage - 1 < 0 ? totalPages - 1 : currentPage - 1,
                    search,
                    pathname,
                  })}
                />
              </PaginationItem>
              {totalPages < 4 ? (
                Array.from({ length: totalPages }, (_, index) => index).map(
                  (index) => (
                    <PaginationItem key={index}>
                      <PaginationLink
                        to={constructUrl({
                          pageNumber: index,
                          size,
                          search,
                          pathname,
                        })}
                        isActive={index === currentPage}
                      >
                        {showPageNumber(index)}
                      </PaginationLink>
                    </PaginationItem>
                  )
                )
              ) : (
                <span className="text-xs font-medium">
                  {showPageNumber(currentPage)} of {totalPages}
                </span>
              )}
              <PaginationItem>
                <PaginationNext
                  to={constructUrl({
                    size,
                    pageNumber:
                      currentPage + 1 === totalPages ? 0 : currentPage + 1,
                    search,
                    pathname,
                  })}
                />
              </PaginationItem>
            </PaginationContent>
          </Pagination>
        </Field>
      </div>
    </div>
  );
}
