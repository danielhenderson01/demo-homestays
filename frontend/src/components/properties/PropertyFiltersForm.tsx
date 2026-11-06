import { useLocation, useNavigate, useSearchParams } from "react-router";
import { Controller, useForm, type UseFormReturn } from "react-hook-form";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { propertyFiltersSchema } from "@/lib/schemas";
import {
  Field,
  FieldDescription,
  FieldError,
  FieldGroup,
  FieldLabel,
  FieldSet,
} from "../ui/field";
import { Input } from "../ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { CATEGORIES } from "@/lib/constants";
import { Button } from "../ui/button";
import { Filter, Loader, RotateCcw, Search } from "lucide-react";
import { Slider } from "../ui/slider";
import { Sheet, SheetContent, SheetTrigger } from "../ui/sheet";
import { cn } from "@/lib/utils";
import { Separator } from "../ui/separator";
import { ScrollArea } from "../ui/scroll-area";
import TooltipButton from "../common/TooltipButton";

export default function PropertyFiltersForm() {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();

  const form = useForm<z.infer<typeof propertyFiltersSchema>>({
    resolver: zodResolver(propertyFiltersSchema),
    defaultValues: {
      name: searchParams.get("name") || "",
      category: searchParams.get("category") || "",
      street: searchParams.get("street") || "",
      city: searchParams.get("city") || "",
      zipcode: searchParams.get("zipcode") || "",
      beds: [
        Number(searchParams.get("minBeds") || 0),
        Number(searchParams.get("maxBeds") || 9),
      ],
      baths: [
        Number(searchParams.get("minBaths") || 0),
        Number(searchParams.get("maxBaths") || 9),
      ],
      squareFeet: [
        Number(searchParams.get("minSquareFeet") || 0),
        Number(searchParams.get("maxSquareFeet") || 10000),
      ],
      priceRange: [
        Number(searchParams.get("minPrice") || 0),
        Number(searchParams.get("maxPrice") || 1000),
      ],
      page: 0,
      size: 10,
    },
  });

  async function onSubmit(data: z.infer<typeof propertyFiltersSchema>) {
    const params = new URLSearchParams({
      name: data.name || "",
      category: data.category || "",
      street: data.street || "",
      city: data.city || "",
      zipcode: data.zipcode || "",
      minBeds: data.beds ? data.beds[0].toString() : "",
      maxBeds: data.beds ? data.beds[1].toString() : "",
      minBaths: data.baths ? data.baths[0].toString() : "",
      maxBaths: data.baths ? data.baths[1].toString() : "",
      minSquareFeet: data.squareFeet ? data.squareFeet[0].toString() : "",
      maxSquareFeet: data.squareFeet ? data.squareFeet[1].toString() : "",
      minPrice: data.priceRange ? data.priceRange[0].toString() : "",
      maxPrice: data.priceRange ? data.priceRange[1].toString() : "",
      page: data.page?.toString() || "",
      size: data.size?.toString() || "",
    });
    setSearchParams(params);
  }

  function onReset() {
    form.reset();
    navigate(pathname);
  }

  return (
    <Sheet>
      <SheetTrigger asChild>
        <TooltipButton tooltipText="Filter properties" variant="outline">
          <Filter />
        </TooltipButton>
      </SheetTrigger>
      <SheetContent side="left" className="bg-card dark:bg-card/95">
        <ScrollArea className="h-full w-full">
          <form onSubmit={form.handleSubmit(onSubmit)}>
            <FieldGroup className="container mx-auto w-full p-6">
              <FieldSet className="flex w-full flex-col gap-3">
                <NameController form={form} />
                <CategoryController form={form} />
                <Separator />
                <PriceController form={form} />
                <Separator />
                <SquareFeetController form={form} />
                <Separator />
                <BedsController form={form} />
                <Separator />
                <BathsController form={form} />
                <Separator />
                <StreetController form={form} />
                <CityController form={form} />
                <ZipcodeController form={form} />
              </FieldSet>
              <SubmitButton isSubmitting={form.formState.isSubmitting} />
              <ResetButton
                disabled={form.formState.isSubmitting}
                onClick={() => onReset()}
              />
            </FieldGroup>
          </form>
        </ScrollArea>
      </SheetContent>
    </Sheet>
  );
}

type TFiltersForm = {
  query?: string | undefined;
  name?: string | undefined;
  category?: string | undefined;
  street?: string | undefined;
  city?: string | undefined;
  zipcode?: string | undefined;
  beds?: number[] | undefined;
  baths?: number[] | undefined;
  squareFeet?: number[] | undefined;
  priceRange?: number[] | undefined;
  page?: number | undefined;
  size?: number | undefined;
};

type FiltersControllerProps = {
  form: UseFormReturn<TFiltersForm, any, TFiltersForm>;
  className?: string;
};

