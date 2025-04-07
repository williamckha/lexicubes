package com.lexicubes.backend.puzzle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PuzzleTest {

    private static Puzzle threeByThreePuzzle;

    @BeforeAll
    public static void initializeThreeByThreePuzzle() {
        final List<Puzzle.Cube> cubes = new ArrayList<>();

        int cubeId = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    cubes.add(new Puzzle.Cube(cubeId++, x, y, z, 'a', 'b', 'c'));
                }
            }
        }

        threeByThreePuzzle = Puzzle.of(cubes);
    }

    @Test
    public void singleCubePuzzle_faceShouldHaveEveryOtherFaceAsNeighbour() {
        final Puzzle.Cube cube = new Puzzle.Cube(0, 0, 0, 0, 'a', 'b', 'c');
        Puzzle.of(List.of(cube));

        assertEquals(Set.of(cube.getLeftFace(), cube.getRightFace()), cube.getTopFace().getNeighbours());
        assertEquals(Set.of(cube.getTopFace(), cube.getRightFace()), cube.getLeftFace().getNeighbours());
        assertEquals(Set.of(cube.getTopFace(), cube.getLeftFace()), cube.getRightFace().getNeighbours());
    }

    @Test
    public void threeByThreePuzzle_innerCubeTopFaceShouldHaveEveryFaceSharingEdgeOrCornerAsNeighbour() {
        final Set<Puzzle.Cube.Face> neighbours = new HashSet<>();

        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 2, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);

        final Puzzle.Cube innerCube = threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).orElse(null);
        assertNotNull(innerCube);
        assertEquals(neighbours, innerCube.getTopFace().getNeighbours());
    }

    @Test
    public void threeByThreePuzzle_innerCubeLeftFaceShouldHaveEveryFaceSharingEdgeOrCornerAsNeighbour() {
        final Set<Puzzle.Cube.Face> neighbours = new HashSet<>();

        threeByThreePuzzle.getCubeAtCoordinate(0, 0, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 0, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 0, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 0, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 0, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 1, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(0, 2, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);

        final Puzzle.Cube innerCube = threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).orElse(null);
        assertNotNull(innerCube);
        assertEquals(neighbours, innerCube.getLeftFace().getNeighbours());
    }

    @Test
    public void threeByThreePuzzle_innerCubeRightFaceShouldHaveEveryFaceSharingEdgeOrCornerAsNeighbour() {
        final Set<Puzzle.Cube.Face> neighbours = new HashSet<>();

        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 0, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 0, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 0).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 1).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 1, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 1, 2).map(Puzzle.Cube::getTopFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 0).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 2, 0).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 1).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(2, 2, 1).map(Puzzle.Cube::getLeftFace).ifPresent(neighbours::add);
        threeByThreePuzzle.getCubeAtCoordinate(1, 2, 2).map(Puzzle.Cube::getRightFace).ifPresent(neighbours::add);

        final Puzzle.Cube innerCube = threeByThreePuzzle.getCubeAtCoordinate(1, 1, 1).orElse(null);
        assertNotNull(innerCube);
        assertEquals(neighbours, innerCube.getRightFace().getNeighbours());
    }

    @Test
    public void puzzleWithDuplicateCubeIds_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'a', 'b', 'c'),
                new Puzzle.Cube(0, 1, 0, 0, 'd', 'e', 'f'),
                new Puzzle.Cube(1, 0, 0, 1, 'g', 'h', 'i'),
                new Puzzle.Cube(2, 1, 0, 1, 'j', 'k', 'l')
        )));
    }

    @Test
    public void puzzleWithDuplicateCubeCoordinates_shouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Puzzle.of(List.of(
                new Puzzle.Cube(0, 0, 0, 0, 'a', 'b', 'c'),
                new Puzzle.Cube(1, 0, 0, 1, 'd', 'e', 'f'),
                new Puzzle.Cube(2, 0, 0, 1, 'g', 'h', 'i'),
                new Puzzle.Cube(3, 1, 0, 0, 'j', 'k', 'l')
        )));
    }

    @Test
    public void threeByThreePuzzle_allFaceIdsShouldBeUnique() {
        final List<Integer> faceIds = threeByThreePuzzle.getCubes().stream()
                .flatMap(cube -> Stream.of(cube.getTopFace(), cube.getLeftFace(), cube.getRightFace()))
                .map(Puzzle.Cube.Face::getId)
                .toList();

        assertEquals(faceIds.size(), faceIds.stream().distinct().count());
    }

    @Test
    public void threeByThreePuzzleCubes_shouldBeJsonSerializableAndDeserializable() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        final Puzzle.Cubes cubes = new Puzzle.Cubes(threeByThreePuzzle.getCubes());
        final String json = mapper.writeValueAsString(cubes);
        final Puzzle.Cubes deserializedCubes = mapper.readValue(json, Puzzle.Cubes.class);

        final List<Puzzle.Cube.Face> allFaces = cubes.list().stream()
                .flatMap(cube -> Stream.of(cube.getTopFace(), cube.getLeftFace(), cube.getRightFace()))
                .toList();

        final List<Puzzle.Cube.Face> allDeserializedFaces = deserializedCubes.list().stream()
                .flatMap(cube -> Stream.of(cube.getTopFace(), cube.getLeftFace(), cube.getRightFace()))
                .toList();

        assertEquals(cubes, deserializedCubes);
        assertEquals(allFaces.stream().map(Puzzle.Cube.Face::getCube).toList(),
                allDeserializedFaces.stream().map(Puzzle.Cube.Face::getCube).toList());
    }
}
