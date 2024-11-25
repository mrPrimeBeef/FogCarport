package app.services.svgEngine;

import java.util.Locale;

public class Svg {

    private static final String SVG_TEMPLATE = "<svg version=\"1.1\" x=\"%s\" y=\"%s\" width=\"%s\" viewBox=\"%s\" preserveAspectRatio=\"xMinYMin\">";

    private static final String SVG_ARROW_DEFS = "<defs>\n" +
            "        <marker id=\"beginArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"0\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill: #000000;\" />\n" +
            "        </marker>\n" +
            "        <marker id=\"endArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"12\" refY=\"6\" orient=\"auto\">\n" +
            "            <path d=\"M0,0 L12,6 L0,12 L0,0 \" style=\"fill: #000000;\" />\n" +
            "        </marker>\n" +
            "    </defs>";

    private static final String SVG_RECT_TEMPLATE = "<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" style=\"%s\"/>";

    private static final String SVG_LINE_TEMPLATE = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\"/>";

    private static final String SVG_ARROW_TEMPLATE = "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"black\" marker-start=\"url(#beginArrow)\" marker-end=\"url(#endArrow)\"/>";

    private static final String SVG_TEXT_TEMPLATE = "<text x=\"%f\" y=\"%f\">%s</text>";

    private StringBuilder svgString = new StringBuilder();

    public Svg(String x, String y, String width, String viewBox) {
        Locale.setDefault(new Locale("US"));
        svgString.append(String.format(SVG_TEMPLATE, x, y, width, viewBox));
        svgString.append(SVG_ARROW_DEFS);
    }

    public void addRectangle(double x, double y, double width, double height, String style) {
        svgString.append(String.format(SVG_RECT_TEMPLATE, x, y, width, height, style));
    }

    public void addLine(double x1, double y1, double x2, double y2) {
        svgString.append(String.format(SVG_LINE_TEMPLATE, x1, y1, x2, y2));
    }

    public void addDimensionLine(double x1, double y1, double x2, double y2) {
        svgString.append(String.format(SVG_ARROW_TEMPLATE, x1, y1, x2, y2));
    }

    public void addText(double x, double y, int rotation, String text) {
        svgString.append(String.format(SVG_TEXT_TEMPLATE, x, y, text));
    }

    public void addSvg(Svg innerSvg) {
        svgString.append(innerSvg.toString());
    }

    public String toString() {

        return svgString.append("</svg>").toString();
    }

}
