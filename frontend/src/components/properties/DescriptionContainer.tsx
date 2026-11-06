import { useState } from "react";

export default function DescriptionContainer({ text }: { text: string }) {
  const [isFullText, setIsFullText] = useState(false);

  function toggleDescription() {
    return setIsFullText(!isFullText);
  }

  const maxDisplayWords = 50;

  const words = text.split(" ");
  const isLongText = words.length > maxDisplayWords;

  const renderedText =
    isLongText && !isFullText
      ? `${words.splice(0, maxDisplayWords).join(" ")}...`
      : text;

  return (
    <article className="mx-auto w-full">
      <p className="text-lg leading-relaxed">{renderedText}</p>
      {isLongText && (
        <div className="flex">
          <div className="ml-auto inline-flex">
            <div
              className="text-xs font-bold text-indigo-500 hover:cursor-pointer hover:text-indigo-600"
              onClick={toggleDescription}
            >
              {isFullText ? "Show Less" : "Show More"}
            </div>
          </div>
        </div>
      )}
    </article>
  );
}
