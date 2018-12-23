package model;

import javafx.scene.image.Image;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Class used to load images and patterns from the resource files.
 *
 *  Implemented as a singleton.
 */
public class ResourceLoader {

    // CONSTANTS

    private static final String RESOURCE_DIR_ROOT = "/res";
    private static final String PATTERN_PACK_PREFIX = "pattern_";
    private static final String GAME_ICON_NAME = "qbitz.png";

    // ATTRIBUTES

    // Singleton object.
    private static ResourceLoader loader = new ResourceLoader();

    // Root directory of the resources.
    private static final Path root = rootLocator();

    // Streams to hold files being processed.
    private Stream<Path> filteredResources;

    // File system necessary for use in execution from JAR files.
    private static FileSystem fileSystem = null;

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
    private static Path rootLocator() {
        Path resPath = null;

        try {
            URI uri = ResourceLoader.class.getResource(RESOURCE_DIR_ROOT).toURI();

            if (uri.getScheme().equals("jar")) {
                System.out.println("ResourceLoader: Detected JAR environment.");
                fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                resPath = fileSystem.getPath(RESOURCE_DIR_ROOT);
            }
            else {
                System.out.println("ResourceLoader: Detected non-JAR environment.");
                resPath = Paths.get(uri);
            }

        } catch (InvalidPathException e) {
            resPath = null;
            System.err.println("ResourceLoader: Could not locate resources directory.");
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            resPath = null;
            System.err.println("ResourceLoader: Could not open the resources directory.");
            e.printStackTrace();
        } catch (Exception e) {
            resPath = null;
            System.err.println("ResourceLoader: Unknown error prevented locating resources directory.");
            e.printStackTrace();
        } finally {
            if (resPath != null) {
                System.out.println("ResourceLoader: Resources directory successfully located at: " + resPath);
            }
            else {
                System.err.println("ResourceLoader: Could not locate resources directory.");
            }
            return resPath;
        }
    }

    /**
     * Retrieves the game icon from the resources.
     * @return If successfully found, the Image object containing the game icon. Otherwise, null.
     */
    public Image getGameIcon() {
        System.out.println("ResourceLoader: Locating the GameIcon.");

        filterResourcesBy(ResourceTypes.GameIcon);
        Image icon = null;

        try {
            String iconURL = filteredResources.findFirst().get().toUri().toURL().toString();
            icon = new Image(iconURL);
        } catch (MalformedURLException e) {
            System.err.println("ResourceLoader: Could not retrieve the icon URL.");
            e.printStackTrace();
        }

        System.out.println("ResourceLoader: GameIcon located successfully.");
        return icon;
    }

    /**
     * Retrieves the list of pattern packages in the resources directory.
     * @return ArrayList of PatternPack objects constructed from the pattern packages found in resources.
     */
    public List<PatternPack> getPatternPacks() {
        System.out.println("ResourceLoader: Locating the PatternPacks.");

        filterResourcesBy(ResourceTypes.PatternPack);
        List<PatternPack> packs;

        Stream<PatternPack> packStream = filteredResources.map(new Function<Path, PatternPack>() {
            @Override
            public PatternPack apply(Path path) {
                return new PatternPack(path);
            }
        });

        packs = packStream.collect(Collectors.toList());

       // Running from JAR reverses expected naming order
        packs.sort(new Comparator<PatternPack>() {
            @Override
            public int compare(PatternPack o1, PatternPack o2) {
                java.util.regex.Pattern pattern = Pattern.compile("([0-9]+)");
                Matcher matcher = pattern.matcher(o1.toString());
                matcher.find();
                int patternNo1 = Integer.valueOf(matcher.group());

                matcher = pattern.matcher(o2.toString());
                matcher.find();
                int patternNo2 = Integer.valueOf(matcher.group());

                if (patternNo1 < patternNo2) return -1;
                else if (patternNo1 > patternNo2) return 1;
                else return 0;
            }
        });

        System.out.println("ResourceLoader: PatternPacks located successfully.");
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
                    filteredResources = Files.walk(root, 1).filter(new Predicate<Path>() {
                        @Override
                        public boolean test(Path path) {
                            if (!Files.isDirectory(path)) {
                                return false;
                            }

                            String pathName = path.getFileName().toString();

                            return pathName.startsWith(PATTERN_PACK_PREFIX);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("ResourceLoader: An error occurred in filtering process.");
                    e.printStackTrace();
                }
                break;

            case GameIcon:
                try {
                    filteredResources = Files.walk(root, 1).filter(new Predicate<Path>() {
                        @Override
                        public boolean test(Path path) {
                            String pathName = path.getFileName().toString();
                            return pathName.equals(GAME_ICON_NAME);
                        }
                    });
                } catch (Exception e) {
                    System.err.println("ResourceLoader: An error occurred in filtering process.");
                    e.printStackTrace();
                }
        }
    }

    /**
     * Taken from https://stackoverflow.com/a/990492/8980631
     */
    static String removeExtension(String s) {

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
