import type { Review } from "@/lib/types";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Item,
  ItemContent,
  ItemDescription,
  ItemGroup,
  ItemMedia,
  ItemTitle,
} from "@/components/ui/item";
import Rating from "./Rating";
import { ScrollArea } from "../ui/scroll-area";
import { Separator } from "../ui/separator";
import { IMAGES_URL } from "@/lib/constants";

export default function PropertyReviews({ reviews }: { reviews: Review[] }) {
  return (
    <ScrollArea className="w-full">
      <ItemGroup className="gap-10">
        {reviews.map((review) => (
          <Item key={review.id}>
            <Separator className="mb-2" />
            <ItemMedia>
              <Avatar size="sm">
                <AvatarImage
                  src={`${IMAGES_URL}/samples/profile-default.jpg`}
                />
                <AvatarFallback>{review.guest.name.charAt(0)}</AvatarFallback>
              </Avatar>
            </ItemMedia>
            <ItemContent>
              <ItemTitle>{review.guest.name}</ItemTitle>
              <ItemDescription>{review.guest.email}</ItemDescription>
              <div className="mt-2 flex flex-col gap-1.5">
                <Rating rating={review.rating} />
                <p className="mt-2">{review.comment}</p>
              </div>
            </ItemContent>
          </Item>
        ))}
      </ItemGroup>
    </ScrollArea>
  );
}
