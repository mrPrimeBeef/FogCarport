package app.services.svgEngine;

import java.util.Locale;

public class Svg {

    private static final String SVG_START_TAG_TEMPLATE = "<svg version=\"1.1\" x=\"%s\" y=\"%s\" width=\"%s\" viewBox=\"%s\" preserveAspectRatio=\"xMinYMin\">";
    private static final String SVG_RECTANGLE_TEMPLATE = "<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"%s\"/>";
    private static final String SVG_TEXT_TEMPLATE = "<text x=\"%f\" y=\"%f\" text-anchor=\"middle\" alignment-baseline=\"middle\" transform=\"rotate(%d %f %f)\">%s</text>";
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

    private StringBuilder svg = new StringBuilder();

    public Svg(String x, String y, String width, String viewBox) {
        Locale.setDefault(new Locale("US"));
        svg.append(String.format(SVG_START_TAG_TEMPLATE, x, y, width, viewBox));
        svg.append(SVG_ARROW_DEFS);
    }

    public void addRectangle(double x, double y, double width, double height, String style) {
        svg.append(String.format(SVG_RECTANGLE_TEMPLATE, x, y, width, height, style));
    }

    public String addText(String text, double x, double y, int rotation) {
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

    public void addSvg(Svg innerSvg) {
        svg.append(innerSvg.toString());
    }

    @Override
    public String toString() {
        return svg.append("</svg>").toString();
    }

}