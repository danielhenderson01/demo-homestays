import useAppLinks from "@/hooks/UseAppLinks";
import { useLocation, Link } from "react-router";

export default function Navlinks() {
  const links = useAppLinks();

  const { pathname } = useLocation();

  return (
    <>
      {links.map((link) => (
        <Link
          to={link.href}
          key={`${link.label}-navbar`}
          className={`${pathname === link.href ? "text-indigo-600 hover:underline dark:text-indigo-500" : "hover:underline dark:text-muted-foreground"} tracking-wider`}
        >
          {link.label}
        </Link>
      ))}
    </>
  );
}
