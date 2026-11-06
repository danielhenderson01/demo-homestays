import Comment from "./Comment";
import Rating from "./Rating";
import { Card, CardContent } from "../ui/card";

export default function PropertyReviewCard({
  comment,
  rating,
  guestName,
  profileImage,
}: {
  comment: string;
  rating: number;
  guestName: string;
  profileImage: string;
}) {
  return (
    <Card>
      <CardContent>
        <div className="flex flex-col gap-3 p-1">
          <div className="flex items-center gap-4">
            <img
              src={profileImage}
              alt="profile"
              className="aspect-square size-12 rounded-xl"
            />
            <div className="flex flex-col gap-1.5">
              <Rating rating={rating} />
              <p className="text-sm">{guestName}</p>
            </div>
          </div>
          <Comment comment={comment} />
        </div>
      </CardContent>
    </Card>
  );
}
