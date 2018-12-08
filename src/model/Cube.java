package model;

import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;
import java.util.EnumMap;
import java.util.List;

/**
 * Class to model a cube by mapping faces to images.
 */
public class Cube {

    // CONSTANTS

    private static final String NAME_UP = "up";
    private static final String NAME_DOWN = "down";
    private static final String NAME_LEFT = "left";
    private static final String NAME_RIGHT = "right";
    private static final String NAME_FRONT = "front";
    private static final String NAME_BACK = "back";

    // ATTRIBUTES

    private final EnumMap<CubeFaces, Image> faceMap;
    // CONSTRUCTORS

    /**
     * Constructs a cube from the given list of files. Additional or redundant files in the given list of files are
     * handled well, however missing files cause exceptions.
     * @param images List of files to search for images.
     * @throws MalformedURLException When the located file could not be processed to URL.
     * @throws IllegalArgumentException When files are missing for some faces of the cube.
     */
    public Cube (List<File> images) throws MalformedURLException, IllegalArgumentException {

        // Construct the storage structure.
        this.faceMap = new EnumMap<>(CubeFaces.class);

        // Map the images to faces.
        String fileName;
        Image image;
        for (File f : images) {
            fileName = ResourceLoader.removeExtension(f.getName());
            image = new Image(f.toURI().toURL().toString(), 150, 150, true, true);
            switch (fileName) {
                default:
                    // Do nothing.
                    break;

                case NAME_UP:
                    faceMap.put(CubeFaces.FACE_UP, image);
                    break;

                case NAME_DOWN:
                    faceMap.put(CubeFaces.FACE_DOWN, image);
                    break;

                case NAME_LEFT:
                    faceMap.put(CubeFaces.FACE_LEFT, image);
                    break;

                case NAME_RIGHT:
                    faceMap.put(CubeFaces.FACE_RIGHT, image);
                    break;

                case NAME_FRONT:
                    faceMap.put(CubeFaces.FACE_FRONT, image);
                    break;

                case NAME_BACK:
                    faceMap.put(CubeFaces.FACE_BACK, image);
                    break;
            }
        }

        // Check if mapping is complete.
        for (CubeFaces face : CubeFaces.values()) {
            if (!faceMap.containsKey(face)) {
                throw new IllegalArgumentException("Incomplete pattern pack, cube cannot be constructed.");
            }
        }
    }

    // METHODS

    /**
     * Gets the image mapped to given face.
     * @param face Face to get the image for.
     * @return Image mapped to the given face.
     */
    public Image get(CubeFaces face) {
        return faceMap.get(face);
    }
}
