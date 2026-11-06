import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Controller, useForm } from "react-hook-form";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSet,
} from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Checkbox } from "@/components/ui/checkbox";
import { InputGroup, InputGroupTextarea } from "@/components/ui/input-group";
import { Loader } from "lucide-react";
import { useNavigate } from "react-router";
import { createPropertySchema } from "@/lib/schemas";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { US_STATES } from "@/lib/coordinates";
import {
  AMENITIES,
  CATEGORIES,
  HOST_PROPERTIES_PATH,
  PROPERTY_DEFAULT_VALUES,
} from "@/lib/constants";
import { toast } from "sonner";
import { createProperty } from "@/api/properties";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function HostCreateProperty() {
  const queryClient = useQueryClient();

  const { mutate } = useMutation({
    mutationFn: (property: z.infer<typeof createPropertySchema>) =>
      createProperty(property),
  });

  const navigate = useNavigate();

  const form = useForm<z.infer<typeof createPropertySchema>>({
    resolver: zodResolver(createPropertySchema),
    defaultValues: PROPERTY_DEFAULT_VALUES,
  });

  async function onSubmit(data: z.infer<typeof createPropertySchema>) {
    mutate(data, {
      onSuccess: () => {
        queryClient.invalidateQueries({
          queryKey: ["properties"],
        });
        toast.success("Property created successfully.");
        navigate(HOST_PROPERTIES_PATH);
      },
      onError: () => {
        toast.error("Failed to create property.");
      },
    });
  }

  return (
    <div className="mx-auto flex w-full flex-col py-4">
      <Card className="border-none bg-background ring-0 outline-none">
        <CardHeader>
          <CardTitle className="text-lg">Create Property</CardTitle>
          <CardDescription>
            Fill in the form below to create a new property
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={form.handleSubmit(onSubmit)} className="w-full">
            <FieldGroup>
              <div className="flex flex-col gap-6 md:grid md:grid-cols-2">
                {/* ---- Name ---- */}
                <Controller
                  name="name"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="name">Name</FieldLabel>
                      <Input
                        {...field}
                        id="name"
                        aria-invalid={fieldState.invalid}
                        placeholder="Enter name..."
                        autoComplete="off"
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Tagline ---- */}
                <Controller
                  name="tagline"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="tagline">Tagline</FieldLabel>
                      <Input
                        {...field}
                        id="tagline"
                        aria-invalid={fieldState.invalid}
                        placeholder="Enter tagline..."
                        autoComplete="off"
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Price ---- */}
                <Controller
                  name="price"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="price">Price ($)</FieldLabel>
                      <Input
                        type="number"
                        {...field}
                        id="price"
                        aria-invalid={fieldState.invalid}
                        autoComplete="off"
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Square Feet ---- */}
                <Controller
                  name="squareFeet"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="squareFeet">Square Feet</FieldLabel>
                      <Input
                        type="number"
                        {...field}
                        id="squareFeet"
                        aria-invalid={fieldState.invalid}
                        autoComplete="off"
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Street ---- */}
                <div className="col-span-2">
                  <Controller
                    name="street"
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <Field data-invalid={fieldState.invalid}>
                        <FieldLabel htmlFor="street">Street</FieldLabel>
                        <Input
                          {...field}
                          id="street"
                          aria-invalid={fieldState.invalid}
                          placeholder="Enter street..."
                          autoComplete="off"
                        />
                        {fieldState.invalid && (
                          <FieldError errors={[fieldState.error]} />
                        )}
                      </Field>
                    )}
                  />
                </div>

                {/* ---- City ---- */}
                <Controller
                  name="city"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="city">City</FieldLabel>
                      <Input
                        {...field}
                        id="city"
                        aria-invalid={fieldState.invalid}
                        placeholder="Enter city..."
                        autoComplete="off"
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- State ---- */}
                <Controller
                  name="state"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field
                      orientation="vertical"
                      data-invalid={fieldState.invalid}
                    >
                      <FieldLabel htmlFor="state">State</FieldLabel>
                      <Select
                        name={field.name}
                        value={field.value}
                        onValueChange={field.onChange}
                      >
                        <SelectTrigger
                          id="state"
                          aria-invalid={fieldState.invalid}
                        >
                          <SelectValue placeholder="Select" />
                        </SelectTrigger>
                        <SelectContent position="popper">
                          {US_STATES.map((state) => (
                            <SelectItem key={state} value={state}>
                              {state}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Zipcode ---- */}
                <Controller
                  name="zipcode"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="zipcode">Zipcode</FieldLabel>
                      <Input
                        {...field}
                        id="zipcode"
                        aria-invalid={fieldState.invalid}
                        placeholder="Enter zipcode..."
                        autoComplete="off"
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Category ---- */}
                <Controller
                  name="category"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field
                      orientation="vertical"
                      data-invalid={fieldState.invalid}
                    >
                      <FieldLabel htmlFor="category">Category</FieldLabel>
                      <Select
                        name={field.name}
                        value={field.value}
                        onValueChange={field.onChange}
                      >
                        <SelectTrigger
                          id="category"
                          aria-invalid={fieldState.invalid}
                        >
                          <SelectValue placeholder="Select" />
                        </SelectTrigger>
                        <SelectContent position="popper">
                          {CATEGORIES.map(({ value, label }) => (
                            <SelectItem key={value} value={value}>
                              {label}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Amenities ---- */}
                <div className="col-span-2">
                  <Controller
                    name="amenities"
                    control={form.control}
                    render={({ field, fieldState }) => (
                      <FieldGroup>
                        <FieldSet data-invalid={fieldState.invalid}>
                          <FieldLegend variant="label">Amenities</FieldLegend>
                          <FieldGroup
                            data-slot="checkbox-group"
                            className="grid grid-cols-3 md:grid-cols-4"
                          >
                            {AMENITIES.map((value) => (
                              <Field
                                key={value}
                                orientation="horizontal"
                                data-invalid={fieldState.invalid}
                              >
                                <Checkbox
                                  id={`checkbox-amenity-${value}`}
                                  name={field.name}
                                  aria-invalid={fieldState.invalid}
                                  checked={field.value.includes(value)}
                                  onCheckedChange={(checked) => {
                                    const newValue = checked
                                      ? [...field.value, value]
                                      : field.value.filter(
                                          (value) => value !== value
                                        );
                                    field.onChange(newValue);
                                  }}
                                />
                                <FieldLabel
                                  htmlFor={`checkbox-amenity-${value}`}
                                  className="font-normal"
                                >
                                  {value}
                                </FieldLabel>
                              </Field>
                            ))}
                          </FieldGroup>
                        </FieldSet>
                        {fieldState.invalid && (
                          <FieldError errors={[fieldState.error]} />
                        )}
                      </FieldGroup>
                    )}
                  />
                </div>

                {/* ---- Beds ---- */}
                <Controller
                  name="beds"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="beds">Beds</FieldLabel>
                      <Input
                        type="number"
                        {...field}
                        id="beds"
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />

                {/* ---- Baths ---- */}
                <Controller
                  name="baths"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="baths">Baths</FieldLabel>
                      <Input
                        type="number"
                        {...field}
                        id="baths"
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && (
                        <FieldError errors={[fieldState.error]} />
                      )}
                    </Field>
                  )}
                />
              </div>

              {/* ---- Description ---- */}
              <Controller
                name="description"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor="description">Description</FieldLabel>
                    <InputGroup>
                      <InputGroupTextarea
                        {...field}
                        id="description"
                        placeholder="Enter description..."
                        rows={6}
                        className="min-h-24 resize-none"
                        aria-invalid={fieldState.invalid}
                      />
                    </InputGroup>
                    {fieldState.invalid && (
                      <FieldError errors={[fieldState.error]} />
                    )}
                  </Field>
                )}
              />

              {/* ---- Images ---- */}
              {/* <Controller */}
              {/*   name="images" */}
              {/*   control={form.control} */}
              {/*   render={({ field, fieldState }) => ( */}
              {/*     <Field data-invalid={fieldState.invalid}> */}
              {/*       <FieldLabel htmlFor="images">Images</FieldLabel> */}
              {/*       <FilePond */}
              {/*         id="images" */}
              {/*         onupdatefiles={(fileItems) => { */}
              {/*           const files = fileItems.map( */}
              {/*             (fileItem) => fileItem.file */}
              {/*           ); */}
              {/*           field.onChange(files); */}
              {/*         }} */}
              {/*         allowMultiple={true} */}
              {/*         labelIdle={`Drag & Drop your images or <span class="filepond--label-action">Browse</span>`} */}
              {/*         credits={false} */}
              {/*       /> */}
              {/*       {fieldState.invalid && ( */}
              {/*         <FieldError errors={[fieldState.error]} /> */}
              {/*       )} */}
              {/*     </Field> */}
              {/*   )} */}
              {/* /> */}

              {/* ---- Submit ---- */}
              <Field orientation="horizontal" className="mx-auto w-full">
                <div className="ml-auto flex items-center gap-2">
                  <Button
                    variant="outline"
                    type="submit"
                    size="lg"
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
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
