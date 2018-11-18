package model;

/**
 * Class used to model a player.
 */
public class Player {

    // ATTRIBUTES
    private final String name;
    private final CubeFaces[][] solutionGrid;
    private long endTime;
    private boolean didWin;

    // CONSTRUCTORS

    /**
     * Constructs a player from their name and game's board size.
     * @param name Name of the player.
     * @param boardDimensions Dimensions of the pattern to match.
     */
    public Player(String name, int boardDimensions) {
        this.name = name;
        this.solutionGrid = new CubeFaces[boardDimensions][boardDimensions];
        this.endTime = 0;
        this.didWin = false;
    }

    // METHODS

    /**
     * Compares two objects to check if they are equal.
     * @param other Object to compare with this.
     * @return True if they are equal, false if they are not.
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Player)) {
            return false;
        }

        Player otherP = (Player) other;

        return (this.name.equals(otherP.name) && this.solutionGrid == otherP.solutionGrid);
    }

    /**
     * Gets the name of the player.
     * @return Name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Method to model a move on the board.
     * @param row Row of the move.
     * @param col Column of the move.
     * @param face Upper face of the cube that is placed on the board.
     * @throws IllegalArgumentException When move indices are out of bounds of the board.
     */
    public void play(int row, int col, CubeFaces face) throws IllegalArgumentException {

        // Range checks.
        if (row >= solutionGrid.length || col >= solutionGrid.length || row < 0 || col < 0) {
            throw new IllegalArgumentException("Player: Illegal move outside board bounds.");
        }

        // Apply change.
        solutionGrid[row][col] = face;
    }

    /**
     * Checks whether the current board of the player matches the pattern.
     * @param pattern Pattern to compare with player's board.
     * @return True if board matches the pattern, false if not.
     * @throws IllegalArgumentException When dimensions of the board and the pattern does not match.
     */
    public boolean checkSolution(Pattern pattern) throws IllegalArgumentException {
        return pattern.isCorrect(solutionGrid);
    }

    /**
     * Sets the ending time of the player.
     */
    public void setEndTime() {
        endTime = System.currentTimeMillis();
    }

    /**
     * Gets the ending time of the player.
     * @return
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Sets whether the player has won.
     * @param didWin True if the player has won the last round, false if not.
     */
    public void setEndResult(boolean didWin) {
        this.didWin = didWin;
    }

    /**
     * Gets whether the player has won.
     * @return True if the player has won the last round, false if not.
     */
    public boolean getEndResult() {
        return didWin;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Did win: " + didWin;
    }
}
