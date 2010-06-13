package org.alliance.jarupdater.launcher;

import javax.swing.UIManager;
import org.alliance.jarupdater.core.Core;
import org.alliance.jarupdater.core.OSInfo;

/**
 *
 * @author Bastvera
 */
public class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (!OSInfo.isWindows()) {
            new DirectoryCheck(args);
        }
        setNativeLookAndFeel();
        new Core();
    }

    private static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception exc) {
                exc.printStackTrace();
                System.exit(0);
            }
        }
    }
}
