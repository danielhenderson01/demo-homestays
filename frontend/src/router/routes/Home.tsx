import { currentUserQuery } from "@/api/users";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { GUEST_DASHBOARD_PATH, HOST_DASHBOARD_PATH } from "@/lib/constants";
import type { QueryClient } from "@tanstack/react-query";
import { Form, redirect } from "react-router";

export const action = (queryClient: QueryClient) => async () => {
  const user = await queryClient.query(currentUserQuery());
  if (user.role === "HOST") {
    return redirect(HOST_DASHBOARD_PATH);
  }
  if (user.role === "GUEST") {
    return redirect(GUEST_DASHBOARD_PATH);
  }
  return null;
};

export default function Home() {
  return (
    <main className="grid min-h-screen place-items-center px-8">
      <div className="flex flex-col gap-6">
        <div>
          <p className="text-3xl font-semibold tracking-wide md:text-4xl dark:text-muted-foreground/80">
            Demo
          </p>
          <p className="text-4xl font-semibold tracking-wide text-primary md:text-5xl">
            Homestays
          </p>
        </div>
        <Separator />
        <p className="text-xl leading-relaxed font-medium md:text-2xl dark:text-muted-foreground">
          Discover Unique Stays with Local Charm
        </p>
        <Form method="POST">
          <Button
            type="submit"
            size="lg"
            variant="outline"
            className="font-semibold text-primary md:py-5 md:text-lg dark:text-indigo-600"
          >
            Go to Dashboard
          </Button>
        </Form>
      </div>
    </main>
  );
}
