import { Moon, Sun } from "lucide-react";
import { useTheme } from "./ThemeProvider";
import TooltipButton from "./TooltipButton";

export default function ModeToggle() {
  const { theme, setTheme } = useTheme();

  return (
    <TooltipButton
      tooltipText="Toggle Theme"
      variant="ghost"
      size="icon-sm"
      onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
    >
      <Sun className="h-[1.2rem] w-[1.2rem] scale-100 rotate-0 transition-all dark:scale-0 dark:-rotate-90" />
      <Moon className="absolute h-[1.2rem] w-[1.2rem] scale-0 rotate-90 transition-all dark:scale-100 dark:rotate-0" />
    </TooltipButton>
  );
}
