package me.lachlanap.physicsplayground.ui;

/**
 * Translates between the world's coordinates and view coordinates.
 */
class View {

    private static final double DEFAULT_PPM = 100;

    private double pixelsPerMetre;
    private double offsetPixelsX, offsetPixelsY;

    public View() {
        this.pixelsPerMetre = DEFAULT_PPM;

        this.offsetPixelsX = this.offsetPixelsY = 0;
    }

    public double absoluteToViewX(double metres) {
        return metres * pixelsPerMetre + offsetPixelsX;
    }

    public double absoluteToViewY(double metres) {
        return metres * pixelsPerMetre + offsetPixelsY;
    }

    public double relativeToView(double metres) {
        return metres * pixelsPerMetre;
    }

    public double getOffsetPixelsX() {
        return offsetPixelsX;
    }

    public double getOffsetPixelsY() {
        return offsetPixelsY;
    }

    public double getPixelsPerMetre() {
        return pixelsPerMetre;
    }

    public void setPixelOffset(double x, double y) {
        offsetPixelsX = x;
        offsetPixelsY = y;
    }

    public void setPixelsPerMetre(double pixelsPerMetre) {
        this.pixelsPerMetre = Math.max(1, pixelsPerMetre);
    }
}
