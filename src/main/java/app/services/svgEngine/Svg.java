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

    private static final double SPACING_DIM_TEXT = 10;
    private static final double SPACING_HELP_LINE = 20;
    private static final double SPACING_ARROW = 6;
    private static final double DEFAULT_OFFSET_DISTANCE = 50;

    private final StringBuilder svg = new StringBuilder();

    public Svg(String x, String y, String width, String viewBox) {
        Locale.setDefault(new Locale("US"));
        svg.append(String.format(SVG_OPEN_TAG_TEMPLATE, x, y, width, viewBox));
        svg.append(SVG_ARROW_DEFS);
    }

    public String close() {
        return svg.append("</svg>").toString();
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


    public void addDimension(double x1, double y1, double x2, double y2, OffsetDirection offsetDirection) {
        addDimension(x1, y1, x2, y2, offsetDirection, DEFAULT_OFFSET_DISTANCE, "");
    }

    public void addDimension(double x1, double y1, double x2, double y2, OffsetDirection offsetDirection, double offsetDistance) {
        addDimension(x1, y1, x2, y2, offsetDirection, offsetDistance, "");
    }

    public void addDimension(double x1, double y1, double x2, double y2, OffsetDirection offsetDirection, double offsetDistance, String stars) {

        Line dimLine = new Line();
        Line extLine1 = new Line();
        Line extLine2 = new Line();

        double textX = 0;
        double textY = 0;
        double textRotation = 0;


        if (offsetDirection == OffsetDirection.DOWN) {
            dimLine = new Line(x1, y1 + offsetDistance, x2, y2 + offsetDistance);
            extLine1 = new Line(x1, y1 + SPACING_HELP_LINE, dimLine.x1, dimLine.y1 + SPACING_ARROW);
            extLine2 = new Line(x2, y2 + SPACING_HELP_LINE, dimLine.x2, dimLine.y2 + SPACING_ARROW);

            textX = 0.5 * (x1 + x2);
            textY = dimLine.y1 - SPACING_DIM_TEXT;
        }
        if (offsetDirection == OffsetDirection.UP) {
            dimLine = new Line(x1, y1 - offsetDistance, x2, y2 - offsetDistance);
            extLine1 = new Line(x1, y1 - SPACING_HELP_LINE, dimLine.x1, dimLine.y1 - SPACING_ARROW);
            extLine2 = new Line(x2, y2 - SPACING_HELP_LINE, dimLine.x2, dimLine.y2 - SPACING_ARROW);

            textX = 0.5 * (x1 + x2);
            textY = dimLine.y1 - SPACING_DIM_TEXT;
        }

        if (offsetDirection == OffsetDirection.LEFT) {

            dimLine = new Line(x1 - offsetDistance, y1, x2 - offsetDistance, y2);
            extLine1 = new Line(x1 - SPACING_HELP_LINE, y1, dimLine.x1 - SPACING_ARROW, dimLine.y1);
            extLine2 = new Line(x2 - SPACING_HELP_LINE, y2, dimLine.x2 - SPACING_ARROW, dimLine.y2);

            textX = dimLine.x1 - SPACING_DIM_TEXT;
            textY = 0.5 * (y1 + y2);
            textRotation = -90;
        }

        if (offsetDirection == OffsetDirection.RIGHT) {

            dimLine = new Line(x1 + offsetDistance, y1, x2 + offsetDistance, y2);
            extLine1 = new Line(x1 + SPACING_HELP_LINE, y1, dimLine.x1 + SPACING_ARROW, dimLine.y1);
            extLine2 = new Line(x2 + SPACING_HELP_LINE, y2, dimLine.x2 + SPACING_ARROW, dimLine.y2);

            textX = dimLine.x1 - SPACING_DIM_TEXT;
            textY = 0.5 * (y1 + y2);

            textRotation = -90;

        }

        addDimensionLine(dimLine.x1, dimLine.y1, dimLine.x2, dimLine.y2);
        addLine(extLine1.x1, extLine1.y1, extLine1.x2, extLine1.y2, "stroke: black");
        addLine(extLine2.x1, extLine2.y1, extLine2.x2, extLine2.y2, "stroke: black");

        double distanceInMeter = 0.01 * calculateDistance(x1, y1, x2, y2);
        String text = String.format("%.2f", distanceInMeter) + stars;
        addText(text, textX, textY, textRotation);

    }

    private static double calculateDistance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }


}