import { useQueryClient } from "@tanstack/react-query";
import { Button } from "../ui/button";
import { LogOut } from "lucide-react";
import { signOut } from "aws-amplify/auth";
import { toast } from "sonner";

export default function SignOutButton() {
  const queryClient = useQueryClient();

  async function handleSignOut() {
    try {
      queryClient.clear();
      await signOut();
      window.location.href = "/";
    } catch (error) {
      toast.error("Something went wrong...", {
        description: "Please try again",
      });
    }
  }

  return (
    <Button variant="ghost" onClick={handleSignOut}>
      <LogOut className="mr-0.5" />
      Sign Out
    </Button>
  );
}
