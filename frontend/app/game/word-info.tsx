import { usePuzzleCurrentWord, usePuzzleWordInfo } from "~/game/puzzle-store";
import { MIN_WORD_LENGTH } from "~/game/game-constants";

const WORD_INFO_MESSAGES = {
  tooShort: "Too short",
  notInList: "Not in list",
  alreadyFound: "Already found",
};

export function WordInfo() {
  const currentWord = usePuzzleCurrentWord();
  const wordInfo = usePuzzleWordInfo();

  const showWordInfo = currentWord.length === 0 && wordInfo !== null;

  return (
    <div className="flex justify-center items-center h-12 short:h-16 tall:h-20">
      {currentWord.length > 0 && (
        <span
          className={`text-xl short:text-3xl tall:text-4xl font-bold 
                      ${currentWord.length < MIN_WORD_LENGTH ? "opacity-50" : ""}`}
        >
          {currentWord.toUpperCase()}
        </span>
      )}

      {showWordInfo && wordInfo.status === "success" && (
        <span className="text-xl short:text-3xl tall:text-4xl font-bold text-amber-600 animate-word-info-fade-in-out">
          {wordInfo.isBonus ? "Bonus word found!" : `+${wordInfo.numPoints} points`}
        </span>
      )}

      {showWordInfo && wordInfo.status !== "success" && (
        <span className="text-xl short:text-3xl tall:text-4xl font-bold animate-word-info-fade-in-out">
          {WORD_INFO_MESSAGES[wordInfo.status]}
        </span>
      )}

      {showWordInfo && (wordInfo.status === "success" || wordInfo.status === "alreadyFound") && (
        <div
          className="absolute text-lg short:text-2xl tall:text-3xl p-1 short:p-2
                       font-bold bg-card text-center border-2
                       animate-word-definition-fade-in"
        >
          {wordInfo.word.toLowerCase()}
        </div>
      )}
    </div>
  );
}
