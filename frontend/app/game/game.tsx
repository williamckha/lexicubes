import { WordInfo } from "~/game/word-info";
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
import {
  usePuzzleActions,
  usePuzzleBonusWordsFound,
  usePuzzleRequiredWordsFound,
  usePuzzleScore,
} from "~/game/puzzle-store";
import { useWindowSize } from "@uidotdev/usehooks";
import { PuzzleCubes } from "~/game/puzzle-cubes";
import { PERK_SCORES } from "~/game/game-constants";

export interface GameProps {
  puzzle: Puzzle;
}

export function Game({ puzzle }: GameProps) {
  const { initializePuzzleStateIfAbsent } = usePuzzleActions();
  initializePuzzleStateIfAbsent(puzzle);

  const puzzleScore = usePuzzleScore(puzzle.id);
  const requiredWordsFound = usePuzzleRequiredWordsFound(puzzle.id);
  const bonusWordsFound = usePuzzleBonusWordsFound(puzzle.id);
  const requiredWords = puzzle.solutions.filter((sol) => !sol.isBonus);

  const { isWordListDialogVisible, isWordListPaneVisible, toggleWordListVisibility } =
    useWordListVisibility();

  const wordList = (
    <WordList
      words={puzzle.solutions.map((sol) => {
        return {
          word: sol.word,
          found: requiredWordsFound.includes(sol.word) || bonusWordsFound.includes(sol.word),
          isBonus: sol.isBonus,
        };
      })}
      showSomeLettersUnlocked={puzzleScore >= PERK_SCORES.SHOW_SOME_LETTERS}
    />
  );

  return (
    <>
      {isWordListPaneVisible && (
        <div className="flex flex-1 flex-col overflow-y-scroll my-10 pr-8">{wordList}</div>
      )}

      <Dialog open={isWordListDialogVisible} onOpenChange={toggleWordListVisibility}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Words</DialogTitle>
            <VisuallyHidden>
              <DialogDescription>Word list for the current puzzle</DialogDescription>
            </VisuallyHidden>
          </DialogHeader>
          {wordList}
        </DialogContent>
      </Dialog>

      <div className="flex flex-1 self-center items-center flex-col gap-2 short:gap-3 tall:gap-4">
        <ScoreInfo
          score={puzzleScore}
          requiredWordsFound={requiredWordsFound.length}
          requiredWordsTotal={requiredWords.length}
          bonusWordsFound={bonusWordsFound.length}
          onWordCountClick={toggleWordListVisibility}
        />
        <WordInfo />
        <PuzzleCubes puzzle={puzzle} />
      </div>
    </>
  );
}

function useWordListVisibility() {
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

  return { isWordListDialogVisible, isWordListPaneVisible, toggleWordListVisibility };
}
