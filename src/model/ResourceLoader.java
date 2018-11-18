package model;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Class used to load images and patterns from the resource files.
 *
 *  Implemented as a singleton.
 */
public class ResourceLoader {

    // CONSTANTS

    private static final String PATTERN_PACK_PREFIX = "pattern_";
    private static final String GAME_ICON_NAME = "qbitz.png";

    // ATTRIBUTES

    // Singleton object.
    private static ResourceLoader loader = new ResourceLoader();

    // Root directory of the resources.
    private static final File root = rootLocator();

    // Streams to hold files being processed.
    private Stream<Path> filteredResources;

    // CONSTRUCTORS

    /**
     * Gets the reference of the singleton object ResourceLoader.
     * @return Reference to the singleton ResourceLoader.
     */
    public static ResourceLoader getInstance() {
        return loader;
    }

    /**
     * Constructor made private.
     */
    private ResourceLoader() {

    }

    // Methods

    /**
     * Locates and returns the root of the resources directory.
     * @return Root of the resources directory. If an exception occurred returns null instead.
     */
    private static File rootLocator() {

        System.out.println("ResourceLoader: Locating the resources directory...");
        File returnF = null;

        try {
            returnF = Paths.get("res").toFile();
        } catch (InvalidPathException e) {
            returnF = null;
            System.err.println("ResourceLoader: Could not locate resources directory.");
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            returnF = null;
            System.err.println("ResourceLoader: Could not open the resources directory.");
            e.printStackTrace();
        } finally {
            if (returnF != null) {
                System.out.println("ResourcesLoader: Resources directory successfully located at: " + returnF.toPath().toAbsolutePath());
            }
            return returnF;
        }
    }

    /**
     * Retrieves the game icon from the resources.
     * @return If successfully found, the Image object containing the game icon. Otherwise, null.
     */
    public Image getGameIcon() {
        filterResourcesBy(ResourceTypes.GameIcon);
        Image icon = null;

        try {
            String iconURL = filteredResources.findFirst().get().toUri().toURL().toString();
            icon = new Image(iconURL);
        } catch (MalformedURLException e) {
            System.err.println("ResourceLoader: Could not retrieve the icon URL.");
            e.printStackTrace();
        }

        return icon;
    }

    /**
     * Retrieves the list of pattern packages in the resources directory.
     * @return ArrayList of PatternPack objects constructed from the pattern packages found in resources.
     */
    public List<PatternPack> getPatternPacks() {
        filterResourcesBy(ResourceTypes.PatternPack);
        List<PatternPack> packs = null;

        Stream<PatternPack> packStream = filteredResources.map(new Function<Path, PatternPack>() {
            @Override
            public PatternPack apply(Path path) {
                return new PatternPack(path);
            }
        });

        packs = packStream.collect(Collectors.toCollection(ArrayList::new));

        return packs;
    }

    /**
     * Used to filter resources by certain types.
     * @param type Enumeration for supported resource types.
     */
    private void filterResourcesBy(ResourceTypes type) {

        switch (type) {
            default:
                System.err.println("ResourceLoader: Unexpected file filter type.");
                break;

            case PatternPack:
                try {
                    filteredResources = Files.find(root.toPath(), 1, new BiPredicate<Path, BasicFileAttributes>() {
                        @Override
                        public boolean test(Path path, BasicFileAttributes basicFileAttributes) {
                            if (!basicFileAttributes.isDirectory()) {
                                return false;
                            }

                            String pathName = path.getFileName().toString();
                            return pathName.startsWith(PATTERN_PACK_PREFIX);
                        }
                    });
                } catch (IOException e) {
                    System.err.println("ResourceLoader: An error occurred in filtering process.");
                    e.printStackTrace();
                }
                break;

            case GameIcon:
                try {
                    filteredResources = Files.find(root.toPath(), 1, new BiPredicate<Path, BasicFileAttributes>() {
                        @Override
                        public boolean test(Path path, BasicFileAttributes basicFileAttributes) {
                            if(basicFileAttributes.isDirectory()) {
                                return false;
                            }

                            String pathName = path.getFileName().toString();
                            return pathName.equals(GAME_ICON_NAME);
                        }
                    });
                } catch (IOException e) {
                    System.err.println("ResourceLoader: An error occurred in filtering process.");
                    e.printStackTrace();
                }
        }
    }

    /**
     * Taken from https://stackoverflow.com/a/990492/8980631
     */
    public static String removeExtension(String s) {

        String separator = System.getProperty("file.separator");
        String filename;

        // Remove the path upto the filename.
        int lastSeparatorIndex = s.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = s;
        } else {
            filename = s.substring(lastSeparatorIndex + 1);
        }

        // Remove the extension.
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }

    /*
        INNER CLASSES
    */

    /**
     * Types used to filter resources.
     */
    enum ResourceTypes {
        PatternPack,
        GameIcon
    }
}
