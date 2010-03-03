package org.alliance.jarupdater.core;

import java.io.File;
import org.alliance.jarupdater.ui.MainWindow;

/**
 *
 * @author Bastvera
 */
public class Core {

    private File updateFilePath;
    private File orginalFilePath;
    private File backupFilePath;

    public Core() {
        String userDirectory;
        if (OSInfo.isLinux()) {
            if (new File("portable").exists()) {
                userDirectory = "";
            } else {
                userDirectory = System.getProperty("user.home") + "/.alliance/";
            }
        } else if (OSInfo.isWindows()) {
            if (new File("portable").exists()) {
                userDirectory = "";
            } else {
                userDirectory = System.getenv("APPDATA") + "/Alliance/";
            }
        } else {
            userDirectory = "";
        }
        updateFilePath = new File(userDirectory + "cache/alliance.update");
        orginalFilePath = new File("alliance.jar");
        backupFilePath = new File(orginalFilePath.getName().replace(".jar", ".bak"));
        new MainWindow(this).setVisible(true);
    }

    public File getBackupFilePath() {
        return backupFilePath;
    }

    public File getOrginalFilePath() {
        return orginalFilePath;
    }

    public File getUpdateFilePath() {
        return updateFilePath;
    }
}
