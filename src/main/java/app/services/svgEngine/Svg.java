package app.services.svgEngine;

import java.util.Locale;

public class Svg {

    private static final String SVG_OPEN_TAG_TEMPLATE = "<svg version=\"1.1\" x=\"%s\" y=\"%s\" width=\"%s\" viewBox=\"%s\" preserveAspectRatio=\"xMinYMin\">";
    private static final String SVG_RECTANGLE_TEMPLATE = "<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"%s\"/>";
    private static final String SVG_TEXT_TEMPLATE = "<text x=\"%f\" y=\"%f\" text-anchor=\"middle\" alignment-baseline=\"middle\" transform=\"rotate(%f %f %f)\">%s</text>";
    private static final String SVG_LINE_TEMPLATE = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" style=\"%s\"/>";
    private static final String SVG_DIMENSION_LINE_TEMPLATE = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" marker-start=\"url(#beginArrow)\" marker-end=\"url(#endArrow)\"/>";
    private static final String SVG_ARROW_DEFS = "<defs>\n" +
            "        <marker id=\"beginArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"0\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill: #000000;\" />\n" +
            "        </marker>\n" +
            "        <marker id=\"endArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"12\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,0 L12,6 L0,12 L0,0 \" style=\"fill: #000000;\" />\n" +
            "        </marker>\n" +
            "    </defs>";

    private static final double SPACING_DIM_LINE = 50;
    private static final double SPACING_DIM_TEXT = 10;

    private final StringBuilder svg = new StringBuilder();

    public Svg(String x, String y, String width, String viewBox) {
        Locale.setDefault(new Locale("US"));
        svg.append(String.format(SVG_OPEN_TAG_TEMPLATE, x, y, width, viewBox));
        svg.append(SVG_ARROW_DEFS);
    }

    public String addRectangle(double x, double y, double width, double height, String style) {
        String s = String.format(SVG_RECTANGLE_TEMPLATE, x, y, width, height, style);
        svg.append(s);
        return s;
    }

    public String addText(String text, double x, double y, double rotation) {
        String s = String.format(SVG_TEXT_TEMPLATE, x, y, rotation, x, y, text);
        svg.append(s);
        return s;
    }

    public String addLine(double x1, double y1, double x2, double y2, String style) {
        String s = String.format(SVG_LINE_TEMPLATE, x1, y1, x2, y2, style);
        svg.append(s);
        return s;
    }

    public String addDimensionLine(double x1, double y1, double x2, double y2) {
        String s = String.format(SVG_DIMENSION_LINE_TEMPLATE, x1, y1, x2, y2);
        svg.append(s);
        return s;
    }

    public void addDimension(double x1, double y1, double x2, double y2, Direction direction) {

        double distanceInMeter = 0.01*calculateDistance(x1, y1, x2, y2);

        double degrees = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));

        double X1 = 0;
        double Y1 = 0;
        double X2 = 0;
        double Y2 = 0;
        double xText = 0;
        double yText = 0;
        double rotationText = 0;

        if (direction == Direction.DOWN) {
            X1 = x1;
            Y1 = y1 + SPACING_DIM_LINE;
            X2 = x2;
            Y2 = y2 + SPACING_DIM_LINE;
            xText = 0.5*(x1+x2);
            yText = Y1 - SPACING_DIM_TEXT;
            rotationText = 0;
        }
        if (direction == Direction.UP) {
            X1 = x1;
            Y1 = y1 - SPACING_DIM_LINE;
            X2 = x2;
            Y2 = y2 - SPACING_DIM_LINE;
            xText = 0.5*(x1+x2);
            yText = Y1 - SPACING_DIM_TEXT;
            rotationText = 0;
        }

        if (direction == Direction.LEFT) {
            X1 = x1 - SPACING_DIM_LINE;
            Y1 = y1;
            X2 = x2 - SPACING_DIM_LINE;
            Y2 = y2;
            xText = X1 - SPACING_DIM_TEXT;
            yText = 0.5*(y1+y2);
            rotationText = -90;
        }

        if (direction == Direction.RIGHT) {
            X1 = x1 + SPACING_DIM_LINE;
            Y1 = y1;
            X2 = x2 + SPACING_DIM_LINE;
            Y2 = y2;
            xText = X1 - SPACING_DIM_TEXT;
            yText = 0.5*(y1+y2);
            rotationText = -90;
        }

        addText(String.format("%.2f", distanceInMeter), xText, yText, rotationText);

        addDimensionLine(X1, Y1, X2, Y2);

    }

    public String close() {
        return svg.append("</svg>").toString();
    }


    private static double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }


}