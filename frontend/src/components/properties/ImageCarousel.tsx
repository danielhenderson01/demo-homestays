import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel";

export default function ImageCarousel({ images }: { images: string[] }) {
  return (
    <Carousel>
      <CarouselContent>
        {images.map((image, index) => {
          return (
            <CarouselItem key={index}>
              <img
                src={image}
                alt="image"
                className="mx-auto max-h-96 w-full rounded-md object-cover"
              />
            </CarouselItem>
          );
        })}
      </CarouselContent>
      <CarouselPrevious variant="ghost" />
      <CarouselNext variant="ghost" />
    </Carousel>
  );
}
