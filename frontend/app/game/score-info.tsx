import { IoStar } from "react-icons/io5";
import { PERK_SCORES } from "~/game/game-constants";

export interface ScoreInfoProps {
  score: number;
  requiredWordsFound: number;
  requiredWordsTotal: number;
  bonusWordsFound: number;
  onWordCountClick?: () => void;
}

export function ScoreInfo({
  score,
  requiredWordsFound,
  requiredWordsTotal,
  bonusWordsFound,
  onWordCountClick,
}: ScoreInfoProps) {
  return (
    <div className="flex flex-col items-center gap-1 short:gap-2 tall:gap-4">
      <div
        className="flex flex-col items-center gap-2 select-none cursor-pointer"
        onClick={onWordCountClick}
      >
        <span className="text-xl short:text-2xl tall:text-3xl font-semibold">
          {requiredWordsFound} / {requiredWordsTotal} words
        </span>
        {bonusWordsFound > 0 && (
          <span className="text-sm text-muted-foreground">+{bonusWordsFound} bonus words</span>
        )}
      </div>
      <ScoreBar score={score} />
    </div>
  );
}

interface ScoreBarProps {
  score: number;
}

function ScoreBar({ score }: ScoreBarProps) {
  const starScore = Math.min(
    ...Object.values(PERK_SCORES).filter((perkScore) => perkScore > score),
  );

  return (
    <div className="w-80 h-6 border-2 relative">
      {starScore !== Infinity && (
        <IoStar
          size={16}
          className="absolute top-[2px] transition-[left] duration-500 z-10"
          style={{ left: `clamp(0px, calc(${starScore}% - 8px), calc(100% - 16px))` }}
        />
      )}
      <div
        className="bg-accent-secondary box-content border-r-2 h-full transition-[width] duration-500 z-0"
        style={{ width: `${Math.max(score, 1)}%` }}
      />
    </div>
  );
}