function CategoryController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
      <Controller
        name="category"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field orientation="vertical" data-invalid={fieldState.invalid}>
            <FieldLabel htmlFor="category">Category</FieldLabel>
            <Select
              name={field.name}
              value={field.value}
              onValueChange={field.onChange}
            >
              <SelectTrigger id="category" aria-invalid={fieldState.invalid}>
                <SelectValue placeholder="Select" />
              </SelectTrigger>
              <SelectContent position="popper">
                <SelectItem value="">Select</SelectItem>
                {CATEGORIES.map(({ value, label }) => (
                  <SelectItem key={value} value={value}>
                    {label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
          </Field>
        )}
      />
    </div>
  );
}

function NameController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
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
            {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
          </Field>
        )}
      />
    </div>
  );
}

function BedsController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
      <Controller
        name="beds"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <FieldLabel htmlFor="beds">Beds</FieldLabel>
            <FieldDescription className="text-center">
              <span className="font-medium tabular-nums">
                {field.value ? field.value[0] : 0}
              </span>{" "}
              -{" "}
              <span className="font-medium tabular-nums">
                {field.value ? field.value[1] : 9}
              </span>
            </FieldDescription>
            <Slider
              value={field.value}
              onValueChange={(value) => field.onChange(value)}
              max={9}
              min={0}
              step={1}
              className="mb-1 w-full"
            />
          </Field>
        )}
      />
    </div>
  );
}

function BathsController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
      <Controller
        name="baths"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <FieldLabel htmlFor="baths">Baths</FieldLabel>
            <FieldDescription className="text-center">
              <span className="font-medium tabular-nums">
                {field.value ? field.value[0] : 0}
              </span>{" "}
              -{" "}
              <span className="font-medium tabular-nums">
                {field.value ? field.value[1] : 9}
              </span>
            </FieldDescription>
            <Slider
              value={field.value}
              onValueChange={(value) => field.onChange(value)}
              max={9}
              min={0}
              step={1}
              className="mb-1 w-full"
            />
          </Field>
        )}
      />
    </div>
  );
}

function PriceController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
      <Controller
        name="priceRange"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <FieldLabel htmlFor="priceRange">Price Range</FieldLabel>
            <FieldDescription className="text-center">
              <span className="font-medium tabular-nums">
                ${field.value ? field.value[0] : 0}
              </span>{" "}
              -{" "}
              <span className="font-medium tabular-nums">
                ${field.value ? field.value[1] : 1000}
              </span>
            </FieldDescription>
            <Slider
              value={field.value}
              onValueChange={(value) => field.onChange(value)}
              max={1000}
              min={0}
              step={10}
              className="mb-1 w-full"
            />
          </Field>
        )}
      />
    </div>
  );
}

function SquareFeetController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
      <Controller
        name="squareFeet"
        control={form.control}
        render={({ field, fieldState }) => (
          <Field data-invalid={fieldState.invalid}>
            <FieldLabel htmlFor="squareFeet">Square Feet</FieldLabel>
            <FieldDescription className="text-center">
              <span className="font-medium tabular-nums">
                {field.value ? field.value[0] : 0}
              </span>{" "}
              -{" "}
              <span className="font-medium tabular-nums">
                {field.value ? field.value[1] : 10000}
              </span>
            </FieldDescription>
            <Slider
              value={field.value}
              onValueChange={(value) => field.onChange(value)}
              max={10000}
              min={0}
              step={500}
              className="mb-1 w-full"
            />
          </Field>
        )}
      />
    </div>
  );
}

function StreetController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
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
            {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
          </Field>
        )}
      />
    </div>
  );
}

function CityController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
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
            {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
          </Field>
        )}
      />
    </div>
  );
}

function ZipcodeController({ form, className }: FiltersControllerProps) {
  return (
    <div className={cn("", className)}>
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
            {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
          </Field>
        )}
      />
    </div>
  );
}

function SubmitButton({
  isSubmitting,
  className,
}: {
  isSubmitting: boolean;
  className?: string;
}) {
  return (
    <Button type="submit" disabled={isSubmitting} className={cn("", className)}>
      {isSubmitting ? (
        <Loader className="size-4 animate-spin" />
      ) : (
        <>
          <Search className="size-4" />
          <span className="text-sm font-semibold">Search</span>
        </>
      )}
    </Button>
  );
}

function ResetButton({
  disabled,
  onClick,
  className,
}: {
  disabled: boolean;
  onClick: () => void;
  className?: string;
}) {
  return (
    <Button
      type="button"
      variant="outline"
      disabled={disabled}
      onClick={onClick}
      className={cn("hover:text-red-400", className)}
    >
      <RotateCcw className="size-4" />
      <span className="text-sm font-semibold">Reset</span>
    </Button>
  );
}
