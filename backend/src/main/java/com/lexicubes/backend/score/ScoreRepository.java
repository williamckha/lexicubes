package com.lexicubes.backend.score;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository extends CrudRepository<Score, Long> {

    @Query("SELECT * FROM scores WHERE puzzle_id = :puzzleId AND user_id = :userId")
    Optional<Score> findScoreByPuzzleIdAndUserId(Long puzzleId, Long userId);

    @Query(""" 
            SELECT s.id AS score_id,
                   u.id AS user_id,
                   u.name AS user_name,
                   s.num_points,
                   s.num_required_words_found,
                   s.num_bonus_words_found
            FROM scores AS s
            JOIN users AS u ON s.user_id = u.id
            WHERE s.puzzle_id = :puzzleId
            ORDER BY s.num_points DESC,
                     s.num_required_words_found DESC,
                     s.num_bonus_words_found DESC
            LIMIT :limit
            OFFSET :offset
            """)
    List<ScoreWithUser> findScoresWithUsersByPuzzleIdOrderByNumPoints(Long puzzleId, int offset, int limit);

    @Query(""" 
            SELECT s.id AS score_id,
                   u.id AS user_id,
                   u.name AS user_name,
                   s.num_points,
                   s.num_required_words_found,
                   s.num_bonus_words_found
            FROM scores AS s
            JOIN users AS u ON s.user_id = u.id
            WHERE s.puzzle_id = :puzzleId
            ORDER BY s.num_required_words_found DESC,
                     s.num_points DESC,
                     s.num_bonus_words_found DESC
            LIMIT :limit
            OFFSET :offset
            """)
    List<ScoreWithUser> findScoresWithUsersByPuzzleIdOrderByNumRequiredWordsFound(Long puzzleId, int offset, int limit);

    @Query(""" 
            SELECT s.id AS score_id,
                   u.id AS user_id,
                   u.name AS user_name,
                   s.num_points,
                   s.num_required_words_found,
                   s.num_bonus_words_found
            FROM scores AS s
            JOIN users AS u ON s.user_id = u.id
            WHERE s.puzzle_id = :puzzleId
            ORDER BY s.num_bonus_words_found DESC,
                     s.num_points DESC,
                     s.num_required_words_found DESC
            LIMIT :limit
            OFFSET :offset
            """)
    List<ScoreWithUser> findScoresWithUsersByPuzzleIdOrderByNumBonusWordsFound(Long puzzleId, int offset, int limit);

    @Query("SELECT COUNT(*) FROM scores WHERE puzzle_id = :puzzleId")
    long countScoresByPuzzleId(Long puzzleId);

    @Query("SELECT AVG(num_points) FROM scores WHERE puzzle_id = :puzzleId")
    int getAverageNumPointsByPuzzleId(Long puzzleId);

    @Query("SELECT AVG(num_bonus_words_found) FROM scores WHERE puzzle_id = :puzzleId")
    int getAverageNumBonusWordsFoundByPuzzleId(Long puzzleId);
}
