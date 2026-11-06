import { Button } from "@/components/ui/button";
import { HOST_PROPERTIES_PATH } from "@/lib/constants";
import { ArrowRight } from "lucide-react";
import {
  Link,
  useLoaderData,
  useSubmit,
  type ActionFunctionArgs,
  type LoaderFunctionArgs,
} from "react-router";
import { QueryClient, useSuspenseQuery } from "@tanstack/react-query";
import { hostPropertyDetailQuery } from "@/api/properties";
import { Input } from "@/components/ui/input";
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
import { US_STATES } from "@/lib/coordinates";
import { AMENITIES, CATEGORIES } from "@/lib/constants";
import { updatePropertySchema } from "@/lib/schemas";
import { toast } from "sonner";
import { updateProperty } from "@/api/properties";
import {
  Card,
  CardAction,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { requireRole } from "@/api/users";

export const loader =
  (queryClient: QueryClient) =>
  async ({ params }: LoaderFunctionArgs) => {
    await requireRole(queryClient, ["HOST"]);
    if (!params.id) {
      throw new Response("", {
        status: 404,
        statusText: "Not Found",
      });
    }
    const propertyId = Number(params.id);
    await queryClient.query({
      ...hostPropertyDetailQuery(propertyId),
      staleTime: "static",
    });
    return { propertyId };
  };

export const action =
  (queryClient: QueryClient) =>
  async ({ request }: ActionFunctionArgs) => {
    const data = await request.json();
    const res = await updateProperty(data);
    if (!res) {
      toast.error("Failed to update property.");
    } else {
      await queryClient.invalidateQueries({
        queryKey: ["properties", "detail", res.id],
      });
      toast.success("Property updated successfully.");
    }
    return null;
  };

export default function HostProperty() {
  const { propertyId } = useLoaderData() as Awaited<
    ReturnType<ReturnType<typeof loader>>
  >;

  const { data: property } = useSuspenseQuery(
    hostPropertyDetailQuery(propertyId)
  );

  const form = useForm<z.infer<typeof updatePropertySchema>>({
    resolver: zodResolver(updatePropertySchema),
    defaultValues: { ...property },
  });

  const submit = useSubmit();

  function onSubmit(data: z.infer<typeof updatePropertySchema>) {
    submit(data, { method: "post", encType: "application/json" });
  }

  return (
    <div className="mx-auto flex w-full flex-col py-4">
      <Card className="border-none bg-background ring-0 outline-none">
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
              <Link to={`${HOST_PROPERTIES_PATH}/${property.id}/images`}>
                Images
                <ArrowRight />
              </Link>
            </Button>
          </CardAction>
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
                          // className="min-w-[120px]"
                        >
                          <SelectValue placeholder="Select" />
                        </SelectTrigger>
                        <SelectContent position="popper">
                          {/*<SelectItem value="auto">Auto</SelectItem>*/}
                          {/*<SelectSeparator />*/}
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

              <Field orientation="horizontal" className="mx-auto w-full">
                <div className="ml-auto flex items-center gap-2">
                  <input type="hidden" id="id" name="id" value={propertyId} />
                  <Button
                    type="submit"
                    variant="outline"
                    className="p-4 text-sm font-bold shadow-sm"
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
