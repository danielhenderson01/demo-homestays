import { Navigation2 } from "lucide-react";
import ModeToggle from "../common/ModeToggle";
import NavSheet from "./NavSheet";
import Navlinks from "./Navlinks";

export default function Navbar() {
  return (
    <nav className="fixed top-0 left-0 z-50 h-(--header-height) w-full border-b bg-neutral-100/90 dark:bg-neutral-950/90">
      <div className="container mx-auto flex h-(--header-height) w-full max-w-7xl items-center justify-between px-4">
        <div className="pr-6">
          <Navigation2 className="size-5" />
        </div>

        <div className="hidden items-center gap-x-6 text-sm font-semibold sm:flex">
          <Navlinks />
        </div>

        <div className="ml-auto inline-flex">
          <div className="flex items-center justify-between gap-3">
            <ModeToggle />
            <NavSheet />
          </div>
        </div>
      </div>
    </nav>
  );
}
