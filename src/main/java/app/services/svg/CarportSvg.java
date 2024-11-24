package app.services.svg;

public class CarportSvg {

    public static String topView(int carportLengthCm, int carportWidthCm) {




        String viewBox = "0 0 " + carportLengthCm + " " + carportWidthCm;
        Svg svg = new Svg(0, 0, viewBox, "100%");

        // Spær
        for (int x = 0; x < carportLengthCm; x += 55) {
            svg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        // Remme
        svg.addRectangle(0,35,carportLengthCm,4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        svg.addRectangle(0,565,carportLengthCm,4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Stolper
        svg.addRectangle(110,35,9.7,9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        return svg.toString();
    }
}
