package org.alliance.misc;

import org.alliance.core.CoreSubsystem;
import org.alliance.core.plugins.ConsolePlugInExtension;
import org.alliance.core.plugins.PlugIn;

/**
 *
 * PlugIn to test the PlugIn and UICallback systems.
 */
public class TestPlugIn implements PlugIn {

    private static boolean everInitialized = false;

    private int initCount = 0;

    public void init(CoreSubsystem core) throws Exception {
        if (initCount > 0) {
            throw new IllegalStateException("PlugIns have been initialized a second time without any shutdown call.");
        }
        initCount++;
    }

    public ConsolePlugInExtension getConsoleExtensions() {
        return null;
    }

    public void shutdown() throws Exception {
        // don't worry if multiple shutdowns are called (at least for now)
        if (initCount > 0) {
            initCount--;
        }
    }

    public static void checkInitialized() {
        if (!everInitialized) {
            new Exception("The TestPlugIn was not ever initialized by core init.").printStackTrace();
        }
    }

}
