import { MIN_WORD_LENGTH, usePuzzleCurrentWord, usePuzzleWordInfo } from "~/game/puzzle-store";

const WORD_INFO_MESSAGES = {
  tooShort: "Too short",
  notInList: "Not in list",
  alreadyFound: "Already found",
};

export function WordInfo() {
  const currentWord = usePuzzleCurrentWord();
  const wordInfo = usePuzzleWordInfo();

  const isWordInfoAvailable = currentWord.length === 0 && wordInfo !== null;

  return (
    <div className="h-20 flex justify-center items-center">
      {currentWord.length > 0 && (
        <span
          className={`text-3xl sm:text-4xl font-bold 
                      ${currentWord.length < MIN_WORD_LENGTH ? "opacity-50" : ""}`}
        >
          {currentWord.toUpperCase()}
        </span>
      )}
      {isWordInfoAvailable && wordInfo.status === "success" && (
        <span className="text-3xl sm:text-4xl font-bold text-amber-600 animate-word-info-fade-in-out">
          {wordInfo.isBonus ? "Bonus word found!" : `+${wordInfo.numPoints} points`}
        </span>
      )}
      {isWordInfoAvailable && wordInfo.status !== "success" && (
        <span className="text-3xl sm:text-4xl font-bold animate-word-info-fade-in-out">
          {WORD_INFO_MESSAGES[wordInfo.status]}
        </span>
      )}
      {isWordInfoAvailable &&
        (wordInfo.status === "success" || wordInfo.status === "alreadyFound") && (
          <div
            className="absolute text-2xl sm:text-3xl font-bold p-2 bg-card text-center border-2
                       animate-word-definition-fade-in"
          >
            {wordInfo.word.toLowerCase()}
          </div>
        )}
    </div>
  );
}
