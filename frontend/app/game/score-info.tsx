import { usePuzzleFoundWords } from "~/game/puzzle-store";
import type { Puzzle } from "~/game/puzzle-queries";

interface ScoreInfoProps {
  puzzle: Puzzle;
  onWordCountClick?: () => void;
}

export function ScoreInfo({ puzzle, onWordCountClick }: ScoreInfoProps) {
  const foundWords = usePuzzleFoundWords(puzzle.id);

  const nonBonusWords = puzzle.solutions.filter((sol) => !sol.isBonus);

  const [foundNonBonusWords, foundBonusWords] = foundWords.reduce(
    (groups: string[][], word: string) => {
      if (nonBonusWords.find((sol) => sol.word === word)) {
        groups[0].push(word);
      } else {
        groups[1].push(word);
      }
      return groups;
    },
    [[], []],
  );

  return (
    <div
      className="flex flex-col items-center gap-2 select-none cursor-pointer"
      onClick={onWordCountClick}
    >
      <span className="text-3xl font-semibold">
        <span>{foundNonBonusWords.length}</span> / <span>{nonBonusWords.length}</span> words
      </span>
      {foundBonusWords.length > 0 && (
        <span className="text-sm text-gray-400">
          +<span>{foundBonusWords.length}</span> bonus words
        </span>
      )}
    </div>
  );
}
