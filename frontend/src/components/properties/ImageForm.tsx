import "filepond/dist/filepond.min.css";
import "filepond-plugin-image-preview/dist/filepond-plugin-image-preview.css";
import { FilePond, registerPlugin } from "react-filepond";
import FilePondPluginImagePreview from "filepond-plugin-image-preview";
import FilePondPluginImageExifOrientation from "filepond-plugin-image-exif-orientation";
import { Controller, useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { addImagesSchema } from "@/lib/schemas";
import z from "zod";
import { Field, FieldError, FieldGroup, FieldSet } from "../ui/field";
import { Button } from "../ui/button";
import { Loader } from "lucide-react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { addImagesToProperty } from "@/api/images";
import { toast } from "sonner";

registerPlugin(FilePondPluginImageExifOrientation, FilePondPluginImagePreview);

export default function ImageForm({ propertyId }: { propertyId: number }) {
  const form = useForm<z.infer<typeof addImagesSchema>>({
    resolver: zodResolver(addImagesSchema),
    defaultValues: {
      propertyId,
      images: [],
    },
  });

  const queryClient = useQueryClient();

  const { mutate } = useMutation({
    mutationFn: (data: z.infer<typeof addImagesSchema>) =>
      addImagesToProperty({ ...data }),
  });

  async function onSubmit(data: z.infer<typeof addImagesSchema>) {
    mutate(data, {
      onSuccess: () => {
        queryClient.invalidateQueries({
          queryKey: ["images", "list", propertyId],
        });
        toast.success("Images added successfully");
      },
      onError: () => {
        toast.error("Failed to add images");
      },
    });
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="w-full">
      <FieldSet>
        <FieldGroup>
          <Controller
            name="images"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <FilePond
                  id="images"
                  onupdatefiles={(fileItems) => {
                    const files = fileItems.map((fileItem) => fileItem.file);
                    field.onChange(files);
                  }}
                  allowMultiple={true}
                  labelIdle={`Drag & Drop your images or <span class="filepond--label-action">Browse</span>`}
                  credits={false}
                />
                {fieldState.invalid && (
                  <FieldError errors={[fieldState.error]} />
                )}
              </Field>
            )}
          />

          <Field orientation="horizontal" className="mx-auto w-full">
            <div className="ml-auto flex items-center gap-2">
              <input type="hidden" id="id" name="id" value={propertyId} />
              <Button
                type="submit"
                variant="outline"
                className="p-4 text-sm font-bold shadow-lg"
                disabled={form.formState.isSubmitting}
              >
                {form.formState.isSubmitting ? (
                  <Loader className="size-4 animate-spin" />
                ) : (
                  <span className="text-sm font-bold">Save</span>
                )}
              </Button>
            </div>
          </Field>
        </FieldGroup>
      </FieldSet>
    </form>
  );
}
