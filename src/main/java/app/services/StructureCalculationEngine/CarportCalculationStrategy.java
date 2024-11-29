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
    int defaultOverhangY = 20;
    int defaultOverhangX = 100;
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
            //***** Remme *****
            ItemSearchBuilder builderBeam = new ItemSearchBuilder();
            Map<String, Object> filtersBeam = builderBeam
                    .setItemType("Spær")
                    .setLengthCm(carport.getLength())
                    .build();

            Material beamMaterial = ItemMapper.searchSingleItem(filtersBeam, pool);
            calculateBeams(carport, beamMaterial);

            //***** Spær *****
            ItemSearchBuilder builderRafter = new ItemSearchBuilder();
            Map<String, Object> filtersRafter = builderRafter
                    .setItemType("Lægte")
                    .setLengthCm(carport.getWidth())
                    .build();

            Material rafterMaterial = ItemMapper.searchSingleItem(filtersRafter, pool);
            calculateRafters(carport, rafterMaterial);

            //***** Stolper *****
            ItemSearchBuilder builderPillar = new ItemSearchBuilder();
            Map<String, Object> filtersPillar = builderPillar
                    .setItemType("Stolpe")
                    .setLengthCm(carport.getHeight())
                    .build();

            Material pillarMaterial = ItemMapper.searchSingleItem(filtersPillar, pool);
            calculatePillars(carport, pillarMaterial);

            // testingPillars(carport, pillarMaterial);


        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return placedMaterialList;
    }

    void testingPillars(Carport carport, Material pillarMaterial){
        for(int x = 0; x <= carport.getWidth(); x += 30){
            for(int y = 0; y <= carport.getLength(); y += 30){
                Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
                PlacedMaterial placedPillar = new PlacedMaterial(clonedMaterial, x, y, 0);

                rotateAroundY(clonedMaterial);
                placedMaterialList.add(placedPillar);
            }
        }
    }

    void calculatePillars(Carport carport, Material pillarMaterial) {

        for(int i = 0; i < 4; i++){
            Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
            PlacedMaterial placedPillar = null;
            if(i == 0){
                placedPillar = new PlacedMaterial(clonedMaterial, defaultOverhangX - pillarMaterial.getWidthCm()/20, defaultOverhangY - pillarMaterial.getWidthCm()/20, 0);
            }else if(i == 1){
                placedPillar = new PlacedMaterial(clonedMaterial, carport.getLength() - defaultOverhangX - pillarMaterial.getWidthCm()/20, defaultOverhangY - pillarMaterial.getWidthCm()/20, 0);
            }else if(i == 2){
                placedPillar = new PlacedMaterial(clonedMaterial, defaultOverhangX - pillarMaterial.getWidthCm()/20, carport.getWidth() - defaultOverhangY - pillarMaterial.getWidthCm()/20, 0);
            }else if(i == 3){
                placedPillar = new PlacedMaterial(clonedMaterial, carport.getLength() - defaultOverhangX - pillarMaterial.getWidthCm()/20, carport.getWidth() - defaultOverhangY - pillarMaterial.getWidthCm()/20, 0);
            }
            rotateAroundY(clonedMaterial);
            placedMaterialList.add(placedPillar);
        }


        /*
        int amountOfPillarsX = Math.max(2, (carport.getLength() - 2 * defaultOverhangX) / maxPillarDistance + 1);
        int amountOfPillarsY = Math.max(2, (carport.getWidth() - 2 * defaultOverhangY) / maxPillarDistance + 1);

        System.out.println("Amount of Pillars on X: " + amountOfPillarsX);
        System.out.println("Amount of Pillars on Y: " + amountOfPillarsY);

        for (int i = 0; i < amountOfPillarsY; i++) {
            for (int j = 0; j < amountOfPillarsX; j++) {
                float y = defaultOverhangX + j * (float) (carport.getLength() - 2 * defaultOverhangX) / (amountOfPillarsX - 1);
                float x = defaultOverhangY + i * (float) (carport.getWidth() - 2 * defaultOverhangY) / (amountOfPillarsY - 1);

                // Clones the material for each pillar
                Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
                System.out.println("Pillar x: " + x);
                System.out.println("Pillar y: " + y);
                PlacedMaterial placedPillar = new PlacedMaterial(clonedMaterial, x, y, 0);

                rotateAroundY(clonedMaterial);
                placedMaterialList.add(placedPillar);
            }
        }

        */
    }


    private void calculateBeams(Carport carport, Material beamMaterial) {

        for(int i = 0; i < 4; i++){
            Material clonedMaterial = beamMaterial.cloneMaterial(beamMaterial);
            PlacedMaterial placedPillar = null;
            if(i == 0){
                placedPillar = new PlacedMaterial(clonedMaterial, 0, defaultOverhangY - beamMaterial.getWidthCm()/20, 0);
            }else if(i == 1){
                placedPillar = new PlacedMaterial(clonedMaterial, 0, defaultOverhangY - beamMaterial.getWidthCm()/20, 0);
            }else if(i == 2){
                placedPillar = new PlacedMaterial(clonedMaterial, 0, carport.getWidth() - defaultOverhangY - beamMaterial.getWidthCm()/20, 0);
            }else if(i == 3){
                placedPillar = new PlacedMaterial(clonedMaterial, 0, carport.getWidth() - defaultOverhangY - beamMaterial.getWidthCm()/20, 0);
            }
            rotateAroundX(clonedMaterial);
            placedMaterialList.add(placedPillar);
        }

        /*
        int amountOfBeamsY = (carport.getLength() - 2 * defaultOverhangY) / maxPillarDistance % maxPillarDistance + 1;

        System.out.println("Amount of Beams: " + amountOfBeamsY);

        for (int j = 0; j < amountOfBeamsY; j++) {
            float y = j * ((float) carport.getLength() / amountOfBeamsY - 2 * defaultOverhangY) - beamMaterial.getWidthCm()/20+defaultOverhangY;
            Material clonedMaterial = beamMaterial.cloneMaterial(beamMaterial);
            System.out.println("Beam y: " + y);
            PlacedMaterial placedBeam = new PlacedMaterial(clonedMaterial, 0, y, carport.getHeight());

            rotateAroundX(clonedMaterial);
            placedMaterialList.add(placedBeam);
        }
        */
    }

    private void calculateRafters(Carport carport, Material rafterMaterial){

        int amountOfRaftersX = carport.getLength() / 50;
        float spacing = (carport.getLength() - (amountOfRaftersX * rafterMaterial.getWidthCm()/10)) / (amountOfRaftersX - 1);

        for(int k = 0; k < amountOfRaftersX; k++){
            float x = k * (rafterMaterial.getWidthCm()/10 + spacing);

            Material clonedMaterial = rafterMaterial.cloneMaterial(rafterMaterial);
            PlacedMaterial placedRafter = new PlacedMaterial(clonedMaterial, x, 0, 0);

            rotateAroundZ(clonedMaterial);
            rotateAroundY(clonedMaterial);
            placedMaterialList.add(placedRafter);
        }
    }

    void rotateAroundX(Material material){

        float height = material.getHeightCm();
        float width = material.getWidthCm();

        material.setHeightCm(width);
        material.setWidthCm(height);
    }

    void rotateAroundY(Material material){

        float width = material.getWidthCm();
        float length = material.getLengthCm();

        material.setLengthCm(width);
        material.setWidthCm(length);
    }

    void rotateAroundZ(Material material){

        float height = material.getHeightCm();
        float length = material.getLengthCm();

        material.setHeightCm(length);
        material.setLengthCm(height);
    }
}
