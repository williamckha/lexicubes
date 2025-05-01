import {
  usePuzzleBonusWordsFound,
  usePuzzleRequiredWordsFound,
  usePuzzleScore,
} from "~/game/puzzle-store";
import { useState } from "react";
import { CheckboxWithLabel } from "~/components/ui/checkbox";
import type { Puzzle } from "~/game/puzzle-queries";
import { PERK_SCORES } from "~/game/game-constants";

export interface WordListProps {
  puzzle: Puzzle;
}

export function WordList({ puzzle }: WordListProps) {
  const requiredWordsFound = usePuzzleRequiredWordsFound(puzzle.id);
  const bonusWordsFound = usePuzzleBonusWordsFound(puzzle.id);
  const puzzleScore = usePuzzleScore(puzzle.id);

  const [sortWordsAlphabetically, setSortWordsAlphabetically] = useState(false);
  const [showSomeLetters, setShowSomeLetters] = useState(false);

  const allWords = puzzle.solutions.map((sol) => {
    return {
      word: sol.word,
      found: requiredWordsFound.includes(sol.word) || bonusWordsFound.includes(sol.word),
      isBonus: sol.isBonus,
    };
  });

  type WordListEntry = (typeof allWords)[number];

  const wordComparator = (wordA: WordListEntry, wordB: WordListEntry): number => {
    if (!sortWordsAlphabetically && wordA.found !== wordB.found) {
      // Put all found words at the front and all missing words at the end
      return wordA.found ? -1 : 1;
    }
    return wordA.word.localeCompare(wordB.word);
  };

  const allRequiredWordsGroupedByLength = allWords
    .filter((word) => !word.isBonus)
    .reduce((groups: Record<number, WordListEntry[]>, word) => {
      groups[word.word.length] = groups[word.word.length] ?? [];
      groups[word.word.length].push(word);
      return groups;
    }, {});

  const allBonusWords = allWords.filter((word) => word.isBonus).sort(wordComparator);

  const getNumWordsLeftString = (words: WordListEntry[]) => {
    const numWordsLeft = words.length - words.filter((word) => word.found).length;
    return `+${numWordsLeft} word${numWordsLeft === 1 ? "" : "s"} left`;
  };

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold">Hints</h1>

      <CheckboxWithLabel
        checked={sortWordsAlphabetically}
        onCheckedChange={(e) => setSortWordsAlphabetically(e === true)}
        label="Sort words alphabetically"
        description="Shows all missing words in the word list, sorted alphabetically alongside the words
                     you've already found, so you know where to look next."
      />

      <CheckboxWithLabel
        checked={showSomeLetters}
        onCheckedChange={(e) => setShowSomeLetters(e === true)}
        hidden={puzzleScore < PERK_SCORES.SHOW_SOME_LETTERS}
        label="Show some letters"
        description="Show some letters of the missing words. Shorter words show only the first letter, while
                     longer words may reveal the first two and occasionally some ending letters."
      />

      {Object.entries(allRequiredWordsGroupedByLength)
        .sort()
        .map(([numLetters, words]) => (
          <div key={numLetters} className="flex flex-col gap-4">
            <h2>{`${numLetters} letters`}</h2>
            <div className="flex flex-wrap gap-x-3 gap-y-1 empty:hidden">
              {words
                .filter((word) => sortWordsAlphabetically || showSomeLetters || word.found)
                .sort(wordComparator)
                .map(({ word, found }) => (
                  <span key={word} className="text-lg">
                    {found ? word : obscureWord(word, !showSomeLetters)}
                  </span>
                ))}
            </div>
            {!sortWordsAlphabetically && !showSomeLetters && (
              <span className="text-sm text-muted-foreground">{getNumWordsLeftString(words)}</span>
            )}
          </div>
        ))}

      <div className="flex flex-col gap-4">
        <h2>Bonus words</h2>
        <div className="flex flex-wrap gap-x-3 gap-y-1 empty:hidden">
          {allBonusWords
            .filter(({ found }) => found)
            .map(({ word }) => (
              <span key={word} className="text-lg">
                {word}
              </span>
            ))}
        </div>
        <span className="text-sm text-muted-foreground">
          {getNumWordsLeftString(allBonusWords)}
        </span>
      </div>
    </div>
  );
}

function obscureWord(word: string, hideAllLetters: boolean) {
  if (hideAllLetters) {
    return "-".repeat(word.length);
  }

  let numStartingLettersToShow: number;
  if (word.length > 12) {
    numStartingLettersToShow = 3;
  } else if (word.length > 5) {
    numStartingLettersToShow = 2;
  } else {
    numStartingLettersToShow = 1;
  }

  let numEndingLettersToShow: number;
  if (word.length >= 10) {
    numEndingLettersToShow = 3;
  } else if (word.length === 8) {
    numEndingLettersToShow = 2;
  } else if (word.length === 7) {
    numEndingLettersToShow = 1;
  } else {
    numEndingLettersToShow = 0;
  }

  const numLettersToHide = word.length - numStartingLettersToShow - numEndingLettersToShow;

  return (
    word.substring(0, numStartingLettersToShow) +
    "-".repeat(numLettersToHide) +
    word.substring(word.length - numEndingLettersToShow)
  );
}
