package net.socialhub.core.utils;

import net.socialhub.core.model.error.SocialHubException;

import java.io.File;
import java.io.FileOutputStream;

public class DataUtil {

    /**
     * byte to file
     */
    public static File bytesToFile(byte[] bytes) {

        File file = new File("image.png");
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(bytes);
            return file;

        } catch (Exception e) {
            throw new SocialHubException(e);
        }
    }
}
