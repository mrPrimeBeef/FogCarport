package app.services.svgEngine;

import java.sql.SQLException;

import app.services.StructureCalculationEngine.Entities.Carport;

public class CarportSvg {

    private static final int SPACING = 100;

    public static String sideView(Carport carport) {

        int carportLengthCm = carport.getLength();
        int carportHeightCm = carport.getHeight();


        String viewBox = String.format("%d %d %d %d", -SPACING, -SPACING, carportLengthCm + 2 * SPACING, carportHeightCm + 2 * SPACING);
        Svg svg = new Svg(-SPACING, -SPACING, "100%", viewBox);
        svg.addRectangle(-100, -100, carportLengthCm + 200, carportHeightCm + 200, "fill: lightgreen");
        svg.addRectangle(0, 0, carportLengthCm, carportHeightCm, "fill: white");

        svg.addDimension(0, 0, 0, carportHeightCm, OffsetDirection.LEFT, 70);
        svg.addDimension(0, 30, 0, carportHeightCm, OffsetDirection.LEFT, 30, "***");
        svg.addDimension(carportLengthCm, 0, carportLengthCm, carportHeightCm, OffsetDirection.RIGHT);
        svg.addDimension(carportLengthCm, carportHeightCm, 0, carportHeightCm, OffsetDirection.DOWN);

        return svg.close();

    }


    public static String topView(Carport carport) throws SQLException {

        int carportLengthCm = carport.getLength();
        int carportWidthCm = carport.getWidth();

        String viewBox = String.format("%d %d %d %d", -SPACING, -SPACING, carportLengthCm + 2 * SPACING, carportWidthCm + 2 * SPACING);
        // Svg svg = new Svg(-SPACING, -SPACING, "100%", "-100 -100 " + (carportLengthCm + 200) + " " + (carportWidthCm + 200));
        Svg svg = new Svg(-SPACING, -SPACING, "100%", viewBox);
        svg.addRectangle(-100, -100, carportLengthCm + 200, carportWidthCm + 200, "fill: lightgreen");
        svg.addRectangle(0, 0, carportLengthCm, carportWidthCm, "fill: white");

        svg.addDimension(0, carportWidthCm, carportLengthCm, carportWidthCm, OffsetDirection.DOWN);
        svg.addDimension(0, carportWidthCm, 0, 0, OffsetDirection.LEFT, 70);
        svg.addDimension(0, carportWidthCm - 35, 0, 35, OffsetDirection.LEFT, 30, "*");

//        // DRAWING REAL CARPORT FROM CALCULATION ENGINE
//        List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
//        for (PlacedMaterial placedMaterial : placedMaterials) {
//            double x = placedMaterial.getX();
//            double y = placedMaterial.getY();
//            float length = placedMaterial.getMaterial().getLengthCm();
//            float height = placedMaterial.getMaterial().getHeightMm();
//            String itemType = placedMaterial.getMaterial().getItemType();
//
//            if (itemType.equalsIgnoreCase("stolpe")) {
//                svg.addRectangle(x, y, length, height, "stroke:black;fill: red");
//            } else {
//                svg.addRectangle(x, y, length, height, "stroke:black;fill: white");
//            }
//        }


        // Hardcoded Spær og deres dimensions
        for (int x = 0; x < carportLengthCm; x += 55) {
            svg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
            svg.addDimension(x, 0, x + 55, 0, OffsetDirection.UP);
        }

        // Hardcoded Remme
        svg.addRectangle(0, 35, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
        svg.addRectangle(0, 565, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");

        // Hardcoded Stolper
        svg.addRectangle(110, 35, 9.7, 9.7, "stroke-width:2px; stroke:black; fill: none");

        // Hardcoded hulbånd
        svg.addLine(120, 35, 745, 565, "stroke:black; stroke-dasharray: 5 5");

        return svg.close();
    }


}