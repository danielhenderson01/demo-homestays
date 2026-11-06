import { Button } from "@/components/ui/button";
import { CardTitle, CardContent, Card } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Link } from "react-router";

export default function GuestPaymentFail() {
  return (
    <main className="m-8 mx-auto max-w-6xl rounded-md border p-4 md:p-6">
      <h1 className="mb-10 text-center text-4xl font-bold">
        Something went wrong...
      </h1>
      <Card className="flex flex-col">
        <CardTitle className="m-3 text-xl">Payment Failed</CardTitle>
        <Separator className="mb-2" />
        <CardContent>
          <p className="mx-auto mb-2 flex">
            Sorry, there was an error while processing your payment...
          </p>
          <Button asChild size="lg" className="mx-auto mb-2 flex">
            <Link to="/">Go Back Home</Link>
          </Button>
        </CardContent>
      </Card>
    </main>
  );
}
