package com.lexicubes.backend.score;

public record ScoreResponse(Long userId,
                            int numPoints,
                            int numRequiredWordsFound,
                            int numBonusWordsFound) {
}
