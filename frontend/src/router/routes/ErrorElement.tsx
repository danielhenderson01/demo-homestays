import { useRouteError, Link, isRouteErrorResponse } from "react-router";
import { Button } from "@/components/ui/button";
import { ArrowLeft } from "lucide-react";

export default function ErrorElement() {
  const error = useRouteError();

  const isRouteError = isRouteErrorResponse(error);

  return (
    <main className="grid min-h-screen place-items-center px-8">
      <div className="text-center">
        {isRouteError && (
          <>
            <p className="text-9xl font-semibold text-primary">
              {error.status}
            </p>
            <h1 className="mt-4 mb-6 text-3xl font-semibold tracking-tight sm:text-5xl">
              {error.statusText}
            </h1>
          </>
        )}
        <p className="text-lg leading-7 sm:text-2xl">
          {isRouteError ? (
            error.status === 403 ? (
              <>Sorry, you do not have permission to access this resource.</>
            ) : error.status === 404 ? (
              <>Sorry, we couldn't find what you are looking for.</>
            ) : (
              <>Sorry, there was an error.</>
            )
          ) : (
            <>Sorry, there was an error.</>
          )}
        </p>
        <div className="mt-8">
          <Button
            size="lg"
            variant="outline"
            className="shadow-lg shadow-primary/20"
          >
            <Link to="/" className="flex items-center gap-2 font-bold">
              <ArrowLeft />
              Back Home
            </Link>
          </Button>
        </div>
      </div>
    </main>
  );
}
