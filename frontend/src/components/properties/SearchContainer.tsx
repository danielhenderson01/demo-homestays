import PropertySearchForm from "./PropertySearchForm";
import PropertyFiltersForm from "./PropertyFiltersForm";

export default function SearchContainer() {
  return (
    <div className="w-full">
      <div>
        <div className="flex items-center justify-between gap-1">
          <div className="w-full max-w-3xl">
            <PropertySearchForm />
          </div>
          <div className="ml-2">
            <PropertyFiltersForm />
          </div>
        </div>
      </div>
    </div>
  );
}
