import { usePuzzleFoundWords } from "~/game/puzzle-store";
import { useState } from "react";
import { Checkbox } from "~/components/ui/checkbox";
import type { Puzzle } from "~/game/puzzle-queries";

interface WordListProps {
  puzzle: Puzzle;
}

export function WordList({ puzzle }: WordListProps) {
  const foundWords = usePuzzleFoundWords(puzzle.id);

  const [sortWordsAlphabetically, setSortWordsAlphabetically] = useState(false);
  const [showSomeLetters, setShowSomeLetters] = useState(false);

  const allWordsGroupedByLength = puzzle.solutions
    .map((sol) => {
      return {
        word: sol.word,
        found: foundWords.includes(sol.word),
        isBonus: sol.isBonus,
      };
    })
    .filter((word) => !word.isBonus || word.found)
    .reduce((groups: Record<number, Word[]>, word) => {
      groups[word.word.length] = groups[word.word.length] ?? [];
      groups[word.word.length].push(word);
      return groups;
    }, {});

  const wordComparator = (wordA: Word, wordB: Word): number => {
    if (!sortWordsAlphabetically && wordA.found !== wordB.found) {
      // Put all found words at the front and all missing words at the end
      return wordA.found ? -1 : 1;
    }
    return wordA.word.localeCompare(wordB.word);
  };

  return (
    <div className="flex flex-col gap-4">
      <h1 className="text-xl font-semibold">Hints</h1>
      <label className="flex items-center gap-2">
        <Checkbox
          checked={sortWordsAlphabetically}
          onCheckedChange={(e) => setSortWordsAlphabetically(e === true)}
        />
        <span>Sort words alphabetically</span>
      </label>
      <label className="flex items-center gap-2">
        <Checkbox
          checked={showSomeLetters}
          onCheckedChange={(e) => setShowSomeLetters(e === true)}
        />
        <span>Show some letters</span>
      </label>
      {Object.entries(allWordsGroupedByLength)
        .sort()
        .map(([numLetters, words]) => (
          <div key={numLetters} className="flex flex-col gap-2">
            <h1 className="text-xl font-semibold">{`${numLetters} letters`}</h1>
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
              <span className="text-sm text-muted-foreground">
                {getNumberOfWordsLeftString(words)}
              </span>
            )}
          </div>
        ))}
    </div>
  );
}

interface Word {
  word: string;
  found: boolean;
  isBonus: boolean;
}

function obscureWord(word: string, hideAllLetters: boolean): string {
  const numStartingLettersToShow = hideAllLetters
    ? 0
    : word.length > 12
      ? 3
      : word.length > 5
        ? 2
        : 1;
  const numEndingLettersToShow = hideAllLetters
    ? 0
    : word.length === 7
      ? 1
      : word.length === 8
        ? 2
        : word.length >= 10
          ? 3
          : 0;
  const numLettersToHide = word.length - numStartingLettersToShow - numEndingLettersToShow;
  return (
    word.substring(0, numStartingLettersToShow) +
    "-".repeat(numLettersToHide) +
    word.substring(word.length - numEndingLettersToShow)
  );
}

function getNumberOfWordsLeftString(words: Word[]): string {
  const numWordsLeft = words.length - words.filter((word) => word.found).length;
  return `+${numWordsLeft} word${numWordsLeft === 1 ? "" : "s"} left`;
}
