import { useState } from "react";
import { Button } from "@/components/ui/button";

export default function Comment({ comment }: { comment: string }) {
  const [isExpanded, setIsExpanded] = useState(false);

  function toggleExpanded() {
    setIsExpanded(!isExpanded);
  }

  const longComment = comment.length > 130;
  const displayComment =
    longComment && !isExpanded ? `${comment.slice(0, 130)}...` : comment;

  return (
    <div>
      <p className="leading-relaxed">{displayComment}</p>
      {longComment && (
        <Button variant="link" onClick={toggleExpanded}>
          {isExpanded ? "Show Less" : "Show More"}
        </Button>
      )}
    </div>
  );
}
