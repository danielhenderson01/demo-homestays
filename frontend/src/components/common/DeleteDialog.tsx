import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Loader, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function DeleteDialog({
  id,
  isPending,
  deleteAction,
}: {
  id: number;
  isPending: boolean;
  deleteAction: (id: number) => void;
}) {
  return (
    <AlertDialog>
      <AlertDialogTrigger asChild disabled={isPending}>
        <Button size="icon-sm" variant="ghost">
          <Trash2 className="text-red-600" />
        </Button>
      </AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
          <AlertDialogDescription>
            This action cannot be undone.
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction
            variant="destructive"
            disabled={isPending}
            onClick={() => deleteAction(id)}
          >
            {isPending ? <Loader className="animate-spin" /> : <Trash2 />}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
