package hakaary.app.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

import javax.swing.JPanel;

public class AppImageDisplay {

    private class AppImageDisplayCanvas extends Canvas {

        Image image;

        private double zoomFactor = 1.0;
        private int offsetX = 0;
        private int offsetY = 0;
        private int lastBaseRenderWidth = 0;
        private int lastBaseRenderHeight = 0;

        private double scrollAccumulator = 0.0;
        private static final double SCROLL_THRESHOLD = 0.25;

        public AppImageDisplayCanvas() {
            super();
            addMouseWheelListener(e -> {
                if (e.isControlDown()) {
                    if (image != null) {
                        double factor = (e.getWheelRotation() < 0) ? 1.15 : 1.0 / 1.15;
                        applyZoom(factor, e.getX(), e.getY());
                    }
                } else if (scrollPageFunc != null) {
                    scrollAccumulator += e.getPreciseWheelRotation();
                    if (scrollAccumulator >= SCROLL_THRESHOLD) {
                        scrollAccumulator = 0.0;
                        scrollPageFunc.accept(true);
                    } else if (scrollAccumulator <= -SCROLL_THRESHOLD) {
                        scrollAccumulator = 0.0;
                        scrollPageFunc.accept(false);
                    }
                }
            });
        }

        private void render(Graphics g) {
            int canvasWidth = getWidth();
            int canvasHeight = getHeight();

            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);

            double imgAspect = (double) imageWidth / imageHeight;

            int baseRenderWidth = canvasWidth;
            int baseRenderHeight = (int) (canvasWidth / imgAspect);

            if (baseRenderHeight > canvasHeight) {
                baseRenderHeight = canvasHeight;
                baseRenderWidth = (int) (canvasHeight * imgAspect);
            }

            lastBaseRenderWidth = baseRenderWidth;
            lastBaseRenderHeight = baseRenderHeight;

            int zoomedWidth = (int) (baseRenderWidth * zoomFactor);
            int zoomedHeight = (int) (baseRenderHeight * zoomFactor);

            int drawX = (canvasWidth - zoomedWidth) / 2 + offsetX;
            int drawY = (canvasHeight - zoomedHeight) / 2 + offsetY;

            g.clearRect(0, 0, canvasWidth, canvasHeight);
            g.drawImage(image, drawX, drawY, zoomedWidth, zoomedHeight, null);
        }

        private void applyZoom(double factor, int mouseX, int mouseY) {
            double oldZoom = zoomFactor;
            double newZoom = Math.max(1.0, oldZoom * factor);

            if (newZoom == oldZoom) return;

            int canvasWidth = getWidth();
            int canvasHeight = getHeight();

            int oldZoomedWidth  = (int) (lastBaseRenderWidth  * oldZoom);
            int oldZoomedHeight = (int) (lastBaseRenderHeight * oldZoom);
            int oldDrawX = (canvasWidth  - oldZoomedWidth)  / 2 + offsetX;
            int oldDrawY = (canvasHeight - oldZoomedHeight) / 2 + offsetY;

            int newZoomedWidth  = (int) (lastBaseRenderWidth  * newZoom);
            int newZoomedHeight = (int) (lastBaseRenderHeight * newZoom);
            int newDrawX = mouseX - (int) ((mouseX - oldDrawX) * newZoom / oldZoom);
            int newDrawY = mouseY - (int) ((mouseY - oldDrawY) * newZoom / oldZoom);

            zoomFactor = newZoom;
            offsetX = newDrawX - (canvasWidth  - newZoomedWidth)  / 2;
            offsetY = newDrawY - (canvasHeight - newZoomedHeight) / 2;

            if (zoomFactor == 1.0) {
                offsetX = 0;
                offsetY = 0;
            }

            repaint();
        }

        public void resetZoom() {
            zoomFactor = 1.0;
            offsetX = 0;
            offsetY = 0;
        }

        public void setCurrentImage(Image image) {
            this.image = image;
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            if (image != null) {
                render(g);
            }
        }

    }

    // Called with true = next page, false = prev page
    private Consumer<Boolean> scrollPageFunc;

    private final JPanel imageDisplay;
    private final AppImageDisplayCanvas canvas;

    public AppImageDisplay() {
        imageDisplay = new JPanel(new BorderLayout());

        canvas = new AppImageDisplayCanvas();
        canvas.setBackground(Color.BLACK);

        imageDisplay.add(canvas, BorderLayout.CENTER);
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
        canvas.resetZoom();
        canvas.setCurrentImage(image);
    }

    public void renderCurrentImage() {
        canvas.repaint();
    }

    public void setScrollPageFunc(Consumer<Boolean> function) {
        scrollPageFunc = function;
    }

}
