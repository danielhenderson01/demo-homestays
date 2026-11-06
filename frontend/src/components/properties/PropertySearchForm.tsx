import { useLocation, useNavigate, useSearchParams } from "react-router";
import { Controller, useForm } from "react-hook-form";
import z from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { searchSchema } from "@/lib/schemas";
import { Field, FieldError, FieldGroup, FieldSet } from "../ui/field";
import { Input } from "../ui/input";
import { Loader, RotateCcw, Search } from "lucide-react";
import TooltipButton from "../common/TooltipButton";

export default function PropertySearchForm() {
  const navigate = useNavigate();
  const { pathname } = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();

  const form = useForm<z.infer<typeof searchSchema>>({
    resolver: zodResolver(searchSchema),
    defaultValues: {
      query: searchParams.get("query") || "",
    },
  });

  function onSubmit(data: z.infer<typeof searchSchema>) {
    setSearchParams(
      new URLSearchParams({
        query: data.query,
      })
    );
  }

  function onReset() {
    form.reset({ query: "" });
    navigate(pathname);
  }

  return (
    <form onSubmit={form.handleSubmit(onSubmit)}>
      <FieldGroup>
        <FieldSet>
          <div className="flex items-center gap-3">
            {/* ---- Query ---- */}
            <Controller
              name="query"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <Input
                    {...field}
                    id="query"
                    aria-invalid={fieldState.invalid}
                    placeholder="Search..."
                    autoComplete="off"
                    className="shadow-sm"
                  />
                  {fieldState.invalid && (
                    <FieldError errors={[fieldState.error]} />
                  )}
                </Field>
              )}
            />

            {/* --- Submit Button --- */}
            <TooltipButton
              tooltipText="Search"
              type="submit"
              variant="outline"
              disabled={form.formState.isSubmitting}
            >
              {form.formState.isSubmitting ? (
                <Loader className="animate-spin" />
              ) : (
                <Search className="size-4" />
              )}
            </TooltipButton>

            {/* --- Reset Button --- */}
            <TooltipButton
              tooltipText="Reset"
              type="button"
              variant="outline"
              disabled={form.formState.isSubmitting}
              onClick={() => onReset()}
            >
              <RotateCcw className="size-4" />
            </TooltipButton>
          </div>
        </FieldSet>
      </FieldGroup>
    </form>
  );
}
