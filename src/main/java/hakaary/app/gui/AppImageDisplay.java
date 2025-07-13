package hakaary.app.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.JButton;

public class AppImageDisplay {

    private class AppImageDisplayCanvas extends Canvas {

        Image image;

        public AppImageDisplayCanvas() {
            super();
        }

        public void drawImage() {
            int canvasWidth = getWidth();
            int canvasHeight = getHeight();

            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);

            double imgAspect = (double) imageWidth / imageHeight;

            int renderWidth = canvasWidth;
            int renderHeigth = (int) (canvasWidth / imgAspect);

            if (renderHeigth > canvasHeight) {
                renderHeigth = canvasHeight;
                renderWidth = (int) (canvasHeight * imgAspect);
            }

            getGraphics().clearRect(0, 0, getWidth(), getHeight());
            getGraphics().drawImage(
                    image,
                    (canvasWidth - renderWidth) / 2,
                    (canvasHeight - renderHeigth) / 2,
                    renderWidth,
                    renderHeigth,
                    null
            );
        }

        public void setCurrentImage(Image image) {
            this.image = image;
            drawImage();
        }

        @Override
        public void paint(Graphics g) {
            if (image != null) {
                drawImage();
            }
        }

    }

    private final JPanel imageDisplay;
    private final AppImageDisplayCanvas canvas;

    private final JButton buttonPreviousOnDisplay;
    private final JButton buttonNextOnDisplay;

    public AppImageDisplay(boolean buttonsOnDisplay) {
        imageDisplay = new JPanel(new BorderLayout());

        canvas = new AppImageDisplayCanvas();
        canvas.setBackground(Color.BLACK);

        buttonPreviousOnDisplay = new JButton("<");
        buttonNextOnDisplay = new JButton(">");

        if (!buttonsOnDisplay) {
            buttonPreviousOnDisplay.setVisible(false);
            buttonNextOnDisplay.setVisible(false);
        }

        imageDisplay.add(canvas, BorderLayout.CENTER);
        imageDisplay.add(buttonPreviousOnDisplay, BorderLayout.WEST);
        imageDisplay.add(buttonNextOnDisplay, BorderLayout.EAST);
    }

    public void addMouseListener(MouseAdapter mouseAdapter) {
        canvas.addMouseListener(mouseAdapter);
    }

    public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        canvas.addMouseMotionListener(mouseMotionListener);
    }

    public JPanel getImageDisplay() {
        return imageDisplay;
    }

    public void setCurrentImage(Image image) {
        canvas.setCurrentImage(image);
    }

    public void renderCurrentImage() {
        canvas.drawImage();
    }

    public void setButtonPreviousOnDispFunc(Runnable function) {
        buttonPreviousOnDisplay.addActionListener(
                e -> function.run()
        );
    }

    public void setButtonNextOnDispFunc(Runnable function) {
        buttonNextOnDisplay.addActionListener(
                e -> function.run()
        );
    }

}
