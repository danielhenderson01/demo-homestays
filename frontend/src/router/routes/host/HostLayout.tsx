import { Outlet, useNavigation } from "react-router";
import Loading from "@/components/common/Loading";
import Navbar from "@/components/navigation/Navbar";

export default function HostLayout() {
  const navigation = useNavigation();

  return (
    <>
      <Navbar />
      <main className="container mx-auto w-full max-w-7xl flex-1 px-4 py-(--header-height)">
        {navigation.state === "loading" ? <Loading /> : <Outlet />}
      </main>
    </>
  );
}
