import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

export default function Modal({
  children,
  dialogTrigger,
  dialogDescription,
  dialogTitle,
}: {
  children: React.ReactNode;
  dialogTrigger: React.ReactNode;
  dialogDescription?: string;
  dialogTitle?: string;
}) {
  return (
    <Dialog>
      <DialogTrigger asChild>{dialogTrigger}</DialogTrigger>
      <DialogContent className="sm:max-w-xl">
        {(dialogTitle || dialogDescription) && (
          <DialogHeader>
            {dialogTitle && <DialogTitle>{dialogTitle}</DialogTitle>}
            {dialogDescription && (
              <DialogDescription>{dialogDescription}</DialogDescription>
            )}
          </DialogHeader>
        )}
        {children}
      </DialogContent>
    </Dialog>
  );
}
