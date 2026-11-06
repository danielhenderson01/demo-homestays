import ImageCarousel from "@/components/properties/ImageCarousel";
import PropertyRating from "@/components/properties/PropertyRating";
import ShareButton from "@/components/properties/ShareButton";
import { Separator } from "@/components/ui/separator";
import { formatQuantity } from "@/lib/formatters";
import {
  Link,
  useLoaderData,
  type ActionFunctionArgs,
  type LoaderFunctionArgs,
} from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { guestPropertyDetailQuery } from "@/api/properties";
import { existsGuestReviewQuery } from "@/api/reviews";
import {
  ArrowUp,
  CircleCheck,
  MessageSquareText,
  SquareArrowOutUpRight,
} from "lucide-react";
import { bookingInfoDetailQuery, createBooking } from "@/api/bookings";
import BookingForm from "@/components/bookings/BookingForm";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemDescription,
  ItemMedia,
  ItemTitle,
} from "@/components/ui/item";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import PropertyReviews from "@/components/reviews/PropertyReviews";
import {
  GUEST_PROPERTIES_PATH,
  GUEST_REVIEWS_PATH,
  IMAGES_URL,
} from "@/lib/constants";
import ReviewForm from "@/components/reviews/ReviewForm";
import PropertyMap from "@/components/properties/PropertyMap";
import { Fragment } from "react";
import TooltipButton from "@/components/common/TooltipButton";
import { requireRole } from "@/api/users";

export const loader =
  (queryClient: QueryClient) =>
  async ({ params }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    if (!params.id) {
      throw new Response("", {
        status: 404,
        statusText: "Not Found",
      });
    }
    const propertyId = Number(params.id);
    await queryClient.query({
      ...guestPropertyDetailQuery(propertyId),
      staleTime: "static",
    });
    await queryClient.query({
      ...existsGuestReviewQuery(propertyId),
      staleTime: "static",
    });
    await queryClient.query({
      ...bookingInfoDetailQuery(propertyId),
      staleTime: "static",
    });
    return { propertyId };
  };

export const action =
  (queryClient: QueryClient) =>
  async ({ request }: ActionFunctionArgs) => {
    await requireRole(queryClient, ["GUEST"]);
    const formData = await request.formData();
    await createBooking(formData);
    return null;
  };

