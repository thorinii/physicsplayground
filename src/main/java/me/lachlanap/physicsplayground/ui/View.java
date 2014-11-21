package me.lachlanap.physicsplayground.ui;

/**
 * Translates between the world's coordinates and view coordinates.
 */
class View {

    private static final double DEFAULT_PPM = 100;

    private double pixelsPerMetre;
    private double offsetX, offsetY;

    public View() {
        this.pixelsPerMetre = DEFAULT_PPM;

        this.offsetX = this.offsetY = 0;
    }
}
