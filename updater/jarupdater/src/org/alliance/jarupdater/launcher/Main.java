package org.alliance.jarupdater.launcher;

import javax.swing.UIManager;
import org.alliance.jarupdater.core.Core;

/**
 *
 * @author Bastvera
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        new DirectoryCheck();
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
