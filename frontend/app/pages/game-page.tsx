import { Game } from "~/game/game";
import { type PuzzleQueryId, usePuzzleQuery } from "~/game/puzzle-queries";
import { BiHelpCircle } from "react-icons/bi";
import { IoShareSocialSharp } from "react-icons/io5";
import { GiHamburgerMenu } from "react-icons/gi";
import { MdLeaderboard } from "react-icons/md";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "~/components/ui/dialog";
import { AboutPage } from "~/pages/about-page";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";
import React, { useState } from "react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "~/components/ui/dropdown-menu";
import { usePuzzleActions } from "~/game/puzzle-store";
import { LoadingSpinner } from "~/components/ui/loading-spinner";
import { UserDialog } from "~/user/login-dialog";
import { useUserQuery } from "~/user/user-queries";

interface GamePageProps {
  puzzleId: PuzzleQueryId;
}

export function GamePage({ puzzleId }: GamePageProps) {
  const puzzleQuery = usePuzzleQuery(puzzleId);
  const userQuery = useUserQuery();

  const puzzleActions = usePuzzleActions();

  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [isUserDialogOpen, setIsUserDialogOpen] = useState(false);

  if (puzzleQuery.isPending) {
    return (
      <div className="flex flex-1 h-full justify-center items-center">
        <LoadingSpinner />
      </div>
    );
  }

  if (puzzleQuery.isError) {
    return (
      <div className="flex flex-1 flex-col gap-2 h-full justify-center items-center">
        <span className="font-mono font-bold">Error</span>
        <span className="font-mono max-w-200">{puzzleQuery.error.message}</span>
      </div>
    );
  }

  return (
    <div className="flex flex-1 flex-col h-full">
      <header className="flex justify-center bg-accent-secondary border-b-2 shadow-lg">
        <div className="flex flex-1 justify-center items-center max-w-240 mx-4">
          <div className="flex flex-1 justify-start items-center">
            <DropdownMenu open={isMenuOpen} onOpenChange={setIsMenuOpen}>
              <DropdownMenuTrigger>
                <HeaderButton>
                  <GiHamburgerMenu size={28} />
                </HeaderButton>
              </DropdownMenuTrigger>
              <DropdownMenuContent>
                <DropdownMenuItem onClick={() => puzzleActions.resetPuzzleState(puzzleQuery.data)}>
                  Reset puzzle
                </DropdownMenuItem>
                <DropdownMenuItem
                  onClick={() => {
                    setIsUserDialogOpen(true);
                    setIsMenuOpen(false);
                  }}
                >
                  {userQuery.isPending || userQuery.isError || userQuery.data === null
                    ? "Log in"
                    : "Account"}
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
            <HeaderButton>
              <MdLeaderboard size={28} />
            </HeaderButton>
          </div>
          <h1 className="flex-1 text-xl font-semibold text-center">Lexicubes</h1>
          <div className="flex flex-1 justify-end items-center">
            <Dialog>
              <DialogTrigger>
                <HeaderButton>
                  <BiHelpCircle size={28} />
                </HeaderButton>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>About</DialogTitle>
                  <VisuallyHidden>
                    <DialogDescription>About the game</DialogDescription>
                  </VisuallyHidden>
                </DialogHeader>
                <AboutPage />
              </DialogContent>
            </Dialog>
            <HeaderButton>
              <IoShareSocialSharp size={28} />
            </HeaderButton>
          </div>
        </div>
      </header>

      <main className="flex flex-1 justify-center min-h-0 ">
        <div className="flex flex-1 max-w-235 mx-8 gap-8">
          <Game puzzle={puzzleQuery.data} />
        </div>
      </main>

      <UserDialog open={isUserDialogOpen} onOpenChange={setIsUserDialogOpen} />
    </div>
  );
}

function HeaderButton({ children }: React.PropsWithChildren) {
  return <div className="hover:bg-white/30 px-2 py-3 cursor-pointer">{children}</div>;
}
