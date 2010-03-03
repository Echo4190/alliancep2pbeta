package org.alliance.jarupdater.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import org.alliance.jarupdater.core.Core;
import org.alliance.jarupdater.core.LauncherJava;

/**
 *
 * @author Bastvera
 */
public class MainWindow extends JFrame {

    private JButton button;
    private JLabel label;
    private JMenu menuHelp;
    private JMenuBar menu;
    private JMenuItem menuHelpAbout;
    private JProgressBar progressBar;
    private final Core core;

    public MainWindow(Core core) {
        this.core = core;
        initComponents();
        centerOnScreen();
    }

    private void initComponents() {

        this.setTitle("Alliance Udpdater");
        this.setPreferredSize(new Dimension(260, 160));
        this.setResizable(false);

        this.setIconImage(new ImageIcon(getClass().getResource("/res/gfx/updater.png")).getImage());

        label = new JLabel();
        progressBar = new JProgressBar();
        button = new JButton();
        menu = new JMenuBar();
        menuHelp = new JMenu();
        menuHelpAbout = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        label.setFont(new Font("Tahoma", 0, 14));
        label.setText("Press 'Update' to begin updating.");

        button.setText("Update");
        button.setMaximumSize(new Dimension(75, 25));
        button.setMinimumSize(new Dimension(75, 25));
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                buttonMousePressed(evt);
            }
        });

        menuHelp.setText("Help");
        menuHelpAbout.setText("About");
        menuHelp.add(menuHelpAbout);
        menu.add(menuHelp);
        setJMenuBar(menu);
        menuHelpAbout.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent evt) {
                menuHelpAboutMousePressed(evt);
            }
        });

        javax.swing.GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().
                addComponent(button).addGap(93, 93, 93)).addGroup(layout.createSequentialGroup().addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE).
                addContainerGap()).addGroup(layout.createSequentialGroup().addComponent(label, GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE).addContainerGap()))));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(7, 7, 7).
                addComponent(label, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).
                addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).
                addComponent(button).addGap(23, 23, 23)));

        pack();
    }

    private void menuHelpAboutMousePressed(MouseEvent evt) {
        new AboutDialog(this, false).setVisible(true);
    }

    private void buttonMousePressed(MouseEvent evt) {
        if (button.getText().equals("Update")) {
            button.setEnabled(false);
            progressBar.setIndeterminate(true);
            backup();
        } else {
            try {
                LauncherJava.execJar("alliance.jar", new String[0]);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Restart ... failed\nPlease start manually.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            System.exit(0);
        }
    }

    private void backup() {
        label.setText("Making a backup ... Please Wait ...");
        copyFile(core.getOrginalFilePath(), core.getBackupFilePath(), true, false);
    }

    private void update() {
        label.setText("Updating ... Please Wait...");
        copyFile(core.getUpdateFilePath(), core.getOrginalFilePath(), false, true);
    }

    private void finish() {
        label.setText("All done. Press OK to start Alliance.");
        button.setText("OK");
        button.setEnabled(true);
        progressBar.setIndeterminate(false);
    }

    private void centerOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }

        this.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }

    private void copyFile(final File src, final File dst, final boolean backup, final boolean update) {
        Runnable copy = new Runnable() {

            @Override
            public void run() {
                FileInputStream in = null;
                try {
                    in = new FileInputStream(src);
                    FileOutputStream out = new FileOutputStream(dst);
                    byte[] buf = new byte[128 * 1024];
                    int read;
                    while ((read = in.read(buf)) != -1) {
                        out.write(buf, 0, read);
                    }
                    out.flush();
                    out.close();
                    in.close();
                    if (backup) {
                        update();
                    }
                    if (update) {
                        finish();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Updating ... failed\nTry running updater as Administrator.", "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Updating ... failed\nTry running updater as Administrator.", "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    }
                }
            }
        };
        Thread copyT = new Thread(copy);
        copyT.start();
    }
}
