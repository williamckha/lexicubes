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
import { useWindowSize } from "@uidotdev/usehooks";

interface GameProps {
  puzzle: Puzzle;
}

export function Game({ puzzle }: GameProps) {
  const { initializePuzzleStateIfAbsent } = usePuzzleActions();
  initializePuzzleStateIfAbsent(puzzle);

  const [isWordListDialogVisible, isWordListPaneVisible, toggleWordListVisibility] =
    useWordListVisibility();

  return (
    <>
      {isWordListPaneVisible && (
        <div className="flex flex-1 flex-col overflow-y-scroll my-10">
          <WordList puzzle={puzzle} />
        </div>
      )}
      <div className="flex flex-1 self-center items-center flex-col gap-2 short:gap-3 tall:gap-4">
        <ScoreInfo puzzle={puzzle} onWordCountClick={toggleWordListVisibility} />
        <WordInfo />
        <Cubes puzzle={puzzle} />
        <Dialog open={isWordListDialogVisible} onOpenChange={toggleWordListVisibility}>
          <DialogContent>
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
    </>
  );
}

function useWordListVisibility(): [boolean, boolean, () => void] {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isPaneOpen, setIsPaneOpen] = useState(true);

  const windowSize = useWindowSize();
  const windowWidth = windowSize.width ?? 0;
  const isPaneEnabled = windowWidth >= 700;

  if (isPaneEnabled && isDialogOpen) {
    setIsDialogOpen(false);
  }

  const toggleWordListVisibility = () => {
    if (isPaneEnabled) {
      setIsPaneOpen(!isPaneOpen);
    } else {
      setIsDialogOpen(!isDialogOpen);
    }
  };

  const isWordListDialogVisible = isDialogOpen && !isPaneEnabled;
  const isWordListPaneVisible = isPaneOpen && isPaneEnabled;

  return [isWordListDialogVisible, isWordListPaneVisible, toggleWordListVisibility];
}
