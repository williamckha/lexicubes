package com.lexicubes.backend.puzzle;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PuzzleGenerator {

    private final PuzzleRepository puzzleRepository;

    private final Random random;

    public PuzzleGenerator(PuzzleRepository puzzleRepository) {
        this.puzzleRepository = puzzleRepository;
        random = new Random();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Puzzle generateAndSaveDailyPuzzle(LocalDate date) {
        final Optional<Puzzle> existingPuzzle = puzzleRepository.findDailyByPublishedDate(date);
        return existingPuzzle.orElseGet(() -> puzzleRepository.save(Puzzle.of(date, true, generatePyramidPuzzleCubes(3))));
    }

    public List<Puzzle.Cube> generatePyramidPuzzleCubes(int size) {
        final List<Puzzle.Cube> cubes = new ArrayList<>();

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
        double randomNumber = random.nextDouble();
        for (int i = 0; i < LETTER_FREQUENCY.length; i++) {
            randomNumber -= LETTER_FREQUENCY[i];
            if (randomNumber <= 0) {
                return (char) ('a' + i);
            }
        }
        return 'z';
    }

    /**
     * Letter frequency based on English language dictionaries.
     * Taken from <a href="https://en.wikipedia.org/wiki/Letter_frequency">Wikipedia</a>.
     */
    private static final double[] LETTER_FREQUENCY = {
            0.0780, // a
            0.0200, // b
            0.0400, // c
            0.0380, // d
            0.1100, // e
            0.0140, // f
            0.0300, // g
            0.0230, // h
            0.0860, // i
            0.0021, // j
            0.0097, // k
            0.0530, // l
            0.0270, // m
            0.0720, // n
            0.0610, // o
            0.0280, // p
            0.0019, // q
            0.0730, // r
            0.0870, // s
            0.0670, // t
            0.0330, // u
            0.0100, // v
            0.0091, // w
            0.0027, // x
            0.0160, // y
            0.0044  // z
    };
}
