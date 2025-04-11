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
import React from "react";

interface GamePageProps {
  puzzleId: PuzzleQueryId;
}

export function GamePage({ puzzleId }: GamePageProps) {
  const { data: puzzle, error, isPending, isError } = usePuzzleQuery(puzzleId);

  if (isPending) {
    return <span>Loading...</span>;
  }

  if (isError) {
    return <span>Error: {error.message}</span>;
  }

  return (
    <div className="flex flex-1 justify-center items-stretch flex-col">
      <header className="flex justify-center border-b-1 shadow-lg">
        <div className="flex flex-1 justify-center items-center max-w-200 mx-4">
          <div className="flex flex-1 justify-start items-center">
            <HeaderButton>
              <GiHamburgerMenu size={28} />
            </HeaderButton>
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
                  <DialogTitle>Lexicubes</DialogTitle>
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
      <main className="flex flex-1 justify-center items-center">
        <Game puzzle={puzzle} />
      </main>
    </div>
  );
}

function HeaderButton({ children }: React.PropsWithChildren) {
  return <div className="hover:bg-muted px-2 py-3 cursor-pointer">{children}</div>;
}
