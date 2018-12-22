package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class used to model a pattern by creating a grid of cube faces.
 */
public class Pattern implements Serializable {

    // ATTRIBUTES
    private CubeFaces[][] patternGrid;

    // CONSTRUCTORS

    /**
     * Creates a random pattern.
     * @param dimensions Length of the sides of the square pattern.
     * @return Randomly generated pattern of given dimensions.
     */
    public static Pattern createRandomPattern(int dimensions) {
        Pattern pattern = new Pattern(dimensions);
        CubeFaces[] faces = CubeFaces.values();
        CubeFaces currFace;

        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++) {
                currFace = faces[ThreadLocalRandom.current().nextInt(faces.length)];
                pattern.patternGrid[i][j] = currFace;
            }
        }

        return pattern;
    }

    /**
     *
     * @param patternGrid
     * @return
     */
    public static Pattern createPatternFromPatternGrid(CubeFaces[][] patternGrid) {
        Pattern pattern = new Pattern(patternGrid.length);
        pattern.patternGrid = new CubeFaces[patternGrid.length][];

        for (int i = 0; i < patternGrid.length; i++) {
            pattern.patternGrid[i] = new CubeFaces[patternGrid[i].length];

            for (int j = 0; j < patternGrid[i].length; j++) {
                pattern.patternGrid[i][j] = patternGrid[i][j];
            }
        }

        return pattern;
    }

    /**
     * Constructor made private.
     * @param dimensions Length of the sides of the square pattern.
     */
    private Pattern(int dimensions) {
        patternGrid = new CubeFaces[dimensions][dimensions];
    }

    // METHODS

    /**
     * Checks if the given solution grid is correct with respect to this pattern.
     * @param solution Square grid of cube faces.
     * @return True if solution is equal to the pattern, False if not.
     * @throws IllegalArgumentException When solution dimensions do not match pattern dimensions.
     */
    public boolean isCorrect(CubeFaces[][] solution) throws IllegalArgumentException {

        // Checking if dimensions are legal. Assuming rectangular grids.
        if (patternGrid.length != solution.length || patternGrid[0].length != solution[0].length) {
            throw new IllegalArgumentException("Pattern and solution dimensions do not match.");
        }

        // Patterns are not square in Painting puzzle mode, hence this is disabled
//        for (int i = 0; i < patternGrid.length; i++) {
//            if (patternGrid[i].length != solution[i].length) {
//                throw new IllegalArgumentException("Pattern and solution dimensions do not match.");
//            }
//        }

        // Check if solution is correct.
        for (int i = 0; i < patternGrid.length; i++) {
            for (int j = 0; j < patternGrid[i].length; j++) {
                if (patternGrid[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }

        // If not incorrect, then correct.
        return true;
    }

    public CubeFaces[][] getPatternGrid() {
        return patternGrid;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(patternGrid);
    }
}
