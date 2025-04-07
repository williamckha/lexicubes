package com.lexicubes.backend.puzzle;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PuzzleRepository extends CrudRepository<Puzzle, Long> {

    @Override
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = "puzzle",
                    key = "#entity.id"),
            @CacheEvict(
                    cacheNames = "dailyPuzzleByPublishedDate",
                    key = "#entity.publishedDate",
                    condition = "#entity.daily")
    })
    <S extends Puzzle> @NotNull S save(@NotNull S entity);

    @Override
    @Cacheable(cacheNames = "puzzle")
    @NotNull Optional<Puzzle> findById(@NotNull Long id);

    @Query("SELECT * FROM puzzles WHERE is_daily = TRUE AND published_date = :publishedDate LIMIT 1")
    @Cacheable(cacheNames = "dailyPuzzleByPublishedDate")
    @NotNull Optional<Puzzle> findDailyByPublishedDate(@NotNull LocalDate publishedDate);
}
