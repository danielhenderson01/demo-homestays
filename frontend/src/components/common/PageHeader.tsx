import { Separator } from "../ui/separator";

export default function PageHeader({
  title,
  subtitle,
  separator = true,
}: {
  title: string;
  subtitle?: string;
  separator?: boolean;
}) {
  return (
    <div className="flex flex-col gap-1 py-4">
      <h1 className="text-xl capitalize">{title}</h1>
      {subtitle && <p className="text-muted-foreground">{subtitle}</p>}
      {separator && <Separator />}
    </div>
  );
}
