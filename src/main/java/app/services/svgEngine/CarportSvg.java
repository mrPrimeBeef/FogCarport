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
        int carportWidthCm = carport.getWidth();
        int carportHeightCm = carport.getHeight();

        Svg svg = new Svg(-100, -100, carportLengthCm + 100, carportHeightCm + 100);

        svg.addRectangle(-100, -100, carportLengthCm + 200, carportHeightCm + 200, "fill: white");
        svg.addDimension(0, 0, 0, carportHeightCm, OffsetDirection.LEFT);
        svg.addDimension(carportLengthCm, 0, carportLengthCm, carportHeightCm, OffsetDirection.RIGHT);
        svg.addLine(0, carportHeightCm, carportLengthCm, carportHeightCm, "stroke:black");

        // DRAWING REAL CARPORT FROM CALCULATION ENGINE
        List<Double> dimPointsX = new ArrayList<Double>();

        List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
        for (PlacedMaterial placedMaterial : placedMaterials) {
            double x = placedMaterial.getX();
            double y = placedMaterial.getY();
            double z = placedMaterial.getZ();
            float length = placedMaterial.getMaterial().getLengthCm();
            float width = placedMaterial.getMaterial().getWidthCm();
            String itemType = placedMaterial.getMaterial().getItemType();

            if (itemType.equalsIgnoreCase("stolpe")) {

                if (y > 0.5 * carportWidthCm) {
                    svg.addRectangle(x, z, length, width, "stroke:black; fill: white");
                    dimPointsX.add(x + 0.5 * length);
                }

            } else {
                svg.addRectangle(x, z, length, width, "stroke:black; fill: white");
            }
        }

        dimPointsX.add(0.0);
        dimPointsX.add(1.0 * carportLengthCm);
        Collections.sort(dimPointsX);
        for (int i = 0; i < dimPointsX.size() - 1; i++) {
            svg.addDimension(dimPointsX.get(i), carportHeightCm, dimPointsX.get(i + 1), carportHeightCm, OffsetDirection.DOWN);
        }




        System.out.println(dimPointsX);


        return svg.close();

    }


    public static String topView(Carport carport) throws SQLException {

        int carportLengthCm = carport.getLength();
        int carportWidthCm = carport.getWidth();

        Svg svg = new Svg(-100, -100, carportLengthCm + 100, carportWidthCm + 100);

        svg.addRectangle(-100, -100, carportLengthCm + 200, carportWidthCm + 200, "fill: white");
        svg.addDimension(0, carportWidthCm, carportLengthCm, carportWidthCm, OffsetDirection.DOWN);
        svg.addDimension(0, 0, 0, carportWidthCm, OffsetDirection.LEFT, 70);

        // DRAWING REAL CARPORT FROM CALCULATION ENGINE
        List<PlacedMaterial> placedMaterials = carport.getPlacedMaterials();
        for (PlacedMaterial placedMaterial : placedMaterials) {
            double x = placedMaterial.getX();
            double y = placedMaterial.getY();
            float length = placedMaterial.getMaterial().getLengthCm();
            float height = placedMaterial.getMaterial().getHeightCm();
            String itemType = placedMaterial.getMaterial().getItemType();

            if (itemType.equalsIgnoreCase("stolpe")) {
                svg.addRectangle(x, y, length, height, "stroke-width:2px; stroke:black; fill: none");
            } else {
                svg.addRectangle(x, y, length, height, "stroke:black; fill: white");
            }
        }

        List<Double> pillarY = new ArrayList<Double>();
        List<Double> raftersX = new ArrayList<Double>();
        double pillarDim = 0;
        for (PlacedMaterial placedMaterial : placedMaterials) {

            String itemType = placedMaterial.getMaterial().getItemType();
            if (itemType.equalsIgnoreCase("stolpe")) {
                pillarY.add(placedMaterial.getY());
                pillarDim = placedMaterial.getMaterial().getHeightCm();
            }

            if (itemType.equalsIgnoreCase("lægte")) {
                raftersX.add(placedMaterial.getX() + 0.5 * placedMaterial.getMaterial().getLengthCm());
            }
        }


        double min = Collections.min(pillarY);
        double max = Collections.max(pillarY) + pillarDim;
        System.out.println(min + " " + max);
        svg.addDimension(0, min, 0, max, OffsetDirection.LEFT, 40, "*");

        System.out.println("rafterX: " + raftersX);
        for (int i = 0; i < raftersX.size() - 1; i++) {
            svg.addDimension(raftersX.get(i), 0, raftersX.get(i + 1), 0, OffsetDirection.UP);
        }

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

        return svg.close();
    }

}