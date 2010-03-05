package org.alliance.jarupdater.launcher;

import java.io.File;
import org.alliance.jarupdater.core.LauncherJava;

/**
 *
 * @author Bastvera
 */
public class DirectoryCheck {

    public DirectoryCheck() {
        if (!new File("updater.jar").exists()) {
            String newCurrentDirPath = getClass().getResource("/res/gfx/updater.png").toString();
            newCurrentDirPath = newCurrentDirPath.replace("file:/", "");
            newCurrentDirPath = newCurrentDirPath.replace("jar:", "");
            newCurrentDirPath = newCurrentDirPath.substring(0, newCurrentDirPath.indexOf("updater.jar"));
            try {
                LauncherJava.execJar(newCurrentDirPath + "updater.jar", new String[0], newCurrentDirPath);
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        }
    }
}
