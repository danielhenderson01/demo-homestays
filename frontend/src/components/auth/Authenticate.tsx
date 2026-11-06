import "@aws-amplify/ui-react/styles.css";
import { Amplify } from "aws-amplify";
import {
  Authenticator,
  defaultDarkModeOverride,
  Heading,
  Radio,
  RadioGroupField,
  ThemeProvider,
  useAuthenticator,
  View,
} from "@aws-amplify/ui-react";

const config = {
  userPoolId: import.meta.env.VITE_AWS_COGNITO_USER_POOL_ID,
  userPoolClientId: import.meta.env.VITE_AWS_COGNITO_USER_POOL_CLIENT_ID,
};

const userPoolEndpoint = import.meta.env.VITE_AWS_COGNITO_USER_POOL_ENDPOINT;

Amplify.configure({
  Auth: {
    Cognito: userPoolEndpoint ? { ...config, userPoolEndpoint } : config,
  },
});

export default function Authenticate({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ThemeProvider
      theme={{
        name: "my-theme",
        overrides: [defaultDarkModeOverride],
      }}
      colorMode="system"
    >
      <Authenticator
        components={{
          Header() {
            return (
              <View className="mt-16 mb-4">
                <Heading
                  level={3}
                  className="text-2xl! font-semibold! text-foreground!"
                >
                  Demo Homestays
                </Heading>
                <p className="mt-2 text-muted-foreground">
                  <span className="font-semibold">Welcome!</span> Please sign in
                  to continue
                </p>
              </View>
            );
          },

          SignIn: {
            Footer() {
              const { toSignUp } = useAuthenticator();
              return (
                <View className="mt-4 text-center">
                  <p className="text-muted-foreground">
                    Don&apos;t have an account?{" "}
                    <button
                      onClick={toSignUp}
                      className="border-none bg-transparent p-0 text-blue-500 hover:underline"
                    >
                      Sign up here
                    </button>
                  </p>
                </View>
              );
            },
          },

          SignUp: {
            FormFields() {
              const { validationErrors } = useAuthenticator();
              return (
                <>
                  <Authenticator.SignUp.FormFields />
                  <RadioGroupField
                    legend="Role"
                    name="custom:role"
                    errorMessage={validationErrors?.["custom:role"]}
                    hasError={!!validationErrors?.["custom:role"]}
                    isRequired
                  >
                    <Radio value="GUEST">Guest</Radio>
                    <Radio value="HOST">Host</Radio>
                  </RadioGroupField>
                </>
              );
            },

            Footer() {
              const { toSignIn } = useAuthenticator();
              return (
                <View className="mt-4 text-center">
                  <p className="text-muted-foreground">
                    Already have an account?{" "}
                    <button
                      onClick={toSignIn}
                      className="border-none bg-transparent p-0 text-blue-500 hover:underline"
                    >
                      Sign in here
                    </button>
                  </p>
                </View>
              );
            },
          },
        }}
        formFields={{
          signIn: {
            username: {
              placeholder: "Enter your email",
              label: "Email",
              isRequired: true,
            },
            password: {
              placeholder: "Enter your password",
              label: "Password",
              isRequired: true,
            },
          },
          signUp: {
            username: {
              order: 1,
              placeholder: "Enter your email address",
              label: "Email",
              isRequired: true,
            },
            name: {
              order: 2,
              placeholder: "Enter your name",
              label: "Name",
              isRequired: true,
            },
            password: {
              order: 3,
              placeholder: "Create a password",
              label: "Password",
              isRequired: true,
            },
            confirm_password: {
              order: 4,
              placeholder: "Confirm your password",
              label: "Confirm Password",
              isRequired: true,
            },
          },
        }}
      >
        {() => <>{children}</>}
      </Authenticator>
    </ThemeProvider>
  );
}
