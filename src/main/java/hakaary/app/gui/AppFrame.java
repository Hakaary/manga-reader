package hakaary.app.gui;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.formdev.flatlaf.FlatDarculaLaf;

import hakaary.app.reader.PageManager;

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
        // Set the current chapter from PageManager
        navbar.setCbChapterFunc(() -> {
            PageManager.setCurrentChapter(getCurrentSelectedChapter());
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
            PageManager.prefetchAdjacent();
        });

        // Page slider
        navbar.setSliderPageFunc(pageIdx -> {
            PageManager.jumpToPage(pageIdx);
            setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
            PageManager.prefetchAdjacent();
        });

        // Close button
        navbar.setButtonCloseFunc(() -> {
            dispose();
            System.exit(0);
        });

        // Navbar previous button
        navbar.setButtonPreviousFunc(() -> {
            PageManager.setPrevPage();
            setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
            PageManager.prefetchAdjacent();
        });

        // Navbar next button
        navbar.setButtonNextFunc(() -> {
            PageManager.setNextPage();
            setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
            setTxtPage(
                    PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter()
            );
            setCurrentImage();
            PageManager.prefetchAdjacent();
        });

        // Keyboard navigation: n = prev, m = next, f = fullscreen
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('n'), "prevPage");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('m'), "nextPage");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke('f'), "toggleFullscreen");

        getRootPane().getActionMap().put("prevPage", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PageManager.setPrevPage();
                setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
                setTxtPage(PageManager.getCurrentPageIdx() + 1,
                        PageManager.getNumPagesCurrentChapter());
                setCurrentImage();
                PageManager.prefetchAdjacent();
            }
        });
        getRootPane().getActionMap().put("nextPage", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                PageManager.setNextPage();
                setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
                setTxtPage(PageManager.getCurrentPageIdx() + 1,
                        PageManager.getNumPagesCurrentChapter());
                setCurrentImage();
                PageManager.prefetchAdjacent();
            }
        });
        getRootPane().getActionMap().put("toggleFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleFullscreen();
            }
        });

        // Scroll navigation: scroll down = next, scroll up = prev
        imageDisplay.setScrollPageFunc(isNext -> {
            if (isNext) PageManager.setNextPage(); else PageManager.setPrevPage();
            setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
            setTxtPage(PageManager.getCurrentPageIdx() + 1,
                    PageManager.getNumPagesCurrentChapter());
            setCurrentImage();
            PageManager.prefetchAdjacent();
        });

        // Click navigation + fullscreen toggle
        final Timer[] singleClickTimer = {null};

        imageDisplay.addMouseListener(
                new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;

                if (e.getClickCount() == 2) {
                    if (singleClickTimer[0] != null) {
                        singleClickTimer[0].stop();
                        singleClickTimer[0] = null;
                    }
                    toggleFullscreen();
                } else if (e.getClickCount() == 1) {
                    boolean isRightSide = e.getX() >= e.getComponent().getWidth() / 2;

                    singleClickTimer[0] = new Timer(250, evt -> {
                        singleClickTimer[0] = null;
                        if (isRightSide) {
                            PageManager.setNextPage();
                        } else {
                            PageManager.setPrevPage();
                        }
                        setCurrentChapterCbBox(PageManager.getCurrentChapter(), false);
                        setTxtPage(
                                PageManager.getCurrentPageIdx() + 1,
                                PageManager.getNumPagesCurrentChapter()
                        );
                        setCurrentImage();
                        PageManager.prefetchAdjacent();
                    });
                    singleClickTimer[0].setRepeats(false);
                    singleClickTimer[0].start();
                }
            }
        });

        // Dragging the frame
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
