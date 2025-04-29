package com.lexicubes.backend.score;

import com.lexicubes.backend.puzzle.Puzzle;
import com.lexicubes.backend.user.User;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("scores")
public class Score {

    @Id
    @Nullable
    private final Long id;

    @Column("puzzle_id")
    private final AggregateReference<Puzzle, Long> puzzle;

    @Column("user_id")
    private final AggregateReference<User, Long> user;

    private final int numPoints;

    private final int numRequiredWordsFound;

    private final int numBonusWordsFound;

    public static Score of(AggregateReference<Puzzle, Long> puzzle,
                           AggregateReference<User, Long> user,
                           int numPoints,
                           int numRequiredWordsFound,
                           int numBonusWordsFound) {

        return new Score(null, puzzle, user, numPoints, numRequiredWordsFound, numBonusWordsFound);
    }

    public Score(@Nullable Long id,
                 AggregateReference<Puzzle, Long> puzzle,
                 AggregateReference<User, Long> user,
                 int numPoints,
                 int numRequiredWordsFound,
                 int numBonusWordsFound) {

        this.id = id;
        this.puzzle = puzzle;
        this.user = user;
        this.numPoints = numPoints;
        this.numRequiredWordsFound = numRequiredWordsFound;
        this.numBonusWordsFound = numBonusWordsFound;
    }

    public @Nullable Long getId() {
        return id;
    }

    public AggregateReference<Puzzle, Long> getPuzzle() {
        return puzzle;
    }

    public AggregateReference<User, Long> getUser() {
        return user;
    }

    public int getNumPoints() {
        return numPoints;
    }

    public int getNumRequiredWordsFound() {
        return numRequiredWordsFound;
    }

    public int getNumBonusWordsFound() {
        return numBonusWordsFound;
    }
}
