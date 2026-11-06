import {
  GUEST_PROPERTIES_PATH,
  GUEST_BOOKINGS_PATH,
  GUEST_REVIEWS_PATH,
  HOST_BOOKINGS_PATH,
  HOST_PROPERTIES_PATH,
  HOST_REVIEWS_PATH,
  GUEST_DASHBOARD_PATH,
  HOST_DASHBOARD_PATH,
} from "@/lib/constants";
import { fetchAuthSession } from "aws-amplify/auth";
import { useEffect, useState } from "react";

type AppLink = {
  href: string;
  label: string;
};

export default function useAppLinks() {
  const [links, setLinks] = useState<AppLink[]>([]);

  useEffect(() => {
    async function getLinks() {
      const session = await fetchAuthSession();
      if (!session.tokens) return;
      const role = session.tokens.idToken?.payload["custom:role"] as string;
      if (role === "GUEST") {
        setLinks([
          { href: GUEST_DASHBOARD_PATH, label: "Dashboard" },
          { href: GUEST_PROPERTIES_PATH, label: "Properties" },
          { href: GUEST_BOOKINGS_PATH, label: "Bookings" },
          { href: GUEST_REVIEWS_PATH, label: "Reviews" },
        ]);
      } else if (role === "HOST") {
        setLinks([
          { href: HOST_DASHBOARD_PATH, label: "Dashboard" },
          { href: HOST_PROPERTIES_PATH, label: "Properties" },
          { href: HOST_BOOKINGS_PATH, label: "Bookings" },
          { href: HOST_REVIEWS_PATH, label: "Reviews" },
        ]);
      }
    }

    getLinks();
  }, []);

  return links;
}
