package com.lexicubes.backend.puzzle;

import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PuzzleRepository extends CrudRepository<Puzzle, Long> {

    @Override
    @CacheEvict(cacheNames = "puzzles", key = "#entity.id")
    <S extends Puzzle> @NotNull S save(@NotNull S entity);

    @Override
    @NotNull
    @Cacheable(cacheNames = "puzzles")
    Optional<Puzzle> findById(@NotNull Long id);

    @Cacheable(cacheNames = "puzzlesByPublishedDate")
    Optional<Puzzle> findByPublishedDate(@NotNull LocalDate publishedDate);
}
