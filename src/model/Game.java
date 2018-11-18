package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to model a round of the game.
 */
public class Game {

    // ATTRIBUTES

    // Used to store the starting time of the game.
    private long startTimeMillis;

    // Used to store the players in the game.
    private final List<Player> players;
    private final int maxPlayers;
    private final int dimensions;

    // Pattern of the game.
    private Pattern pattern;

    // Stores if the game has a winner yet and the winner itself.
    private boolean hasWinner;
    private Player winner;

    // CONSTRUCTORS

    /**
     * Creates a game with random pattern from the given specifications.
     * @param maxPlayers Maximum number of players allowed in the game.
     * @param dimensions Dimensions of the pattern.
     * @return Game created.
     */
    public static Game createRandomGame(int maxPlayers, int dimensions) {
        Game game = new Game(maxPlayers, dimensions);
        game.pattern = Pattern.createRandomPattern(dimensions);

        System.out.println(game.pattern);
        return game;
    }

    /**
     * Constructs a game of a pattern with given dimensions and the given limit for maximum number of players.
     * @param maxPlayers Maximum number of players allowed.
     * @param dimensions Dimensions of the pattern in the game.
     */
    private Game(int maxPlayers, int dimensions) {
        this.maxPlayers = maxPlayers;
        this.dimensions = dimensions;
        this.hasWinner = false;
        players = new ArrayList<Player>(maxPlayers);
    }

    // METHODS

    /**
     * Adds a player to the game if it is not already full.
     * @param name Name of the player to add.
     * @throws IllegalStateException When number of players matches the maximum number allowed.
     */
    public void addPlayer(String name) throws IllegalStateException {
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Game is full.");
        }

        Player newP = new Player(name, dimensions);
        players.add(players.size(), newP);
    }

    /**
     * Saves the starting time of the game.
     */
    public void startGame() {
        startTimeMillis = System.currentTimeMillis();
    }

    /**
     * Applies the given move to the player with the given name.
     * @param playerName Name of the player to make the move.
     * @param row Row index of the move in the pattern grid.
     * @param col Column index of the move in the pattern grid.
     * @param face Face of the cube played in the move.
     */
    public void playerMove(String playerName, int row, int col, CubeFaces face) {
        try {
            // Find the player.
            Player player = null;

            for (Player p : players) {
                if (p.getName().equals(playerName)) {
                    player = p;
                    break;
                }
            }

            // Make the move.
            player.play(row, col, face);

            // Check if the player has completed.
            if (player.checkSolution(pattern)) {
                player.setEndTime();

                // Check if the player is the first completer.
                boolean isFirstWinner = true;
                for (Player q : players) {
                    if (q == player) {
                        continue;
                    }

                    if (q.getEndResult()) {
                        isFirstWinner = false;
                        break;
                    }
                }

                if (isFirstWinner) {
                    player.setEndResult(true);
                    hasWinner = true;
                    winner = player;
                }
            }
        } catch (NullPointerException e) {
            System.err.println("Game: Player with the given name does not exist.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the game has a winner yet.
     * @return True if the game has a winner, false if not.
     */
    public boolean hasWinner() {
        return hasWinner;
    }

    /**
     * Gets the winner of the game.
     * @return If the game has a winner returns it, otherwise returns null.
     */
    public Player getWinner() {
        if (hasWinner) {
            return winner;
        } else {
            return null;
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    public long getStartTime() {
        return startTimeMillis;
    }
}
