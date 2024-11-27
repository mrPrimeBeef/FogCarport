package app.services.StructureCalculationEngine;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;
import app.persistence.ItemSearchBuilder;
import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.StructureCalculationEngine.Entities.Material;
import app.services.StructureCalculationEngine.Entities.PlacedMaterial;
import app.services.StructureCalculationEngine.Entities.Structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarportCalculationStrategy implements CalculationStrategy{

    //Every unit is in cm unless otherwise is specified
    int defaultOverhangY = 100;
    int defaultOverhangX = 20;
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

            ItemSearchBuilder builderRafter = new ItemSearchBuilder();
            Map<String, Object> filtersRafter = builderRafter
                    .setItemType("Spær")
                    .setLengthCm(carport.getLength())
                    .build();

            Material rafterMaterial = ItemMapper.searchSingleItem(filtersRafter, pool);
            calculateRafters(carport, rafterMaterial);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return placedMaterialList;
    }

    void calculatePillars(Carport carport, Material pillarMaterial) {
        int amountOfPillarsX = (carport.getLength()) / maxPillarDistance % maxPillarDistance + 1;
        int amountOfPillarsY = (carport.getWidth() - defaultOverhangY * 2) / maxPillarDistance % maxPillarDistance + 1;

        for (int i = 0; i <= amountOfPillarsY; i++) {
            for (int j = 0; j <= amountOfPillarsX; j++) {
                float x = j * ((float) carport.getLength() / amountOfPillarsX - 2 * defaultOverhangX) - pillarMaterial.getWidthMm()/20+defaultOverhangX;
                float y = i * ((float) (carport.getWidth() - 2 * defaultOverhangY) / amountOfPillarsY) + defaultOverhangY;

                // Clone the material for each pillar
                Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
                PlacedMaterial placedPillar = new PlacedMaterial(clonedMaterial, x, y, 0);

                placedMaterialList.add(placedPillar);
                float length = placedPillar.getMaterial().getLengthCm();
                float width = placedPillar.getMaterial().getWidthMm()/10;
                float height = placedPillar.getMaterial().getHeightMm()/10;
                placedPillar.getMaterial().setHeightMm(length);
                placedPillar.getMaterial().setLengthCm(width);
                placedPillar.getMaterial().setWidthMm(width);
            }
        }
    }

    private void calculateBeams(Carport carport, Material beamMaterial) {
        int amountOfBeamsX = (carport.getLength() - defaultOverhangY * 2) / maxPillarDistance % maxPillarDistance + 1;

        PlacedMaterial placedBeam = null;
        for (int j = 0; j <= amountOfBeamsX; j++) {
            float x = j * ((float) carport.getLength() / amountOfBeamsX - 2 * defaultOverhangX) - beamMaterial.getWidthMm()/20+defaultOverhangX;
            Material clonedMaterial = beamMaterial.cloneMaterial(beamMaterial);
            placedBeam = new PlacedMaterial(clonedMaterial, x, 0, carport.getHeight());
            placedBeam.setX(placedBeam.getX());
            placedMaterialList.add(placedBeam);

            placedBeam.getMaterial().setHeightMm(placedBeam.getMaterial().getHeightMm() / 10);
            placedBeam.getMaterial().setWidthMm(placedBeam.getMaterial().getWidthMm() / 10);
        }
    }

    private void calculateRafters(Carport carport, Material rafterMaterial){

        int amountOfRaftersY = (carport.getWidth() / 50);

        PlacedMaterial placedRafter = null;
        for(int k = 0; k <= amountOfRaftersY; k++){
            float y = k * ((float) (carport.getWidth()) / amountOfRaftersY);
            Material clonedMaterial = rafterMaterial.cloneMaterial(rafterMaterial);
            placedRafter = new PlacedMaterial(clonedMaterial, 0, y, 0);
            placedRafter.setX(placedRafter.getX());
            placedMaterialList.add(placedRafter);

            float length = placedRafter.getMaterial().getLengthCm();
            float width = placedRafter.getMaterial().getWidthMm();

            placedRafter.getMaterial().setLengthCm(width/10);
            placedRafter.getMaterial().setWidthMm(length);
        }
    }
}
