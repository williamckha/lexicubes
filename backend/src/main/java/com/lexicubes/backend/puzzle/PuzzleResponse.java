package com.lexicubes.backend.puzzle;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record PuzzleResponse(Long id,
                             @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                             LocalDate publishedDate,
                             boolean isDaily,
                             List<Cube> cubes,
                             Dimensions dimensions,
                             List<Solution> solutions) {

    public record Cube(int id,
                       int x,
                       int y,
                       int z,
                       Face topFace,
                       Face leftFace,
                       Face rightFace) {

        public record Face(int id,
                           char letter,
                           List<Integer> neighbourIds) {}
    }

    public record Dimensions(int lengthX,
                             int lengthY,
                             int lengthZ) {}

    public record Solution(String word,
                           List<Integer> faceIds,
                           boolean isBonus) {}
}
