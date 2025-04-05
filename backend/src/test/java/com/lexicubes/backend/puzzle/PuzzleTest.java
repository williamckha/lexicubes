package com.lexicubes.backend.puzzle;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PuzzleTest {

    @Test
    public void singleCube_facesShouldHaveOtherFacesAsNeighbours() {
        final Puzzle.Cube cube = new Puzzle.Cube(1, 0, 0, 0, 'a', 'b', 'c');
        Puzzle.of(List.of(cube));

        assertEquals(Set.of(cube.getLeftFace(), cube.getRightFace()), cube.getTopFace().getNeighbours());
        assertEquals(Set.of(cube.getTopFace(), cube.getRightFace()), cube.getLeftFace().getNeighbours());
        assertEquals(Set.of(cube.getTopFace(), cube.getLeftFace()), cube.getRightFace().getNeighbours());
    }
}
