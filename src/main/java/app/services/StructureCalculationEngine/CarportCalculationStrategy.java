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
                    .setLengthCm(carport.getHeight()+90)
                    .build();

            Material pillarMaterial = ItemMapper.searchSingleItem(filtersPillar, pool);
            calculatePillars(carport, pillarMaterial);

            //***** Stern Brædder *****
            ItemSearchBuilder builderFasciaBoard = new ItemSearchBuilder();
            Map<String, Object> filtersFasciaBoardSide = builderFasciaBoard
                    .setItemType("Brædt")
                    .setWidthMm(25)
                    .setHeightMm(200)
                    .setLengthCm(carport.getLength())
                    .build();

            Material FasciaBoardSide = ItemMapper.searchSingleItem(filtersFasciaBoardSide, pool);

            Map<String, Object> filterFasciaFrontBack = builderFasciaBoard
                    .setItemType("Brædt")
                    .setWidthMm(25)
                    .setHeightMm(200)
                    .setLengthCm(carport.getWidth())
                    .build();

            Material fasciaFrontBack = ItemMapper.searchSingleItem(filterFasciaFrontBack, pool);
            calculateFasciaBoard(carport, fasciaFrontBack, FasciaBoardSide);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return placedMaterialList;
    }

    void calculatePillars(Carport carport, Material pillarMaterial) {

        for(int i = 0; i < 4; i++){
            Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
            float materialWidth = clonedMaterial.getWidthCm();
            clonedMaterial.setLengthCm(carport.getHeight());

            PlacedMaterial placedPillar = null;
            if(i == 0){
                placedPillar = new PlacedMaterial(clonedMaterial, defaultOverhangX - materialWidth/2, defaultOverhangY - materialWidth/2, 0);
            }else if(i == 1){
                placedPillar = new PlacedMaterial(clonedMaterial, carport.getLength() - defaultOverhangX - materialWidth/2, defaultOverhangY - materialWidth/2, 0);
            }else if(i == 2){
                placedPillar = new PlacedMaterial(clonedMaterial, defaultOverhangX - materialWidth/2, carport.getWidth() - defaultOverhangY - materialWidth/2, 0);
            }else if(i == 3){
                placedPillar = new PlacedMaterial(clonedMaterial, carport.getLength() - defaultOverhangX - materialWidth/2, carport.getWidth() - defaultOverhangY - materialWidth/2, 0);
            }
            // placedPillar = new PlacedMaterial(clonedMaterial, 0, 0, 100);

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

        for(int i = 0; i < 2; i++){
            Material clonedMaterial = beamMaterial.cloneMaterial(beamMaterial);
            float materialWidth = clonedMaterial.getWidthCm();
            clonedMaterial.setLengthCm(carport.getLength());

            PlacedMaterial placedPillar = null;
            if(i == 0){
                placedPillar = new PlacedMaterial(clonedMaterial, 0, defaultOverhangY - materialWidth/2, 0);
            }else if(i == 1){
                placedPillar = new PlacedMaterial(clonedMaterial, 0, carport.getWidth() - defaultOverhangY - materialWidth/2, 0);
            }
            rotateAroundX(clonedMaterial);
            placedMaterialList.add(placedPillar);
        }

        /*
        int amountOfBeamsY = (carport.getLength() - 2 * defaultOverhangY) / maxPillarDistance % maxPillarDistance + 1;

        System.out.println("Amount of Beams: " + amountOfBeamsY);

        for (int j = 0; j < amountOfBeamsY; j++) {
            float y = j * ((float) carport.getLength() / amountOfBeamsY - 2 * defaultOverhangY) - beamMaterial.getWidthCm()/2+defaultOverhangY;
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
        float totalRafterWidth = amountOfRaftersX * rafterMaterial.getWidthCm();
        float spacing = (carport.getLength() - totalRafterWidth) / (amountOfRaftersX - 1);

        for(int k = 0; k < amountOfRaftersX; k++){
            float x = k * (rafterMaterial.getWidthCm() + spacing);

            Material clonedMaterial = rafterMaterial.cloneMaterial(rafterMaterial);
            clonedMaterial.setLengthCm(carport.getWidth()); // Adjust length to fit carport width
            PlacedMaterial placedRafter = new PlacedMaterial(clonedMaterial, x, 0, 0);

            rotateAroundZ(clonedMaterial);
            rotateAroundY(clonedMaterial);
            placedMaterialList.add(placedRafter);
        }
    }

    private void calculateFasciaBoard(Carport carport, Material fasciaFrontBack, Material fasciaSide) {

        for(int i = 0; i < 2; i++){
            Material clonedMaterial = fasciaFrontBack.cloneMaterial(fasciaFrontBack);
            clonedMaterial.setLengthCm(clonedMaterial.getLengthCm() * (carport.getWidth() / clonedMaterial.getLengthCm()));
            float materialWidth = clonedMaterial.getWidthCm();

            PlacedMaterial placedFasciaFrontBack = null;
            if(i == 0){
                placedFasciaFrontBack = new PlacedMaterial(clonedMaterial, carport.getLength(), 0, 0);
            }else if(i == 1){
                placedFasciaFrontBack = new PlacedMaterial(clonedMaterial, 0 - materialWidth, 0, 0);
            }
            rotateAroundX(clonedMaterial);
            rotateAroundZ(clonedMaterial);
            placedMaterialList.add(placedFasciaFrontBack);
        }

        for(int i = 0; i < 2; i++){
            Material clonedMaterial = fasciaSide.cloneMaterial(fasciaSide);
            clonedMaterial.setLengthCm(clonedMaterial.getLengthCm() * (carport.getLength() / clonedMaterial.getLengthCm()));
            float materialWidth = clonedMaterial.getWidthCm();

            PlacedMaterial placedFasciaSide = null;
            if(i == 0){
                placedFasciaSide = new PlacedMaterial(clonedMaterial, 0, 0 - materialWidth, 0 + materialWidth / 2);
            }else if(i == 1){
                placedFasciaSide = new PlacedMaterial(clonedMaterial, 0, carport.getWidth(), 0);
            }
            rotateAroundX(clonedMaterial);
            placedMaterialList.add(placedFasciaSide);
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
