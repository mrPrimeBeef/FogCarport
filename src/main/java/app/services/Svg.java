package app.services;

public class Svg {

    private static final String SVG_TEMPLATE = "";
    private StringBuilder svg = new StringBuilder();

    public Svg(String viewBox, String width, String height) {
        svg.append("");
//        String.format("%f tester %s tester", 100.0, "nej");


    }

    public Svg(int x, int y, String viewBox, String width, String height) {
    }

    public void addRectangle(int x, int y, double height, double width, String style) {
    }

    public void addLine(int x1, int y1, int x2, int y2, String style) {
    }

    public void addArrow(int x1, int y1, int x2, int y2, String style) {
    }

    public void addText(int x, int y, int rotation, String text) {
    }

    public void addSvg(Svg innerSvg) {
    }

    public String toString() {
        return null;
    }

}
