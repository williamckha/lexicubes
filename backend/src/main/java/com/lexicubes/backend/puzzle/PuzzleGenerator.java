package com.lexicubes.backend.puzzle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class PuzzleGenerator {

    private final int[] letterDistribution = {
            14810,
            2715,
            4943,
            7874,
            21912,
            4200,
            3693,
            10795,
            13318,
            188,
            1257,
            7253,
            4761,
            12666,
            14003,
            3316,
            205,
            10977,
            11450,
            16587,
            5246,
            2019,
            3819,
            315,
            3853,
            128
    };

    private final int letterDistributionTotal = Arrays.stream(letterDistribution).sum();

    private final PuzzleRepository puzzleRepository;

    private final Random random;

    public PuzzleGenerator(PuzzleRepository puzzleRepository) {
        this.puzzleRepository = puzzleRepository;
        random = new Random();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Puzzle generateAndSaveDailyPuzzle(LocalDate date) {
        final Optional<Puzzle> existingPuzzle = puzzleRepository.findDailyByPublishedDate(date);
        return existingPuzzle.orElseGet(() -> puzzleRepository.save(Puzzle.of(date, true, generatePuzzleCubes(3))));
    }

    public List<Puzzle.Cube> generatePuzzleCubes(int size) {
        final int numCubes = size * size * size;
        final List<Puzzle.Cube> cubes = new ArrayList<>(numCubes);

        int cubeId = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size - y; x++) {
                for (int z = 0; z < size - y; z++) {
                    cubes.add(new Puzzle.Cube(cubeId++, x, y, z,
                            getRandomLetter(), getRandomLetter(), getRandomLetter()));
                }
            }
        }

        return cubes;
    }

    private char getRandomLetter() {
        int randomNumber = random.nextInt(1, letterDistributionTotal);
        for (int i = 0; i < letterDistribution.length; i++) {
            randomNumber -= letterDistribution[i];
            if (randomNumber <= 0) {
                return (char) ('a' + i);
            }
        }
        return 'z';
    }
}
