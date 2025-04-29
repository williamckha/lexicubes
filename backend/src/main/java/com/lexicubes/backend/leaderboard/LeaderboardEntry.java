package com.lexicubes.backend.leaderboard;

public record LeaderboardEntry(Long userId,
                               String userName,
                               int numPoints,
                               int numRequiredWordsFound,
                               int numBonusWordsFound) {
}
