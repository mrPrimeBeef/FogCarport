package app.services.svgEngine;

public class CarportSvg {

    public static String topView(int carportLengthCm, int carportWidthCm) {

        // outerSvg which is the size of the carport in cm plus 100 cm on each side for dimensions
        Svg outerSvg = new Svg("0", "0", "100%","0 0 " + (carportLengthCm+200) + " " + (carportWidthCm+200));
        outerSvg.addRectangle(0,0,carportLengthCm+200, carportWidthCm+200, "fill: green");
        outerSvg.addDimensionLine(100,carportWidthCm+150, carportLengthCm+100, carportWidthCm+150);


        // innerSvg which is the size of the carport in cm
        Svg innerSvg = new Svg("100", "100", ""+carportLengthCm,"0 0 " + carportLengthCm + " " + carportWidthCm);
        innerSvg.addRectangle(0,0,carportLengthCm, carportWidthCm, "fill: blue");

        // Spær
        for (int x = 0; x < carportLengthCm; x += 55) {
            innerSvg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        // Remme
        innerSvg.addRectangle(0, 35, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        innerSvg.addRectangle(0, 565, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Stolper
        innerSvg.addRectangle(110, 35, 9.7, 9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Mål i meter
        innerSvg.addText(20, 20, 0, "5,50m");

        // Pile til mål i meter
        innerSvg.addDimensionLine(100, 100, 400, 400);

        // En linje
        innerSvg.addLine(600, 300, 300, 500);


        // Tilføjelse af innerSvg til outerSvg
        outerSvg.addSvg(innerSvg);


        return outerSvg.toString();
    }
}
