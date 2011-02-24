package org.alliance.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.alliance.core.CoreSubsystem;
import org.alliance.core.NeedsUserInteraction;
import org.alliance.core.NonWindowUICallback;
import org.alliance.core.comm.SearchHit;
import org.alliance.core.node.Friend;
import org.alliance.core.node.Node;
import org.alliance.core.plugins.ConsolePlugInExtension;
import org.alliance.core.plugins.PlugIn;

/**
 *
 * PlugIn to test the PlugIn and UICallback systems.
 */
public class TestPlugIn implements PlugIn {

    private static boolean everInitialized = false;

    private int initCount = 0;
    private TestUICallback testCallback;

    public void init(CoreSubsystem core) throws Exception {
        if (initCount > 0) {
            throw new IllegalStateException("PlugIns have been initialized a second time without any shutdown call.");
        }
        initCount++;

        // add a UICallback
        if (testCallback == null ) {
            testCallback = new TestUICallback(core.getSettings().getMy().getNickname());
            core.addUICallback(testCallback);
        }
        // init, and check to make sure this isn't a repeated init
        if (testCallback.active) {
            throw new IllegalStateException("We've got an init call but the UICallback is already active.");
        } else {
            testCallback.active = true;
        }
    }

    public ConsolePlugInExtension getConsoleExtensions() {
        return null;
    }

    public void shutdown() throws Exception {
        // don't worry if multiple shutdowns are called (at least for now)
        if (initCount > 0) {
            initCount--;
        }

        if (testCallback.active) {
            testCallback.active = false;
            hashCodeOfLastActiveCallbackInvokedForMain.remove(testCallback.nickname);
        } else {
            // don't worry if multiple shutdowns are called (at least for now)
        }
    }

    public static void checkInitialized() {
        if (!everInitialized) {
            throw new IllegalStateException("The TestPlugIn was not ever initialized by core init.");
        }
    }



    public static Map<String,Integer> hashCodeOfLastActiveCallbackInvokedForMain = new HashMap<String,Integer>();

    public class TestUICallback extends NonWindowUICallback {
        public final String nickname;
        public boolean active = false;
        
        public TestUICallback(String nickname) {
            this.nickname = nickname;
        }
        public void testDuplicateCallbacksInChain() {
            //System.out.println("-------------------- TestUICallback for " + nickname + " was called!  Active: " + active + "  hash: " + this.hashCode() + " Method: " + Thread.currentThread().getStackTrace()[2]);
            if (active) {
                Integer lastCallbackHash = hashCodeOfLastActiveCallbackInvokedForMain.get(nickname);
                if (lastCallbackHash != null
                    && lastCallbackHash.intValue() != this.hashCode()) {
                    throw new IllegalStateException("We've got multiple active TestUICallback objects for " + nickname + ": " + lastCallbackHash.intValue() + " != " + this.hashCode() + ".");
                }
                hashCodeOfLastActiveCallbackInvokedForMain.put(nickname, this.hashCode());
            }
        }




        @Override
        public void nodeOrSubnodesUpdated(Node node) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void noRouteToHost(Node node) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void pluginCommunicationReceived(Friend source, String data) {
            testDuplicateCallbacksInChain();
        }

        public void messageRecieved(int srcGuid, String message) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void searchHits(int srcGuid, int hops, List<SearchHit> hits) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void trace(int level, String message, Exception stackTrace) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void handleError(Throwable e, Object source) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void statusMessage(String s) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void toFront() {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void signalFriendAdded(Friend friend) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public boolean isUIVisible() {
            testDuplicateCallbacksInChain();
            return false;
        }

        @Override
        public void logNetworkEvent(String event) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void receivedShareBaseList(Friend friend, String[] shareBaseNames) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void receivedDirectoryListing(Friend friend, int i, String s, TreeMap<String, Long> fileSize) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void newUserInteractionQueued(NeedsUserInteraction ui) {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void firstDownloadEverFinished() {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void callbackRemoved() {
            testDuplicateCallbacksInChain();
        }

        public void signalFileDatabaseFlushStarting() {
            testDuplicateCallbacksInChain();
        }

        public void signalFileDatabaseFlushComplete() {
            testDuplicateCallbacksInChain();
        }

        @Override
        public void statusMessage(String s, boolean b) {
            testDuplicateCallbacksInChain();
        }
    }

}
