package app.services.svgEngine;

public class CarportSvg {

    public static String topView(int carportLengthCm, int carportWidthCm) {

        // outerSvg which is the size of the carport in cm plus 100 cm on each side for dimensions
        Svg outerSvg = new Svg("0", "0", "100%","0 0 " + (carportLengthCm+200) + " " + (carportWidthCm+200));
        outerSvg.addRectangle(0,0,carportLengthCm+200, carportWidthCm+200, "fill: green");
        outerSvg.addDimensionLine(100,carportWidthCm+150, carportLengthCm+100, carportWidthCm+150);
        outerSvg.addDimensionLine(50,100, 50, carportWidthCm+100);
        outerSvg.addText("7,80",carportLengthCm/2 + 100, carportWidthCm+140, 0);
        outerSvg.addText("6,00",50, 50, -10);

        // innerSvg which is the size of the carport in cm
        Svg innerSvg = new Svg("100", "100", ""+carportLengthCm,"0 0 " + carportLengthCm + " " + carportWidthCm);
        innerSvg.addRectangle(0,0,carportLengthCm, carportWidthCm, "fill: lightblue");

        // Spær
        for (int x = 0; x < carportLengthCm; x += 55) {
            innerSvg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        }

        // Remme
        innerSvg.addRectangle(0, 35, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        innerSvg.addRectangle(0, 565, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Stolper
        innerSvg.addRectangle(110, 35, 9.7, 9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Pile til mål i meter
        innerSvg.addDimensionLine(100, 100, 400, 400);

        // Et hulbånd
        innerSvg.addLine(600, 300, 300, 500,"stroke:black; stroke-dasharray: 5 5");

        outerSvg.addSvg(innerSvg);

        return outerSvg.toString();
    }
}
