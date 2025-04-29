package com.lexicubes.backend.score;

import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ScoreService {

    public record SubmitScoreRequest(Long puzzleId,
                                     Long userId,
                                     int numPoints,
                                     int numRequiredWordsFound,
                                     int numBonusWordsFound) {}

    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public ScoreResponse getScoreForPuzzleAndUser(Long puzzleId, Long userId) {
        return scoreRepository
                .findScoreByPuzzleIdAndUserId(puzzleId, userId)
                .map(s -> new ScoreResponse(
                        s.getUser().getId(),
                        s.getNumPoints(),
                        s.getNumRequiredWordsFound(),
                        s.getNumBonusWordsFound()))
                .orElse(new ScoreResponse(userId, 0, 0, 0));
    }

    @Transactional
    public void submitScore(SubmitScoreRequest submitScoreRequest) {
        final Optional<Score> existingScore = scoreRepository.findScoreByPuzzleIdAndUserId(
                submitScoreRequest.puzzleId(),
                submitScoreRequest.userId());

        final Long scoreId = existingScore.map(Score::getId).orElse(null);

        final int numPoints = Math.max(
                existingScore.map(Score::getNumPoints).orElse(0),
                submitScoreRequest.numPoints());

        final int numRequiredWordsFound = Math.max(
                existingScore.map(Score::getNumRequiredWordsFound).orElse(0),
                submitScoreRequest.numRequiredWordsFound());

        final int numBonusWordsFound = Math.max(
                existingScore.map(Score::getNumBonusWordsFound).orElse(0),
                submitScoreRequest.numBonusWordsFound());

        final Score score = new Score(
                scoreId,
                AggregateReference.to(submitScoreRequest.puzzleId()),
                AggregateReference.to(submitScoreRequest.userId()),
                numPoints,
                numRequiredWordsFound,
                numBonusWordsFound);

        scoreRepository.save(score);
    }
}
