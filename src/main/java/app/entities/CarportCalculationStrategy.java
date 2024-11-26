package app.entities;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;
import app.persistence.ItemSearchBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarportCalculationStrategy implements CalculationStrategy{

    //Every unit is in cm unless otherwise is specified
    int defaultOverhang = 100;
    int maxPillarDistance = 310;
    List<PlacedMaterial> placedMaterialList;

    ConnectionPool pool;

    public CarportCalculationStrategy(ConnectionPool pool) {
        this.pool = pool;
        this.placedMaterialList = new ArrayList<>();
    }

    @Override
    public List<PlacedMaterial> calculateStructure(Structure structure) {
        Carport carport = (Carport) structure;

        try {
            ItemSearchBuilder builderPillar = new ItemSearchBuilder();
            Map<String, Object> filtersPillar = builderPillar
                    .setItemType("Stolpe")
                    .setLengthCm(carport.getHeight())
                    .build();

            Material pillarMaterial = ItemMapper.searchSingleItem(filtersPillar, pool);
            calculatePillars(carport, pillarMaterial);

            ItemSearchBuilder builderBeam = new ItemSearchBuilder();
            Map<String, Object> filtersBeam = builderBeam
                    .setItemType("Spær")
                    .setLengthCm(carport.getWidth())
                    .build();

            Material beamMaterial = ItemMapper.searchSingleItem(filtersBeam, pool);
            calculateBeams(carport, beamMaterial);

            //ItemSearchBuilder builderRafter = new ItemSearchBuilder();
            //Map<String, Object> filtersRafter = builderRafter
            //        .setItemType("Spær")
            //        .setLengthCm(carport.getWidth())
            //        .build();

            //Material rafterMaterial = ItemMapper.searchSingleItem(filtersRafter, pool);
            //calculateRafters(carport, rafterMaterial);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return placedMaterialList;
    }

    void calculatePillars(Carport carport, Material pillarMaterial) {
        int amountOfPillarsX = (carport.getLength() - defaultOverhang * 2) / maxPillarDistance % maxPillarDistance + 1;
        int amountOfPillarsY = (carport.getWidth() - defaultOverhang * 2) / maxPillarDistance % maxPillarDistance + 1;

        for (int i = 0; i <= amountOfPillarsY; i++) {
            for (int j = 0; j <= amountOfPillarsX; j++) {
                float x = j * ((float) (carport.getLength() - 2 * defaultOverhang) / amountOfPillarsX) + defaultOverhang;
                float y = i * ((float) (carport.getWidth() - 2 * defaultOverhang) / amountOfPillarsY) + defaultOverhang;

                // Clone the material for each pillar
                Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
                PlacedMaterial placedPillar = new PlacedMaterial(clonedMaterial, x, y, 0);

                placedMaterialList.add(placedPillar);
                float length = placedPillar.material.getLengthCm();
                float width = placedPillar.material.getWidthMm()/10;
                float height = placedPillar.material.getHeightMm()/10;
                placedPillar.material.setHeightMm(length);
                placedPillar.material.setLengthCm(width);
                placedPillar.material.setWidthMm(width);
            }
        }
    }

    private void calculateBeams(Carport carport, Material beamMaterial) {
        int amountOfBeamsX = (carport.getLength()-defaultOverhang*2)/maxPillarDistance % maxPillarDistance + 1;

        PlacedMaterial placedBeam = null;
        for (int j = 0; j <= amountOfBeamsX; j++) {
            float x = j * ((float) (carport.getLength() - 2 * defaultOverhang) / amountOfBeamsX) + defaultOverhang;
            Material clonedMaterial = beamMaterial.cloneMaterial(beamMaterial);
            placedBeam = new PlacedMaterial(clonedMaterial, x, 0, 0);
            placedBeam.setX(placedBeam.getX());
            placedMaterialList.add(placedBeam);

            placedBeam.material.setHeightMm(placedBeam.material.getHeightMm()/10);
            placedBeam.material.setWidthMm(placedBeam.material.getWidthMm()/10);
        }
    }

    private void calculateRafters(Carport carport, Material rafterMaterial){

        int amountOfRaftersY = (carport.getLength()-defaultOverhang*2)/maxPillarDistance % maxPillarDistance + 1;
        float bestDistanceBetweenRafters = 0;
        float bestRafterAmount = 0;
        float smallestRemainder = 1000;
        int rafterAmount = 1;

        for(rafterAmount = 1; rafterAmount <= carport.getLength() / 50; rafterAmount++){
            float distanceBetweenRafters = (float)carport.getLength() / rafterAmount;
            if(distanceBetweenRafters >= 50 && distanceBetweenRafters <= 60){
                float remainder = Math.abs(distanceBetweenRafters - Math.round(distanceBetweenRafters));
                if(remainder < smallestRemainder){
                    smallestRemainder = remainder;
                    bestDistanceBetweenRafters = distanceBetweenRafters;
                    bestRafterAmount = rafterAmount;
                }
            }
        }

        PlacedMaterial placedRafter = null;
        for(int k = 0; k <= amountOfRaftersY; k++){
            float y = k * ((float) (carport.getLength() - 2 * defaultOverhang) / amountOfRaftersY) + defaultOverhang;
            Material clonedMaterial = rafterMaterial.cloneMaterial(rafterMaterial);
            placedRafter = new PlacedMaterial(clonedMaterial, 0, y, 0);
            placedRafter.setX(placedRafter.getX());
            placedMaterialList.add(placedRafter);

            float length = placedRafter.material.getLengthCm();
            float width = placedRafter.material.getWidthMm();

            placedRafter.material.setHeightMm(placedRafter.material.getHeightMm()/10);
            placedRafter.material.setWidthMm(placedRafter.material.getWidthMm()/10);
        }
    }
}
