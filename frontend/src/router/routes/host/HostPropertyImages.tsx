import DeleteDialog from "@/components/common/DeleteDialog";
import ImageForm from "@/components/properties/ImageForm";
import PaginationContainer from "@/components/navigation/PaginationContainer";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useTransition } from "react";
import { Link, useLoaderData, type LoaderFunctionArgs } from "react-router";
import {
  QueryClient,
  useMutation,
  useQueryClient,
  useSuspenseQuery,
} from "@tanstack/react-query";
import { toast } from "sonner";
import { deleteImage, imageListQuery } from "@/api/images";
import { Button } from "@/components/ui/button";
import { HOST_PROPERTIES_PATH } from "@/lib/constants";
import { ArrowLeft } from "lucide-react";
import { Separator } from "@/components/ui/separator";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { requireRole } from "@/api/users";

export const loader =
  (queryClient: QueryClient) =>
  async ({ request, params }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["HOST"]);
    const url = new URL(request.url);
    const searchParams = url.searchParams;
    if (!params.id) {
      throw new Response("", {
        status: 404,
        statusText: "Not Found",
      });
    }
    const propertyId = Number(params.id);
    await queryClient.query({
      ...imageListQuery(propertyId, searchParams.toString()),
      staleTime: "static",
    });
    return { propertyId, searchParams };
  };

export default function HostPropertyImages() {
  const { propertyId, searchParams } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: result } = useSuspenseQuery(
    imageListQuery(propertyId, searchParams.toString())
  );

  const queryClient = useQueryClient();

  const { mutate } = useMutation({
    mutationFn: (imageId: number) => deleteImage(imageId),
  });

  const [isPending, startTransition] = useTransition();

  const handleDeleteImage = (imageId: number) => {
    startTransition(async () => {
      mutate(imageId, {
        onSuccess: () => {
          queryClient.invalidateQueries({
            queryKey: ["images", "list", propertyId, searchParams.toString()],
          });
        },
        onError: () => {
          toast.error("Failed to delete image");
        },
      });
    });
  };

  const { data: images, meta } = result;

  return (
    <div className="mx-auto flex w-full max-w-6xl flex-col py-4">
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Update Property</CardTitle>
          <CardDescription>
            Fill in the form below to update this property
          </CardDescription>
          <CardAction>
            <Button
              variant="outline"
              className="text-sm font-bold shadow-sm"
              asChild
            >
              <Link to={`${HOST_PROPERTIES_PATH}/${propertyId}`}>
                <ArrowLeft />
                Property
              </Link>
            </Button>
          </CardAction>
        </CardHeader>
        <CardContent className="my-4 md:px-6 lg:px-8 xl:px-10">
          <div className="flex flex-col">
            <p className="font-medium">Add Images</p>
            <p className="mb-6 text-sm text-muted-foreground">
              Use the area below to add images to this property by dragging and
              dropping or browsing for images on your device.
            </p>
            <ImageForm propertyId={propertyId} />
          </div>
          <Separator className="my-6" />
          <div className="flex flex-col">
            <p className="font-medium">Remove Images</p>
            <p className="text-sm text-muted-foreground">
              Press the red trash icon on an image to remove it from this
              property.
            </p>
            <div className="mt-8 grid gap-6 lg:grid-cols-2">
              {images.map((image) => (
                <Card
                  className="relative mx-auto flex aspect-video w-full max-w-2xl overflow-hidden"
                  key={image.id}
                >
                  <img
                    src={image.imageUrl}
                    alt="preview"
                    className="w-full object-cover"
                  />
                  <div className="absolute mx-auto flex h-10 w-full items-center justify-between bg-black/80">
                    <div className="absolute top-3 left-3">
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <span className="absolute truncate text-xs text-white dark:text-muted-foreground">
                            {image.objectKey.length > 30
                              ? `${image.objectKey.slice(0, 30)}...`
                              : image.objectKey}
                          </span>
                        </TooltipTrigger>
                        <TooltipContent>{image.objectKey}</TooltipContent>
                      </Tooltip>
                    </div>
                    <div className="absolute top-1.5 right-1.5">
                      <DeleteDialog
                        deleteAction={handleDeleteImage}
                        id={image.id}
                        isPending={isPending}
                      />
                    </div>
                  </div>
                </Card>
              ))}
            </div>
            {meta && <PaginationContainer meta={meta} />}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
