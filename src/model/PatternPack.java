package model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used to handle pattern packs and creation of cubes and patterns.
 */
public class PatternPack {

    // ATTRIBUTES

    private final Path directoryPath;
    private final String packName;

    // CONSTRUCTORS

    /**
     * Constructs a pattern pack from a directory path.
     * @param path Path of the pattern pack directory.
     */
    public PatternPack(Path path) {
        this.directoryPath = path;
        this.packName = path.getFileName().toString();
    }

    // METHODS

    /**
     * Returns the package name.
     * @return The pattern package name.
     */
    @Override
    public String toString() {
        return packName;
    }

    /**
     * Gets the Cube constructed from this PatternPack.
     * @return Cube constructed from the images in this PatternPack.
     */
    public Cube getCube() {

        Cube cube = null;

        try {
            Stream<Path> children = Files.walk(directoryPath, 2).filter(new Predicate<Path>() {
                @Override
                public boolean test(Path path) {
                    // Reject subdirectories
                    if (Files.isDirectory(path)) {
                        return false;
                    }

                    return true;
                }
            });

            List<Path> files = children.collect(Collectors.toCollection(ArrayList::new));

            cube = new Cube(files);
            System.out.println("PatternPack: Cube successfully loaded.");
        } catch (MalformedURLException|IllegalArgumentException e) {
            System.err.println("PatternPack: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("PatternPack: Failed to read files.");
            e.printStackTrace();
        } finally {
            return cube;
        }
    }
}
