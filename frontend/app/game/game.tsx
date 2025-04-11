import { WordInfo } from "~/game/word-info";
import { Cubes } from "~/game/cubes";
import { ScoreInfo } from "~/game/score-info";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "~/components/ui/dialog";
import { WordList } from "~/game/word-list";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";
import { useState } from "react";
import type { Puzzle } from "~/game/puzzle-queries";
import { usePuzzleActions } from "~/game/puzzle-store";

interface GameProps {
  puzzle: Puzzle;
}

export function Game({ puzzle }: GameProps) {
  const [isWordListDialogOpen, setIsWordListDialogOpen] = useState(false);

  const { initializePuzzleStateIfAbsent } = usePuzzleActions();
  initializePuzzleStateIfAbsent(puzzle);

  return (
    <div className="flex justify-start items-center flex-col gap-3">
      <ScoreInfo puzzle={puzzle} onWordCountClick={() => setIsWordListDialogOpen(true)} />
      <WordInfo />
      <Cubes puzzle={puzzle} />
      <Dialog open={isWordListDialogOpen} onOpenChange={setIsWordListDialogOpen}>
        <DialogContent className="">
          <DialogHeader>
            <DialogTitle>Words</DialogTitle>
            <VisuallyHidden>
              <DialogDescription>Word list for the current puzzle</DialogDescription>
            </VisuallyHidden>
          </DialogHeader>
          <WordList puzzle={puzzle} />
        </DialogContent>
      </Dialog>
    </div>
  );
}
