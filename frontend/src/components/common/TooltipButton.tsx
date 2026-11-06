import { Button } from "../ui/button";
import { Tooltip, TooltipContent, TooltipTrigger } from "../ui/tooltip";

export default function TooltipButton({
  children,
  tooltipText,
  type = "button",
  variant = "default",
  size = "default",
  className,
  disabled = false,
  onClick,
  asChild = false,
}: {
  children: React.ReactNode;
  tooltipText: string;
  type?: "button" | "submit" | "reset";
  variant?:
    "default" | "link" | "outline" | "secondary" | "ghost" | "destructive";
  size?:
    "default" | "xs" | "sm" | "lg" | "icon" | "icon-xs" | "icon-sm" | "icon-lg";
  className?: string;
  disabled?: boolean;
  onClick?: React.MouseEventHandler<HTMLButtonElement>;
  asChild?: boolean;
}) {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <Button
          type={type}
          variant={variant}
          size={size}
          className={className}
          disabled={disabled}
          onClick={onClick}
          asChild={asChild}
        >
          {children}
        </Button>
      </TooltipTrigger>
      <TooltipContent>{tooltipText}</TooltipContent>
    </Tooltip>
  );
}
