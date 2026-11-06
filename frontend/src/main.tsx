import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { Authenticator } from "@aws-amplify/ui-react";
import { Toaster } from "sonner";
import { ThemeProvider } from "./components/common/ThemeProvider.tsx";
import { TooltipProvider } from "./components/ui/tooltip.tsx";
import Authenticate from "./components/auth/Authenticate.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ThemeProvider defaultTheme="dark" storageKey="vite-ui-theme">
      <Authenticator.Provider>
        <Authenticate>
          <TooltipProvider>
            <App />
            <Toaster richColors />
          </TooltipProvider>
        </Authenticate>
      </Authenticator.Provider>
    </ThemeProvider>
  </StrictMode>
);
