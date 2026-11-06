import { Link, useLocation } from "react-router";
import {
  Sheet,
  SheetContent,
  SheetFooter,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Menu } from "lucide-react";
import SignOutButton from "../auth/SignOutButton";
import useAppLinks from "@/hooks/UseAppLinks";
import TooltipButton from "../common/TooltipButton";

export default function NavSheet() {
  const links = useAppLinks();

  const { pathname } = useLocation();

  return (
    <Sheet>
      <SheetTrigger asChild>
        <TooltipButton tooltipText="Menu" variant="outline" size="icon">
          <Menu />
          <span className="sr-only">Toggle links</span>
        </TooltipButton>
      </SheetTrigger>
      <SheetContent className="bg-card/90">
        <div className="mx-4 my-12 grid flex-1 items-center pl-3">
          {links.map((link) => (
            <Link
              to={link.href}
              key={`${link.label}-menu`}
              className={`${pathname === link.href ? "font-light text-indigo-600 dark:text-indigo-500" : "text-muted-foreground hover:text-foreground"} w-full text-4xl font-extralight font-stretch-expanded`}
            >
              {link.label}
            </Link>
          ))}
        </div>
        <SheetFooter>
          <SignOutButton />
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
