package app.services.svgEngine;

public class CarportSvg {

    public static String topView(int carportLengthCm, int carportWidthCm) {

        String viewBox = "0 0 " + carportLengthCm + " " + carportWidthCm;
        Svg svg = new Svg(0, 0, viewBox, "100%");

        // Spær
        for (int x = 0; x < carportLengthCm; x += 55) {
            svg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        // Remme
        svg.addRectangle(0, 35, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        svg.addRectangle(0, 565, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Stolper
        svg.addRectangle(110, 35, 9.7, 9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Mål i meter
        svg.addText(20, 20, 0, "5,50m");

        // Pile til mål i meter
        svg.addArrow(100, 100, 400, 400);

        // En linje
        svg.addLine(600, 300, 300, 500);


        // Tilføjelse af indre SVG
        Svg innerSvg = new Svg(200, 50, "0 0 400 400", "100%");
        innerSvg.addLine(0,0,400,400);
        innerSvg.addLine(400,0,0,400);
        svg.addSvg(innerSvg);

        return svg.toString();
    }
}
