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
        } else {
            userDirectory = "";
        }
        updateFilePath = new File(userDirectory + "cache/" + "alliance.update");
        if (!updateFilePath.exists()) {
            updateFilePath = MainWindow.localizeFile("alliance.update", "update", "Alliance Update Files");
        }
        orginalFilePath = new File("alliance.jar");
        if (!orginalFilePath.exists()) {
            orginalFilePath = MainWindow.localizeFile("alliance.jar", "jar", "Jar Files");
        }
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