export default function GuestProperty() {
  const { propertyId } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: property } = useSuspenseQuery(
    guestPropertyDetailQuery(propertyId)
  );

  const { data: existsGuestReview } = useSuspenseQuery(
    existsGuestReviewQuery(propertyId)
  );

  const sectionLinks = [
    { href: "#title", label: "Title" },
    { href: "#description", label: "Description" },
    { href: "#amenities", label: "Amenities" },
    { href: "#images", label: "Images" },
    { href: "#location", label: "Location" },
    { href: "#reviews", label: "Reviews" },
  ];

  return (
    <div className="mx-auto w-full max-w-7xl">
      {/* ---- Image ---- */}
      <div className="flex flex-col py-4">
        <img
          src={property.images[0].imageUrl || "/sample-image.jpg"}
          alt={property.name || "property image"}
          sizes="(max-width:768px) 100vw, 50vw"
          className="mx-auto max-h-100 w-full rounded-md object-cover"
        />
      </div>

      {/* ---- Columns ---- */}
      <div className="mt-4 gap-x-8 lg:grid lg:grid-cols-12">
        {/* ---- Left Column ---- */}
        <div className="px-2 lg:col-span-8">
          {/* ---- Section Links ---- */}
          <div className="mb-4 flex flex-col gap-2">
            <Separator />

            <div className="flex h-5 items-center gap-2 text-sm">
              {sectionLinks.map(({ href, label }) => (
                <Fragment key={href}>
                  <a
                    href={href}
                    className="hover:text-indigo-500 dark:text-muted-foreground dark:hover:text-indigo-500"
                  >
                    {label}
                  </a>
                  {href !== "#reviews" && <Separator orientation="vertical" />}
                </Fragment>
              ))}
            </div>

            <Separator />
          </div>

          <div className="mb-18 flex flex-col gap-4 lg:mb-auto">
            <section
              id="title"
              className="grid grid-cols-12 py-(--header-height)"
            >
              <div className="col-span-10">
                {/* ---- Host ---- */}
                <Item>
                  <ItemMedia>
                    <Avatar size="lg">
                      <AvatarImage
                        src={`${IMAGES_URL}/samples/profile-default.jpg`}
                      />
                      <AvatarFallback>
                        {property.host.name.charAt(0)}
                      </AvatarFallback>
                    </Avatar>
                  </ItemMedia>
                  <ItemContent className="gap-1">
                    <ItemTitle>Hosted by {property.host.name}</ItemTitle>
                    <ItemDescription>{property.host.email}</ItemDescription>
                  </ItemContent>
                </Item>

                <Item>
                  <ItemContent>
                    {/* ---- Name ---- */}
                    <ItemTitle className="text-3xl tracking-wider xl:text-4xl">
                      {property.name}
                    </ItemTitle>
                    {/* ---- Features ---- */}
                    <ItemDescription className="text-lg text-muted-foreground">
                      <span>
                        {formatQuantity(property.beds, "bedroom")} &middot;{" "}
                      </span>
                      <span>
                        {formatQuantity(property.baths, "bath")} &middot;{" "}
                      </span>
                      <span>{property.squareFeet} sqft </span>
                    </ItemDescription>
                  </ItemContent>
                </Item>
              </div>

              {/* ---- Actions ---- */}
              <div className="col-span-2 h-full">
                <div className="flex h-full flex-col justify-between">
                  <BookingForm />
                  <ShareButton />
                  {existsGuestReview ? (
                    <TooltipButton
                      tooltipText="Review"
                      variant="outline"
                      asChild
                    >
                      <Link to={GUEST_REVIEWS_PATH}>
                        <MessageSquareText className="size-4" />
                        <span className="hidden xl:flex">Review</span>
                      </Link>
                    </TooltipButton>
                  ) : (
                    <ReviewForm propertyId={property.id} />
                  )}
                </div>
              </div>
            </section>

            <Separator />

            {/* ---- Description ---- */}
            <section id="description" className="py-(--header-height)">
              <Item>
                <ItemContent className="flex flex-col gap-4">
                  <ItemTitle className="text-base font-semibold tracking-wider capitalize">
                    {property.tagline}
                  </ItemTitle>
                  <p className="text-base leading-loose">
                    {property.description}
                  </p>
                </ItemContent>
              </Item>
            </section>

            <Separator />

            {/* ---- Image Carousel ---- */}
            <section id="images" className="py-(--header-height)">
              <Item className="py-0">
                <ItemContent>
                  <div className="mx-6 mt-2">
                    <ImageCarousel
                      images={property.images.map((i) => i.imageUrl)}
                    />
                  </div>
                </ItemContent>
              </Item>
            </section>

            <Separator />

            {/* ---- Location ---- */}
            <section id="location" className="py-(--header-height)">
              <Item className="py-0">
                <ItemContent>
                  <ItemDescription className="text-base">
                    Location
                  </ItemDescription>
                  <ItemTitle className="mb-2 text-lg">{`${property.street}, ${property.city}, ${property.state} ${property.zipcode}`}</ItemTitle>
                  <div className="dark:brightness-85">
                    <PropertyMap propertyState={property.state} />
                  </div>
                </ItemContent>
              </Item>
            </section>
          </div>
        </div>

        {/* ---- Right Column ---- */}
        <div className="flex flex-col gap-4 lg:col-span-4">
          <Separator />

          {/* ---- Reviews ---- */}
          <section id="reviews" className="py-(--header-height)">
            <Item className="py-0">
              <ItemContent>
                <ItemTitle className="mb-2 text-base">Recent Reviews</ItemTitle>
                <PropertyRating rating={property.rating} size="lg" />
              </ItemContent>
              <ItemActions>
                <TooltipButton
                  tooltipText="See all reviews"
                  variant="outline"
                  size="sm"
                  asChild
                >
                  <Link to={`${GUEST_PROPERTIES_PATH}/${propertyId}/reviews`}>
                    More
                    <SquareArrowOutUpRight />
                  </Link>
                </TooltipButton>
              </ItemActions>
            </Item>
            <PropertyReviews reviews={property.reviews.slice(0, 4)} />
          </section>

          <Separator />

          {/* ---- Amenities ---- */}
          <section id="amenities" className="py-(--header-height)">
            <Item className="py-0">
              <ItemContent>
                <ItemTitle className="mb-2 text-base">Amenities</ItemTitle>
                <div className="grid grid-cols-2">
                  {property.amenities.map((amenity) => (
                    <div key={amenity} className="flex items-center gap-3 py-2">
                      <CircleCheck className="size-4 text-emerald-500" />
                      <span className="text-base font-semibold">{amenity}</span>
                    </div>
                  ))}
                </div>
              </ItemContent>
            </Item>
          </section>
        </div>
      </div>

      <div className="mx-auto flex w-full">
        <div className="ml-auto inline-flex">
          <TooltipButton
            tooltipText="Scroll to top"
            variant="outline"
            size="icon"
            className="aspect-square rounded-full"
          >
            <a href="#">
              <ArrowUp className="size-4" />
            </a>
          </TooltipButton>
        </div>
      </div>
    </div>
  );
}
