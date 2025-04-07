package com.lexicubes.backend.puzzle;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Table("puzzles")
public class Puzzle {

    /**
     * Represents a cube at some coordinate in a puzzle.
     * <p>
     * We use a right-hand coordinate system, where the x-axis is pointing "right" (southeast),
     * the y-axis is pointing up, and the z-axis is pointing "left" (southwest).
     * <p>
     * The puzzle is viewed in isometric perspective, so each cube only has three visible
     * faces (top, left, and right).
     */
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id",
            scope = Puzzle.Cube.class)
    public static class Cube {

        @JsonIdentityInfo(
                generator = ObjectIdGenerators.PropertyGenerator.class,
                property = "id",
                scope = Puzzle.Cube.Face.class)
        public static class Face {

            private record Edge(Point start, Point end) {

                private record Point(int x, int y, int z) {}

                private boolean isTouching(Edge edge) {
                    return start.equals(edge.start) || start.equals(edge.end) ||
                            end.equals(edge.start) || end.equals(edge.end);
                }
            }

            private static final Map<Side, List<Edge.Point>> FACE_VERTICES = Map.of(
                    Side.TOP, List.of(
                            new Edge.Point(0, 1, 0),
                            new Edge.Point(0, 1, 1),
                            new Edge.Point(1, 1, 1),
                            new Edge.Point(1, 1, 0)),
                    Side.LEFT, List.of(
                            new Edge.Point(0, 0, 1),
                            new Edge.Point(1, 0, 1),
                            new Edge.Point(1, 1, 1),
                            new Edge.Point(0, 1, 1)),
                    Side.RIGHT, List.of(
                            new Edge.Point(1, 0, 0),
                            new Edge.Point(1, 0, 1),
                            new Edge.Point(1, 1, 1),
                            new Edge.Point(1, 1, 0)));

            private final int id;

            private final char letter;

            private final Side side;

            private Cube cube;

            @JsonIgnore
            private final List<Edge> edges;

            @JsonIgnore
            private final Set<Face> neighbours;

            @JsonCreator
            private Face(@JsonProperty int id,
                         @JsonProperty char letter,
                         @JsonProperty Side side) {

                if (letter < 'a' || letter > 'z') {
                    throw new IllegalArgumentException("Face letter must be lowercase");
                }

                this.id = id;
                this.letter = letter;
                this.side = side;
                this.edges = new ArrayList<>();
                this.neighbours = new HashSet<>();
            }

            private Face(Cube cube, Side side, char letter) {
                this(cube.getId() * Side.values().length + side.ordinal(), letter, side);
                setCube(cube);
            }

            public int getId() {
                return id;
            }

            public char getLetter() {
                return letter;
            }

            public Side getSide() {
                return side;
            }

            public Cube getCube() {
                return cube;
            }

            private void setCube(Cube cube) {
                this.cube = cube;

                final List<Edge.Point> vertices = FACE_VERTICES.get(side).stream()
                        .map(point -> new Edge.Point(
                                point.x() + cube.getX(),
                                point.y() + cube.getY(),
                                point.z() + cube.getZ()))
                        .toList();

                edges.clear();
                for (int i = 0; i < vertices.size(); i++) {
                    edges.add(new Edge(vertices.get(i), vertices.get((i + 1) % vertices.size())));
                }
            }

            public Set<Face> getNeighbours() {
                return Collections.unmodifiableSet(neighbours);
            }

            private void addNeighbour(Face neighbour) {
                neighbours.add(neighbour);
            }

            private boolean isTouching(Face face) {
                return edges.stream().anyMatch(edge -> face.edges.stream().anyMatch(edge::isTouching));
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Face other &&
                        id == other.id &&
                        letter == other.letter &&
                        side == other.side;
            }

            @Override
            public int hashCode() {
                return id;
            }
        }

        public enum Side {
            TOP, LEFT, RIGHT
        }

        private final int id;

        private final int x;
        private final int y;
        private final int z;

        private final Face topFace;
        private final Face leftFace;
        private final Face rightFace;

        @JsonCreator
        private Cube(@JsonProperty int id,
                     @JsonProperty int x,
                     @JsonProperty int y,
                     @JsonProperty int z) {

            this.id = id;

            this.x = x;
            this.y = y;
            this.z = z;

            this.topFace = null;
            this.leftFace = null;
            this.rightFace = null;
        }

        public Cube(int id,
                    int x,
                    int y,
                    int z,
                    char topFaceLetter,
                    char leftFaceLetter,
                    char rightFaceLetter) {

            if (x < 0 || y < 0 || z < 0) {
                throw new IllegalArgumentException("Cube coordinates must be non-negative");
            }

            this.id = id;

            this.x = x;
            this.y = y;
            this.z = z;

            this.topFace = new Face(this, Side.TOP, topFaceLetter);
            this.leftFace = new Face(this, Side.LEFT, leftFaceLetter);
            this.rightFace = new Face(this, Side.RIGHT, rightFaceLetter);
        }

        public int getId() {
            return id;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        /**
         * @return the top face (positive Y side) of the cube
         */
        public Face getTopFace() {
            return topFace;
        }

        /**
         * @return the left face (positive Z side) of the cube
         */
        public Face getLeftFace() {
            return leftFace;
        }

        /**
         * @return the right face (positive X side) of the cube
         */
        public Face getRightFace() {
            return rightFace;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Cube other &&
                    id == other.id &&
                    x == other.x &&
                    y == other.y &&
                    z == other.z &&
                    Objects.equals(topFace, other.topFace) &&
                    Objects.equals(leftFace, other.leftFace) &&
                    Objects.equals(rightFace, other.rightFace);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     * Wrapper around a list of {@link Cube}s.
     * This is necessary to prevent Spring Data from inferring a one-to-many relationship,
     * forcing usage of our custom converter that serializes the cubes to JSON.
     */
    public record Cubes(List<Cube> list) {}

    @Id
    @Nullable
    private final Long id;

    private final LocalDate publishedDate;

    private final boolean isDaily;

    private final Cubes cubes;

    @Transient
    private final Map<Integer, Cube> cubesByCoordinateKey;

    @Transient
    private final int lengthX;

    @Transient
    private final int lengthY;

    @Transient
    private final int lengthZ;

    public static Puzzle of(List<Cube> cubes) {
        return Puzzle.of(LocalDate.now(), false, cubes);
    }

    public static Puzzle of(LocalDate publishedDate, boolean isDaily, List<Cube> cubes) {
        return new Puzzle(null, publishedDate, isDaily, new Cubes(cubes));
    }

    public Puzzle(@Nullable Long id, LocalDate publishedDate, boolean isDaily, Cubes cubes) {
        if (cubes.list().stream().map(Cube::getId).distinct().count() != cubes.list().size()) {
            throw new IllegalArgumentException("Cube IDs must be unique");
        }

        this.id = id;
        this.publishedDate = publishedDate;
        this.isDaily = isDaily;
        this.cubes = cubes;

        lengthX = cubes.list().stream().mapToInt(Cube::getX).max().orElse(-1) + 1;
        lengthY = cubes.list().stream().mapToInt(Cube::getY).max().orElse(-1) + 1;
        lengthZ = cubes.list().stream().mapToInt(Cube::getZ).max().orElse(-1) + 1;

        try {
            cubesByCoordinateKey = cubes.list().stream().collect(Collectors.toMap(
                    cube -> convertCoordinateToKey(cube.getX(), cube.getY(), cube.getZ()),
                    cube -> cube));
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("Cube coordinates must be unique");
        }

        resolveNeighboursForCubeFaces();
    }

    public @Nullable Long getId() {
        return id;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public boolean isDaily() {
        return isDaily;
    }

    public List<Cube> getCubes() {
        return Collections.unmodifiableList(cubes.list());
    }

    public Optional<Cube> getCubeAtCoordinate(int x, int y, int z) {
        if (isWithinBounds(x, y, z)) {
            return Optional.ofNullable(cubesByCoordinateKey.get(convertCoordinateToKey(x, y, z)));
        }
        return Optional.empty();
    }

    public int getLengthX() {
        return lengthX;
    }

    public int getLengthY() {
        return lengthY;
    }

    public int getLengthZ() {
        return lengthZ;
    }

    public boolean isWithinBounds(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < lengthX && y < lengthY && z < lengthZ;
    }

    /**
     * Converts a 3D coordinate (x, y, z) into a unique integer key using row-major order.
     *
     * @param x the x-coordinate of the cube
     * @param y the y-coordinate of the cube
     * @param z the z-coordinate of the cube
     * @return a unique integer key representing the (x, y, z) coordinate
     */
    private int convertCoordinateToKey(int x, int y, int z) {
        assert isWithinBounds(x, y, z);
        return x * lengthY * lengthZ + y * lengthZ + z;
    }

    private void resolveNeighboursForCubeFaces() {
        final List<Cube.Face> allFaces = cubes.list().stream()
                .flatMap(cube -> Stream.of(cube.getTopFace(), cube.getLeftFace(), cube.getRightFace()))
                .toList();

        for (final Cube.Face face : allFaces) {
            for (final Cube.Face otherFace : allFaces) {
                if (face != otherFace && face.isTouching(otherFace)) {
                    face.addNeighbour(otherFace);
                }
            }
        }
    }
}
