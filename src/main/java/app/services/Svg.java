package app.services;

public class Svg {

    private static final String SVG_TEMPLATE = "<svg version=\"1.1\"\n" +
            "x=\"%d\" y=\"%d\"\n" +
            "viewBox=\"%s\" width=\"%s\" \n" +
            "height=\"%s\" preserveAspectRatio=\"xMinYMin\">";

    private static final String SVG_ARROW_DEFS = "";

    private static final String SVG_RECT_TEMPLATE = "<rect x=\"%d\" y=\"%d\" width=\"%f\" height=\"%f\" style=\"%s\"/>";

    private static final String SVG_LINE_TEMPLATE = "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"black\"/>";

    private StringBuilder svg = new StringBuilder();

//    public Svg(String viewBox, String width, String height) {
//        svg.append("");

    /// /        String.format("%f tester %s tester", 100.0, "nej");
//
//
//    }
    public Svg(int x, int y, String viewBox, String width, String height) {
        svg.append(String.format(SVG_TEMPLATE, x, y, viewBox, width, height));
        svg.append(SVG_ARROW_DEFS);
    }

    public void addRectangle(int x, int y, double width, double height, String style) {
        svg.append(String.format(SVG_RECT_TEMPLATE, x, y, width, height, style));
    }

    public void addLine(int x1, int y1, int x2, int y2, String style) {
        svg.append(String.format(SVG_LINE_TEMPLATE, x1, y1, x2, y2, style));
    }

    public void addArrow(int x1, int y1, int x2, int y2, String style) {
        // Kald addLine med en style der indeholder pilehoveder
    }

    public void addText(int x, int y, int rotation, String text) {
    }

    public void addSvg(Svg innerSvg) {
        svg.append(innerSvg.toString());
    }

    public String toString() {

        return svg.append("</svg>").toString();
    }

}
