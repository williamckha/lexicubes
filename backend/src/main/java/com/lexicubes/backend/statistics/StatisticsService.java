package com.lexicubes.backend.statistics;

import com.lexicubes.backend.score.ScoreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

    private final ScoreRepository scoreRepository;

    public StatisticsService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "globalStatistics")
    public GlobalStatistics getGlobalStatistics(Long puzzleId) {
        final int averageNumPoints = scoreRepository.getAverageNumPointsByPuzzleId(puzzleId);
        final int averageNumBonusWordsFound = scoreRepository.getAverageNumBonusWordsFoundByPuzzleId(puzzleId);
        return new GlobalStatistics(averageNumPoints, averageNumBonusWordsFound);
    }
}
