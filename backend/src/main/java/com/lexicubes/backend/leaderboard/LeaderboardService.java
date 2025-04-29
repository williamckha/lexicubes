package com.lexicubes.backend.leaderboard;

import com.lexicubes.backend.common.PageResponse;
import com.lexicubes.backend.score.ScoreRepository;
import com.lexicubes.backend.score.ScoreWithUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LeaderboardService {

    private final ScoreRepository scoreRepository;

    public LeaderboardService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<LeaderboardEntry> getLeaderboardEntriesForPuzzle(Long puzzleId,
                                                                         LeaderboardPageRequest pageRequest) {

        final List<ScoreWithUser> scoresWithUsers = switch (pageRequest.getSort()) {
            case NUM_POINTS -> scoreRepository.findScoresWithUsersByPuzzleIdOrderByNumPoints(
                    puzzleId, pageRequest.getOffset(), pageRequest.getPageSize());

            case NUM_REQUIRED_WORDS_FOUND -> scoreRepository.findScoresWithUsersByPuzzleIdOrderByNumRequiredWordsFound(
                    puzzleId, pageRequest.getOffset(), pageRequest.getPageSize());

            case NUM_BONUS_WORDS_FOUND -> scoreRepository.findScoresWithUsersByPuzzleIdOrderByNumBonusWordsFound(
                    puzzleId, pageRequest.getOffset(), pageRequest.getPageSize());
        };

        final List<LeaderboardEntry> leaderboardEntries = scoresWithUsers.stream()
                .map(LeaderboardService::mapScoreWithUserToLeaderboardEntry)
                .toList();

        final long totalScoresCount = scoreRepository.countScoresByPuzzleId(puzzleId);
        final long totalPageCount = Math.ceilDiv(totalScoresCount, pageRequest.getPageSize());

        return new PageResponse<>(
                leaderboardEntries,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                totalPageCount);
    }

    private static LeaderboardEntry mapScoreWithUserToLeaderboardEntry(ScoreWithUser scoreWithUser) {
        return new LeaderboardEntry(
                scoreWithUser.userId(),
                scoreWithUser.userName(),
                scoreWithUser.numPoints(),
                scoreWithUser.numRequiredWordsFound(),
                scoreWithUser.numBonusWordsFound());
    }
}
