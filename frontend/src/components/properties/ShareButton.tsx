import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Button } from "../ui/button";
import { Mail, Share2 } from "lucide-react";
import { FaInstagram, FaPinterest, FaTwitter } from "react-icons/fa";
import { toast } from "sonner";
import TooltipButton from "../common/TooltipButton";

export default function ShareButton() {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <TooltipButton tooltipText="Share" variant="outline">
          <Share2 className="size-4" />
          <span className="hidden xl:flex">Share</span>
        </TooltipButton>
      </PopoverTrigger>
      <PopoverContent align="center" className="mx-auto w-full p-8">
        <div className="flex justify-center gap-12">
          <div className="flex flex-col items-center justify-center gap-12">
            <Button
              size="icon"
              variant="ghost"
              onClick={() => toast.info("Pinterest")}
            >
              <FaPinterest className="size-8" />
            </Button>
            <Button
              size="icon"
              variant="ghost"
              onClick={() => toast.info("Email")}
            >
              <Mail className="size-8" />
            </Button>
          </div>
          <div className="flex flex-col items-center justify-center gap-12">
            <Button
              variant="ghost"
              size="icon"
              onClick={() => toast.info("Instagram")}
            >
              <FaInstagram className="size-8" />
            </Button>
            <Button
              size="icon"
              variant="ghost"
              onClick={() => toast.info("Twitter")}
            >
              <FaTwitter className="size-8" />
            </Button>
          </div>
        </div>
      </PopoverContent>
    </Popover>
  );
}
