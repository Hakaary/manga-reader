package org.gui;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import com.formdev.flatlaf.FlatDarculaLaf;

import org.reader.PageManager;

public class AppFrame extends JFrame {

    private final AppImageDisplay imageDisplay;
    private final AppNavbar navbar;

    private boolean isFullscreen = false;
    private int previousWidth = 570;
    private int previousHeight = 800;
    private Point previousLocation;

    private Point initialClick;

    public AppFrame() {
        super();
        FlatDarculaLaf.setup();

        setSize(previousWidth, previousHeight);
        setLayout(new BorderLayout());

        setUndecorated(true);

        imageDisplay = new AppImageDisplay();
        navbar = new AppNavbar();

        add(imageDisplay.getImageDisplay(), BorderLayout.CENTER);
        add(navbar.getNavbarPanel(), BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setFuncs();
        setLocationRelativeTo(null);
        previousLocation = getLocation();
    }

    public void setNewChapter(int chapter) {
        navbar.setNewChapter(chapter);
    }

    public void setChapter(int chapter) {
        PageManager.setCurrentChapter(chapter);
    }

    public Integer getCurrentSelectedChapter() {
        return navbar.getCurrentSelectedChapter();
    }

    public void setCurrentImage() {
        imageDisplay.setCurrentImage(PageManager.getCurrentPage());
    }

    public void setTxtPage(int page, int totalPages) {
        navbar.setTxtPage(page, totalPages);
    }

    public void setCurrentChapterCbBox(int chapter, boolean triggerFunc) {
        navbar.setCurrentChapterCbBox(chapter, triggerFunc);
    }

    private void toggleFullscreen() {
        GraphicsDevice gd = getGraphicsDeviceForFrame();

        if (!isFullscreen) {
            previousWidth = getWidth();
            previousHeight = getHeight();
            previousLocation = getLocation();

            dispose();
            setUndecorated(true);
            setVisible(true);
            gd.setFullScreenWindow(this);
        } else {
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(true);
            setVisible(true);
            setSize(previousWidth, previousHeight);
            setLocation(previousLocation);
        }
        isFullscreen = !isFullscreen;
    }

    private GraphicsDevice getGraphicsDeviceForFrame() {
        GraphicsDevice device = null;
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = env.getScreenDevices();

        Rectangle bounds;
        for (GraphicsDevice gd : devices) {
            bounds = gd.getDefaultConfiguration().getBounds();
            if (bounds.contains(getLocation())) {
                device = gd;
                break;
            }
        }
        return device != null ? device : env.getDefaultScreenDevice();
    }

    private void setFuncs() {
        navbar.setCbChapterFunc(() -> {
            PageManager.setCurrentChapter(
                    getCurrentSelectedChapter()
            );
            PageManager.setCurrentPageIdx(0);
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
        });

        // Close button
        navbar.setButtonCloseFunc(() -> {
            dispose();
            System.exit(0);
        });

        navbar.setButtonPreviousFunc(() -> {
            PageManager.setPrevPage();
            setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
        });

        navbar.setButtonNextFunc(() -> {
            PageManager.setNextPage();
            setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
        });

        imageDisplay.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    toggleFullscreen();
                }
            }
        });

        imageDisplay.addMouseMotionListener(
                new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isFullscreen) {
                    int frameX = getLocation().x;
                    int frameY = getLocation().y;

                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    int newX = frameX + xMoved;
                    int newY = frameY + yMoved;
                    setLocation(newX, newY);
                }
            }
        });

    }

}
