import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "~/components/ui/dialog";
import React from "react";
import { Link } from "react-router";
import {
  AppleIcon,
  FacebookIcon,
  GitHubIcon,
  GoogleIcon,
  type SocialIconProps,
} from "./social-icons";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";
import { useLogoutMutation, useUserQuery } from "~/user/user-queries";

export function UserDialog({ ...props }: React.ComponentProps<typeof Dialog>) {
  const { data: user, isPending, isError } = useUserQuery();

  if (isPending) {
    return null;
  }

  if (isError || user === null) {
    return <LoginDialog {...props} />;
  }

  const { icon: providerIcon, name: providerName } = SOCIAL_PROVIDERS[user.provider];

  return (
    <Dialog {...props}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Account</DialogTitle>
          <VisuallyHidden>
            <DialogDescription>Account info</DialogDescription>
          </VisuallyHidden>
        </DialogHeader>
        <p>
          You're logged in with{" "}
          <span className="relative bottom-[1px] ml-[2px]">{providerIcon}</span>{" "}
          <span className="font-semibold">{providerName}</span> as{" "}
          <span className="font-semibold">{user.name}</span>.
        </p>
        <LogoutButton />
      </DialogContent>
    </Dialog>
  );
}

function LoginDialog({ ...props }: React.ComponentProps<typeof Dialog>) {
  return (
    <Dialog {...props}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Log in</DialogTitle>
          <VisuallyHidden>
            <DialogDescription>
              Log in to save your progress and share your score on the leaderboard.
            </DialogDescription>
          </VisuallyHidden>
        </DialogHeader>
        <p>Log in to save your progress and share your score on the leaderboard.</p>
        <GoogleLoginButton />
        <AppleLoginButton />
        <FacebookLoginButton />
        <GitHubLoginButton />
      </DialogContent>
    </Dialog>
  );
}

const SOCIAL_PROVIDERS = {
  google: { icon: <GoogleIcon size="1em" className="inline align-middle" />, name: "Google" },
  apple: { icon: <AppleIcon size="1em" className="inline align-middle" />, name: "Apple" },
  facebook: { icon: <FacebookIcon size="1em" className="inline align-middle" />, name: "Facebook" },
  github: { icon: <GitHubIcon size="1em" className="inline align-middle" />, name: "GitHub" },
};

interface SocialLoginButtonProps {
  icon: React.ReactElement<SocialIconProps>;
  provider: string;
  href: string;
}

function SocialLoginButton({ icon, provider, href }: SocialLoginButtonProps) {
  return (
    <Link to={href} reloadDocument>
      <div
        className="flex flex-row justify-center items-center gap-2 border-2 p-2 rounded-full font-medium
                      bg-background hover:bg-muted"
      >
        {icon} Continue with {provider}
      </div>
    </Link>
  );
}

function GoogleLoginButton() {
  return (
    <SocialLoginButton
      icon={SOCIAL_PROVIDERS.google.icon}
      provider={SOCIAL_PROVIDERS.google.name}
      href="/oauth2/authorization/google"
    />
  );
}

function AppleLoginButton() {
  return (
    <SocialLoginButton
      icon={SOCIAL_PROVIDERS.apple.icon}
      provider={SOCIAL_PROVIDERS.apple.name}
      href="/oauth2/authorization/apple"
    />
  );
}

function FacebookLoginButton() {
  return (
    <SocialLoginButton
      icon={SOCIAL_PROVIDERS.facebook.icon}
      provider={SOCIAL_PROVIDERS.facebook.name}
      href="/oauth2/authorization/facebook"
    />
  );
}

function GitHubLoginButton() {
  return (
    <SocialLoginButton
      icon={SOCIAL_PROVIDERS.github.icon}
      provider={SOCIAL_PROVIDERS.github.name}
      href="/oauth2/authorization/github"
    />
  );
}

function LogoutButton() {
  const { mutate: logout } = useLogoutMutation();
  return (
    <button
      onClick={() => logout()}
      className="flex flex-row justify-center items-center gap-2 border-2 p-2 rounded-full font-medium cursor-pointer
                 bg-background hover:bg-muted"
    >
      Log out
    </button>
  );
}
