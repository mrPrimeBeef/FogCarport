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

        svg.addRectangle(-100, -100, carportLengthCm + 200, carportHeightCm + 200, "fill:white");
        svg.addDimension(0, 0, 0, carportHeightCm, OffsetDirection.LEFT);
        svg.addDimension(carportLengthCm, 0, carportLengthCm, carportHeightCm, OffsetDirection.RIGHT);
        svg.addLine(0, carportHeightCm, carportLengthCm, carportHeightCm, "stroke:black");

        List<Double> dimPointsX = new ArrayList<Double>();
        dimPointsX.add(0.0);


        for (PlacedMaterial placedMaterial : carport.getPlacedMaterials()) {
            double x = placedMaterial.getX();
            double y = placedMaterial.getY();
            double z = placedMaterial.getZ();
            double xSize = placedMaterial.getMaterial().getLengthCm();
            double zSize = placedMaterial.getMaterial().getWidthCm();
            String itemType = placedMaterial.getMaterial().getItemType();

            svg.addRectangle(x, z, xSize, zSize, "stroke:black; fill:white");

            if (itemType.equalsIgnoreCase("stolpe") && y > carportWidthCm / 2) {
                dimPointsX.add(x + xSize / 2);
            }
        }

        dimPointsX.add((double) carportLengthCm);
        for (int i = 0; i < dimPointsX.size() - 1; i++) {
            svg.addDimension(dimPointsX.get(i), carportHeightCm, dimPointsX.get(i + 1), carportHeightCm, OffsetDirection.DOWN);
        }
        return svg.close();
    }

    public static String topView(Carport carport) throws SQLException {

        int carportLengthCm = carport.getLength();
        int carportWidthCm = carport.getWidth();

        Svg svg = new Svg(-100, -100, carportLengthCm + 100, carportWidthCm + 100);

        svg.addRectangle(-100, -100, carportLengthCm + 200, carportWidthCm + 200, "fill:white");
        svg.addDimension(0, carportWidthCm, carportLengthCm, carportWidthCm, OffsetDirection.DOWN);
        svg.addDimension(0, 0, 0, carportWidthCm, OffsetDirection.LEFT, 70);

        List<Double> pillarEdgesY = new ArrayList<Double>();
        double pillarYSize = 0;
        List<Double> rafterCentersX = new ArrayList<Double>();

        for (PlacedMaterial placedMaterial : carport.getPlacedMaterials()) {
            double x = placedMaterial.getX();
            double y = placedMaterial.getY();
            double xSize = placedMaterial.getMaterial().getLengthCm();
            double ySize = placedMaterial.getMaterial().getHeightCm();
            String itemType = placedMaterial.getMaterial().getItemType();

            if (itemType.equalsIgnoreCase("stolpe")) {
                svg.addRectangle(x, y, xSize, ySize, "stroke-width:2px; stroke:black; fill:none");
                pillarEdgesY.add(y);
                pillarYSize = ySize;
                continue;
            }

            svg.addRectangle(x, y, xSize, ySize, "stroke:black; fill:white");

            if (itemType.equalsIgnoreCase("lægte")) {
                rafterCentersX.add(x + xSize / 2);
            }
        }

        double pillarEdgeY1 = Collections.min(pillarEdgesY);
        double pillarEdgeY2 = Collections.max(pillarEdgesY) + pillarYSize;
        svg.addDimension(0, pillarEdgeY1, 0, pillarEdgeY2, OffsetDirection.LEFT, 40, "*");

        for (int i = 0; i < rafterCentersX.size() - 1; i++) {
            svg.addDimension(rafterCentersX.get(i), 0, rafterCentersX.get(i + 1), 0, OffsetDirection.UP);
        }

        double strapAx1 = carport.getFixatingStrapListXY().get(0).get(0);
        double strapAy1 = carport.getFixatingStrapListXY().get(0).get(1);
        double strapAx2 = carport.getFixatingStrapListXY().get(0).get(2);
        double strapAy2 = carport.getFixatingStrapListXY().get(0).get(3);
        double strapBx1 = carport.getFixatingStrapListXY().get(1).get(0);
        double strapBy1 = carport.getFixatingStrapListXY().get(1).get(1);
        double strapBx2 = carport.getFixatingStrapListXY().get(1).get(2);
        double strapBy2 = carport.getFixatingStrapListXY().get(1).get(3);
        svg.addLine(strapAx1, strapAy1, strapAx2, strapAy2, "stroke:black; stroke-dasharray: 5 5");
        svg.addLine(strapBx1, strapBy1, strapBx2, strapBy2, "stroke:black; stroke-dasharray: 5 5");

        return svg.close();
    }
}