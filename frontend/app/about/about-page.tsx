import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "~/components/ui/dialog";
import { VisuallyHidden } from "@radix-ui/react-visually-hidden";
import React from "react";
import { GameplayExample } from "~/about/gameplay-example";

export function AboutDialog({ ...props }: React.ComponentProps<typeof Dialog>) {
  return (
    <Dialog {...props}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>About</DialogTitle>
          <VisuallyHidden>
            <DialogDescription>About the game</DialogDescription>
          </VisuallyHidden>
        </DialogHeader>
        <AboutPage />
      </DialogContent>
    </Dialog>
  );
}

function AboutPage() {
  return (
    <div className="flex flex-col gap-4">
      <p>Lexicubes is a pseudo-3D word search game.</p>
      <p>A new puzzle is released every day at midnight.</p>
      <h2>How to play</h2>
      <p>
        Form words by <b>connecting letters</b> on neighbouring faces. Two faces are neighbours if
        they share an edge or corner.
      </p>
      <GameplayExample />
      <p>
        Cubes will be removed once you've found all the words using them, revealing more faces
        underneath. Only faces that are fully visible are in play. Try to clear all the cubes!
      </p>
      <p>
        Like in Scrabble, words that require a hyphen, apostrophe, or capital letter are not
        accepted.
      </p>
      <h2>What's a bonus word?</h2>
      <p>
        Depending on the order in which you find the <b>required words</b>—words that end up
        removing cubes once found—you might be able to form a word that not all players will
        encounter. These words are <b>bonus words</b> that are only possible to form if you remove
        cubes in a certain way. They don't count towards your score, but they do show up on the
        leaderboard.
      </p>
      <h2>Unlocking hints</h2>
      <p>
        As you find words and gain points, you'll unlock different hints that can help you complete
        the puzzle. Longer words are worth more points. The <b>star</b> in the progress bar shows
        you how far away you are from unlocking the next hint.
      </p>
    </div>
  );
}
