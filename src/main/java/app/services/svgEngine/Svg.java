package app.services.svgEngine;

import java.util.Locale;

public class Svg {

    private static final String SVG_OPEN_TAG_TEMPLATE = "<svg version=\"1.1\" x=\"%d\" y=\"%d\" width=\"%s\" viewBox=\"%s\" preserveAspectRatio=\"xMinYMin\">";
    private static final String SVG_RECTANGLE_TEMPLATE = "<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"%s\"/>";
    private static final String SVG_TEXT_TEMPLATE = "<text x=\"%f\" y=\"%f\" text-anchor=\"middle\" alignment-baseline=\"middle\" transform=\"rotate(%f %f %f)\">%s</text>";
    private static final String SVG_LINE_TEMPLATE = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" style=\"%s\"/>";
    private static final String SVG_DIM_LINE_TEMPLATE = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" marker-start=\"url(#beginArrow)\" marker-end=\"url(#endArrow)\"/>";
    private static final String SVG_ARROW_DEFS = "<defs>\n" +
            "        <marker id=\"beginArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"0\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill:black;\" />\n" +
            "        </marker>\n" +
            "        <marker id=\"endArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"12\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,0 L12,6 L0,12 L0,0 \" style=\"fill:black;\" />\n" +
            "        </marker>\n" +
            "    </defs>";

    private static final double ARROW_HALF_WIDTH = 6;
    private static final double DIM_LINE_OFFSET_ORIGIN = 50;
    private static final double EXT_LINE_OFFSET_ORIGIN = 20;
    private static final double TEXT_OFFSET_DIM_LINE = 10;

    private static final Locale LOCALE_US = new Locale("us", "US");
    private static final Locale LOCALE_DK = new Locale("da", "DK");

    private final StringBuilder svg = new StringBuilder();

    public Svg(int xMin, int yMin, int xMax, int yMax) {
        String viewBox = String.format(LOCALE_US, "%d %d %d %d", xMin, yMin, xMax - xMin, yMax - yMin);
        svg.append(String.format(LOCALE_US, SVG_OPEN_TAG_TEMPLATE, xMin, yMin, "100%", viewBox));
        svg.append(SVG_ARROW_DEFS);
    }

    public String close() {
        return svg.append("</svg>").toString();
    }

    public void addRectangle(double x, double y, double width, double height, String style) {
        svg.append(String.format(LOCALE_US, SVG_RECTANGLE_TEMPLATE, x, y, width, height, style));
    }

    public void addLine(double x1, double y1, double x2, double y2, String style) {
        svg.append(String.format(LOCALE_US, SVG_LINE_TEMPLATE, x1, y1, x2, y2, style));
    }

    public void addDimension(double x1, double y1, double x2, double y2, OffsetDirection offsetDirection, double offsetDistance, String stars) {
        Line dimLine = new Line();
        Line extLine1 = new Line();
        Line extLine2 = new Line();

        double textX = Double.NaN;
        double textY = Double.NaN;
        double textRotation = Double.NaN;

        switch (offsetDirection) {
            case UP:
                dimLine = new Line(x1, y1 - offsetDistance, x2, y2 - offsetDistance);
                extLine1 = new Line(x1, y1 - EXT_LINE_OFFSET_ORIGIN, dimLine.getX1(), dimLine.getY1() - ARROW_HALF_WIDTH);
                extLine2 = new Line(x2, y2 - EXT_LINE_OFFSET_ORIGIN, dimLine.getX2(), dimLine.getY2() - ARROW_HALF_WIDTH);
                textX = 0.5 * (x1 + x2);
                textY = dimLine.getY1() - TEXT_OFFSET_DIM_LINE;
                textRotation = 0;
                break;

            case DOWN:
                dimLine = new Line(x1, y1 + offsetDistance, x2, y2 + offsetDistance);
                extLine1 = new Line(x1, y1 + EXT_LINE_OFFSET_ORIGIN, dimLine.getX1(), dimLine.getY1() + ARROW_HALF_WIDTH);
                extLine2 = new Line(x2, y2 + EXT_LINE_OFFSET_ORIGIN, dimLine.getX2(), dimLine.getY2() + ARROW_HALF_WIDTH);
                textX = 0.5 * (x1 + x2);
                textY = dimLine.getY1() - TEXT_OFFSET_DIM_LINE;
                textRotation = 0;
                break;

            case LEFT:
                dimLine = new Line(x1 - offsetDistance, y1, x2 - offsetDistance, y2);
                extLine1 = new Line(x1 - EXT_LINE_OFFSET_ORIGIN, y1, dimLine.getX1() - ARROW_HALF_WIDTH, dimLine.getY1());
                extLine2 = new Line(x2 - EXT_LINE_OFFSET_ORIGIN, y2, dimLine.getX2() - ARROW_HALF_WIDTH, dimLine.getY2());
                textX = dimLine.getX1() - TEXT_OFFSET_DIM_LINE;
                textY = 0.5 * (y1 + y2);
                textRotation = -90;
                break;

            case RIGHT:
                dimLine = new Line(x1 + offsetDistance, y1, x2 + offsetDistance, y2);
                extLine1 = new Line(x1 + EXT_LINE_OFFSET_ORIGIN, y1, dimLine.getX1() + ARROW_HALF_WIDTH, dimLine.getY1());
                extLine2 = new Line(x2 + EXT_LINE_OFFSET_ORIGIN, y2, dimLine.getX2() + ARROW_HALF_WIDTH, dimLine.getY2());
                textX = dimLine.getX1() - TEXT_OFFSET_DIM_LINE;
                textY = 0.5 * (y1 + y2);
                textRotation = -90;
                break;
        }

        addDimLine(dimLine.getX1(), dimLine.getY1(), dimLine.getX2(), dimLine.getY2());
        addLine(extLine1.getX1(), extLine1.getY1(), extLine1.getX2(), extLine1.getY2(), "stroke:black");
        addLine(extLine2.getX1(), extLine2.getY1(), extLine2.getX2(), extLine2.getY2(), "stroke:black");

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double lengthInMeter = 0.01 * Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        String text = String.format(LOCALE_DK, "%.2f", lengthInMeter) + stars;
        addText(text, textX, textY, textRotation);

    }

    public void addDimension(double x1, double y1, double x2, double y2, OffsetDirection offsetDirection, double offsetDistance) {
        addDimension(x1, y1, x2, y2, offsetDirection, offsetDistance, "");
    }

    public void addDimension(double x1, double y1, double x2, double y2, OffsetDirection offsetDirection) {
        addDimension(x1, y1, x2, y2, offsetDirection, DIM_LINE_OFFSET_ORIGIN, "");
    }

    private void addText(String text, double x, double y, double rotation) {
        svg.append(String.format(LOCALE_US, SVG_TEXT_TEMPLATE, x, y, rotation, x, y, text));
    }

    private void addDimLine(double x1, double y1, double x2, double y2) {
        svg.append(String.format(LOCALE_US, SVG_DIM_LINE_TEMPLATE, x1, y1, x2, y2));
    }

}