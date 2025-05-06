import { useState } from "react";
import { CheckboxWithLabel } from "~/components/ui/checkbox";

export interface WordListEntry {
  word: string;
  found: boolean;
  isBonus: boolean;
}

export interface WordListProps {
  words: WordListEntry[];
  showSomeLettersUnlocked: boolean;
}

export function WordList({ words, showSomeLettersUnlocked }: WordListProps) {
  const [sortWordsAlphabetically, setSortWordsAlphabetically] = useState(false);
  const [showSomeLetters, setShowSomeLetters] = useState(false);

  const wordComparator = (wordA: WordListEntry, wordB: WordListEntry): number => {
    if (!sortWordsAlphabetically && wordA.found !== wordB.found) {
      // Put all found words at the front and all missing words at the end
      return wordA.found ? -1 : 1;
    }
    return wordA.word.localeCompare(wordB.word);
  };

  const allRequiredWordsGroupedByLength = words
    .filter((word) => !word.isBonus)
    .reduce((groups: Record<number, WordListEntry[]>, word) => {
      groups[word.word.length] = groups[word.word.length] ?? [];
      groups[word.word.length].push(word);
      return groups;
    }, {});

  const bonusWords = words.filter((word) => word.isBonus && word.found).sort(wordComparator);

  const getNumWordsLeftString = (words: WordListEntry[]) => {
    const numWordsLeft = words.length - words.filter((word) => word.found).length;
    return numWordsLeft === 0 ? null : `+${numWordsLeft} word${numWordsLeft === 1 ? "" : "s"} left`;
  };

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold">Hints</h1>

      <CheckboxWithLabel
        checked={sortWordsAlphabetically}
        onCheckedChange={(checked) => setSortWordsAlphabetically(checked === true)}
        label="Sort words alphabetically"
        description="Sorts the word list alphabetically, revealing where all the missing words are relative
                     to the ones you've already found."
      />

      <CheckboxWithLabel
        checked={showSomeLetters}
        onCheckedChange={(checked) => setShowSomeLetters(checked === true)}
        hidden={!showSomeLettersUnlocked}
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
              <span className="empty:hidden text-sm text-muted-foreground">
                {getNumWordsLeftString(words)}
              </span>
            )}
          </div>
        ))}

      <div className="flex flex-col gap-4">
        <h2>Bonus words</h2>
        <div className="flex flex-wrap gap-x-3 gap-y-1 empty:hidden">
          {bonusWords.map(({ word }) => (
            <span key={word} className="text-lg">
              {word}
            </span>
          ))}
        </div>
        {bonusWords.length === 0 && (
          <span className="text-sm text-muted-foreground">None found so far</span>
        )}
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
