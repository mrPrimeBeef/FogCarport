package app.services.svgEngine;

import java.sql.SQLException;
import java.util.List;

import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.StructureCalculationEngine.Entities.PlacedMaterial;

public class CarportSvg {

    public static String sideView(Carport carport) {

        int carportLengthCm = carport.getLength();
        int carportHeightCm = carport.getHeight();

        Svg svg = new Svg("-100", "-100", "100%", "-100 -100 " + (carportLengthCm + 200) + " " + (carportHeightCm + 200));
        svg.addRectangle(-100, -100, carportLengthCm + 200, carportHeightCm + 200, "fill: lightgreen");
        svg.addRectangle(0, 0, carportLengthCm, carportHeightCm, "stroke: black; fill: lightblue");

        svg.addDimensionLine(0, carportHeightCm + 50, carportLengthCm, carportHeightCm + 50);
        svg.addDimensionLine(-50, 0, -50, carportHeightCm);

        return svg.toString();

    }


    public static String topView(Carport carport) throws SQLException {

        int carportLengthCm = carport.getLength();
        int carportWidthCm = carport.getWidth();

        Svg svg = new Svg("-100", "-100", "100%", "-100 -100 " + (carportLengthCm + 200) + " " + (carportWidthCm + 200));
        svg.addRectangle(-100, -100, carportLengthCm + 200, carportWidthCm + 200, "fill: lightgreen");
        svg.addRectangle(0, 0, carportLengthCm, carportWidthCm, "stroke: black; fill: lightblue");

        svg.addDimensionLine(0, carportWidthCm + 50, carportLengthCm, carportWidthCm + 50);
        svg.addDimensionLine(-50, 0, -50, carportWidthCm);

        List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
        for (PlacedMaterial placedMaterial : placedMaterials) {
            double x = placedMaterial.getX();
            double y = placedMaterial.getY();
            float length = placedMaterial.getMaterial().getLengthCm();
            float height = placedMaterial.getMaterial().getHeightMm();

            if (placedMaterial.getMaterial().getItemType().equalsIgnoreCase("stolpe")) {
                svg.addRectangle(x, y, length, height, "stroke:black;fill: red");
            } else {
                svg.addRectangle(x, y, length, height, "stroke:black;fill: white");
            }

        }

//        svg.addText("7,80", carportLengthCm / 2 + 100, carportWidthCm + 140, 0);
//        svg.addText("6,00", 50, 50, -10);

//        // Spær
//        for (int x = 0; x < carportLengthCm; x += 55) {
//            svg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//        }
//
//        // Remme
//        svg.addRectangle(0, 35, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//        svg.addRectangle(0, 565, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//
//        // Stolper
//        svg.addRectangle(110, 35, 9.7, 9.7, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//
//        // Pile til mål i meter
//        svg.addDimensionLine(100, 100, 400, 400);
//
//        // Et hulbånd
//        svg.addLine(600, 300, 300, 500, "stroke:black; stroke-dasharray: 5 5");

        return svg.toString();
    }


}