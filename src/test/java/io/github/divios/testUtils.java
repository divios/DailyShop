package io.github.divios;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class testUtils {

    public static File getResource(String s) {
        URL res = testUtils.class.getClassLoader().getResource(s);
        File file = null;
        try {
            file = Paths.get(res.toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return file;
    }

}
