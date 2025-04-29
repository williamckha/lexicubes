package com.lexicubes.backend.score;

public record ScoreWithUser(Long scoreId,
                            Long userId,
                            String userName,
                            int numPoints,
                            int numRequiredWordsFound,
                            int numBonusWordsFound) {
}
