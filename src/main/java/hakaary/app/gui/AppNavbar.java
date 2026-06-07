package hakaary.app.gui;

import java.util.function.IntConsumer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;

class AppNavbar {

    private final JPanel navbarPanel;

    private final JComboBox<Integer> cbChapter;
    private boolean runCbChapterFunc;

    private final JLabel txtChapter;
    private final JLabel txtPage;

    private final JButton buttonClose;
    private final JButton buttonPrevious;
    private final JButton buttonNext;

    private final JSlider pageSlider;
    private boolean runSliderFunc = true;
    private ChangeListener sliderChangeListener;

    public AppNavbar() {
        navbarPanel = new JPanel(new BorderLayout());

        pageSlider = new JSlider(1, 1, 1);
        pageSlider.setFocusable(false);

        JPanel controlsPanel = new JPanel();
        txtChapter = new JLabel("Chapter:");
        cbChapter = new JComboBox<Integer>();
        runCbChapterFunc = true;
        txtPage = new JLabel();

        buttonClose = new JButton("Close");
        buttonPrevious = new JButton("< Prev");
        buttonNext = new JButton("Next >");

        controlsPanel.add(txtChapter);
        controlsPanel.add(cbChapter);
        controlsPanel.add(buttonClose);
        controlsPanel.add(buttonPrevious);
        controlsPanel.add(buttonNext);
        controlsPanel.add(txtPage);

        navbarPanel.add(pageSlider, BorderLayout.NORTH);
        navbarPanel.add(controlsPanel, BorderLayout.CENTER);
    }

    public JPanel getNavbarPanel() {
        return navbarPanel;
    }

    public void setNewChapter(int chapter) {
        cbChapter.addItem(chapter);
    }

    public void setTxtPage(int page, int totalPages) {
        txtPage.setText(page + "/" + totalPages);

        runSliderFunc = false;
        pageSlider.setMaximum(totalPages);
        pageSlider.setValue(page);
        runSliderFunc = true;
    }

    public void setCurrentChapterCbBox(int chapter, boolean triggerFunc) {
        runCbChapterFunc = triggerFunc;
        cbChapter.setSelectedItem(chapter);
        runCbChapterFunc = true;
    }

    public Integer getCurrentSelectedChapter() {
        return (Integer) cbChapter.getItemAt(cbChapter.getSelectedIndex());
    }

    public void setCbChapterFunc(Runnable function) {
        cbChapter.addItemListener(e -> {
            if (runCbChapterFunc) {
                function.run();
            }
        });
    }

    // Callback receives 0-based page index
    public void setSliderPageFunc(IntConsumer function) {
        sliderChangeListener = e -> {
            if (runSliderFunc && !pageSlider.getValueIsAdjusting()) {
                function.accept(pageSlider.getValue() - 1);
            }
        };
        pageSlider.addChangeListener(sliderChangeListener);
    }

    public void setButtonCloseFunc(Runnable function) {
        buttonClose.addActionListener(e -> function.run());
    }

    public void setButtonPreviousFunc(Runnable function) {
        buttonPrevious.addActionListener(e -> function.run());
    }

    public void setButtonNextFunc(Runnable function) {
        buttonNext.addActionListener(e -> function.run());
    }

}
