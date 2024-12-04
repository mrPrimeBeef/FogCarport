package app.services.svgEngine;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.StructureCalculationEngine.Entities.PlacedMaterial;

public class CarportSvg {

    public static String sideView(Carport carport) throws SQLException {

        int carportLengthCm = carport.getLength();
        int carportHeightCm = carport.getHeight();

        Svg svg = new Svg(-100, -100, carportLengthCm + 100, carportHeightCm + 100);
        svg.addRectangle(-100, -100, carportLengthCm + 200, carportHeightCm + 200, "fill: lightgreen");
        svg.addRectangle(0, 0, carportLengthCm, carportHeightCm, "fill: white");

        svg.addDimension(0, 0, 0, carportHeightCm, OffsetDirection.LEFT);
        svg.addDimension(carportLengthCm, 0, carportLengthCm, carportHeightCm, OffsetDirection.RIGHT);

        System.out.println("--- SVG sideView placedMaterials ---");

        // DRAWING REAL CARPORT FROM CALCULATION ENGINE
        List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
        for (PlacedMaterial placedMaterial : placedMaterials) {
            double x = placedMaterial.getX();
            double z = placedMaterial.getZ();
            float length = placedMaterial.getMaterial().getLengthCm();
            float width = placedMaterial.getMaterial().getWidthCm();
            String itemType = placedMaterial.getMaterial().getItemType();

            System.out.println("itemType: " + itemType);

            if (itemType.equalsIgnoreCase("stolpe")) {
                svg.addRectangle(x, z, length, width, "stroke:black;fill: white");
            } else {
                svg.addRectangle(x, z, length, width, "stroke:black;fill: white");
            }
        }

        // DRAW DIMENSIONS FOR COLUMNS
        List<Double> columnX = new ArrayList<Double>();
        for (PlacedMaterial placedMaterial : placedMaterials) {
            String itemType = placedMaterial.getMaterial().getItemType();

            if (itemType.equalsIgnoreCase("stolpe")) {
                columnX.add(placedMaterial.getX());
            }
        }
        System.out.println(columnX);


        return svg.close();

    }


    public static String topView(Carport carport) throws SQLException {

        int carportLengthCm = carport.getLength();
        int carportWidthCm = carport.getWidth();

        Svg svg = new Svg(-100, -100, carportLengthCm + 100, carportWidthCm + 100);
        svg.addRectangle(-100, -100, carportLengthCm + 200, carportWidthCm + 200, "fill: lightgreen");
        svg.addRectangle(0, 0, carportLengthCm, carportWidthCm, "fill: white");

        svg.addDimension(0, carportWidthCm, carportLengthCm, carportWidthCm, OffsetDirection.DOWN);
        svg.addDimension(0, 0, 0, carportWidthCm, OffsetDirection.LEFT, 70);


        System.out.println("--- SVG topView placedMaterials ---");

        // DRAWING REAL CARPORT FROM CALCULATION ENGINE
        List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
        for (PlacedMaterial placedMaterial : placedMaterials) {
            double x = placedMaterial.getX();
            double y = placedMaterial.getY();
            float length = placedMaterial.getMaterial().getLengthCm();
            float height = placedMaterial.getMaterial().getHeightCm();
            String itemType = placedMaterial.getMaterial().getItemType();

            System.out.println("itemType: " + itemType);

            if (itemType.equalsIgnoreCase("stolpe")) {
                svg.addRectangle(x, y, length, height, "stroke:black;fill: none");
            } else {
                svg.addRectangle(x, y, length, height, "stroke:black;fill: white");
            }
        }

        List<Double> pillarY = new ArrayList<Double>();
        double pillarDim = 0;
        for (PlacedMaterial placedMaterial : placedMaterials) {

            String itemType = placedMaterial.getMaterial().getItemType();
            if (itemType.equalsIgnoreCase("stolpe")) {
                pillarY.add(placedMaterial.getY());
                pillarDim = placedMaterial.getMaterial().getHeightCm();
            }
        }
        System.out.println(pillarY);
        double min = Collections.min(pillarY);
        double max = Collections.max(pillarY) + pillarDim;
        System.out.println(min + " " + max);
        svg.addDimension(0, min, 0, max, OffsetDirection.LEFT, 40, "*");

        double strap1X1 = carport.getFixatingStrapListXY().get(0).get(0);
        double strap1Y1 = carport.getFixatingStrapListXY().get(0).get(1);
        double strap1X2 = carport.getFixatingStrapListXY().get(0).get(2);
        double strap1Y2 = carport.getFixatingStrapListXY().get(0).get(3);
        double strap2X1 = carport.getFixatingStrapListXY().get(1).get(0);
        double strap2Y1 = carport.getFixatingStrapListXY().get(1).get(1);
        double strap2X2 = carport.getFixatingStrapListXY().get(1).get(2);
        double strap2Y2 = carport.getFixatingStrapListXY().get(1).get(3);
        svg.addLine(strap1X1, strap1Y1, strap1X2, strap1Y2, "stroke:black; stroke-dasharray: 5 5");
        svg.addLine(strap2X1, strap2Y1, strap2X2, strap2Y2, "stroke:black; stroke-dasharray: 5 5");

//        // Hardcoded Spær og deres dimensions
//        for (int x = 0; x < carportLengthCm; x += 55) {
//            svg.addRectangle(x, 0, 4.5, carportWidthCm, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//            svg.addDimension(x, 0, x + 55, 0, OffsetDirection.UP);
//        }
//
//        // Hardcoded Remme
//        svg.addRectangle(0, 35, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//        svg.addRectangle(0, 565, carportLengthCm, 4.5, "stroke-width:1px; stroke:#000000; fill: #ffffff");
//
//        // Hardcoded Stolper
//        svg.addRectangle(110, 35, 9.7, 9.7, "stroke-width:2px; stroke:black; fill: none");

        return svg.close();
    }

}