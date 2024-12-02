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

    List<PlacedMaterial> placedMaterialList;

    //Every unit is in cm unless otherwise is specified
    int defaultOverhangY = 20;
    int defaultOverhangX = 100;
    int maxPillarDistanceX = 600 - 2 * defaultOverhangX;
    // int getMaxPillarDistanceY = 560 - 2 * defaultOverhangY;

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
            getBeamAndCalculate(carport);

            //***** Spær *****
            getRafterAndCalculate(carport);

            //***** Stolper *****
            getPillarsAndCalculate(carport);

            //***** Stern Brædder Ender*****
            getFasciaEndsAndCalculate(carport);

            //***** Stern Brædder Ender*****
            getFasciaSidesAndCalculate(carport);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return placedMaterialList;
    }

    private void getPillarsAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderPillar = new ItemSearchBuilder();
        Map<String, Object> filtersPillar = builderPillar
                .setItemType("Stolpe")
                .setLengthCm(carport.getHeight()+90)
                .build();
        Material pillarMaterial = ItemMapper.searchSingleItem(filtersPillar, pool);

        calculatePillars(carport, pillarMaterial);
    }

    private void calculatePillars(Carport carport, Material pillarMaterial) {

        int amountOfPillarsX = Math.max(2, (carport.getLength() + 2 * defaultOverhangX) / maxPillarDistanceX + 1);
        int amountOfPillarsY = 2;
        int totalAmount = 0;
        //int amountOfPillarsY = Math.max(2, (carport.getWidth() - 2 * defaultOverhangY) / maxPillarDistance + 1);

        for (int i = 0; i < amountOfPillarsY; i++) {
            for (int j = 0; j < amountOfPillarsX; j++) {
                float x = 0;
                //if(carport.getLength() > 300) {
                    x = defaultOverhangX + j * (float) (carport.getLength() - 2 * defaultOverhangX) / (amountOfPillarsX - 1);
                //}
                float y = defaultOverhangY + i * (float) (carport.getWidth() - 2 * defaultOverhangY) / (amountOfPillarsY - 1);

                Material clonedMaterial = pillarMaterial.cloneMaterial(pillarMaterial);
                float materialWidth = clonedMaterial.getWidthCm();
                clonedMaterial.setLengthCm(carport.getHeight());
                PlacedMaterial placedPillar = new PlacedMaterial(clonedMaterial, x - materialWidth / 2, y - materialWidth / 2, 0);

                rotateAroundY(clonedMaterial);
                placedMaterialList.add(placedPillar);

                totalAmount++;
            }
        }

        calculatePartsList(carport, pillarMaterial, totalAmount);
    }

    private void getBeamAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderBeam = new ItemSearchBuilder();
        Map<String, Object> filtersBeam = builderBeam
                .setItemType("Spær")
                .setLengthCm(carport.getLength())
                .build();
        Material beamMaterial = ItemMapper.searchSingleItem(filtersBeam, pool);

        calculateBeams(carport, beamMaterial);
    }

    private void calculateBeams(Carport carport, Material beamMaterial) {

        PlacedMaterial placedBeam = null;
        int totalAmount = 0;

        for(int i = 0; i < 2; i++){
            Material clonedMaterial = beamMaterial.cloneMaterial(beamMaterial);
            float materialWidth = clonedMaterial.getWidthCm();
            clonedMaterial.setLengthCm(carport.getLength());

            if(i == 0){
                placedBeam = new PlacedMaterial(clonedMaterial, 0, defaultOverhangY - materialWidth/2, 0);
            }else if(i == 1){
                placedBeam = new PlacedMaterial(clonedMaterial, 0, carport.getWidth() - defaultOverhangY - materialWidth/2, 0);
            }
            rotateAroundX(clonedMaterial);
            placedMaterialList.add(placedBeam);
            totalAmount++;
        }

        calculatePartsList(carport, beamMaterial, totalAmount);
    }

    private void getRafterAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderRafter = new ItemSearchBuilder();
        Map<String, Object> filtersRafter = builderRafter
                .setItemType("Lægte")
                .setLengthCm(carport.getWidth())
                .build();
        Material rafterMaterial = ItemMapper.searchSingleItem(filtersRafter, pool);

        calculateRafters(carport, rafterMaterial);
    }

    private void calculateRafters(Carport carport, Material rafterMaterial){

        int amountOfRaftersX = carport.getLength() / 50;
        int totalAmount = 0;
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
            totalAmount++;
        }

        calculatePartsList(carport, rafterMaterial, totalAmount);
    }

    private void getFasciaEndsAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderFasciaBoard = new ItemSearchBuilder();
        Map<String, Object> filterFasciaFrontBack = builderFasciaBoard
                .setItemType("Brædt")
                .setWidthMm(25)
                .setHeightMm(200)
                .setLengthCm(carport.getWidth())
                .build();

        Material fasciaFrontBack = ItemMapper.searchSingleItem(filterFasciaFrontBack, pool);
        calculateFasciaEnds(carport, fasciaFrontBack);

        calculateFasciaEnds(carport, fasciaFrontBack);
    }

    private void calculateFasciaEnds(Carport carport, Material fasciaEnds) {

        int totalAmount = 0;

        for(int i = 0; i < 2; i++){
            Material clonedMaterial = fasciaEnds.cloneMaterial(fasciaEnds);
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
            totalAmount++;
        }
        calculatePartsList(carport, fasciaEnds, totalAmount);
    }

    private void getFasciaSidesAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderFasciaBoard = new ItemSearchBuilder();
        Map<String, Object> filtersFasciaBoardSide = builderFasciaBoard
                .setItemType("Brædt")
                .setWidthMm(25)
                .setHeightMm(200)
                .setLengthCm(carport.getLength())
                .build();
        Material FasciaBoardSide = ItemMapper.searchSingleItem(filtersFasciaBoardSide, pool);

        calculateFasciaSides(carport, FasciaBoardSide);
    }

    private void calculateFasciaSides(Carport carport, Material fasciaSide){

        int totalAmount = 0;

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
            totalAmount++;
        }
        calculatePartsList(carport, fasciaSide, totalAmount);
    }

    private void calculatePartsList(Structure structure, Material material, int quantity) {
        structure.addToPartsList(material.getName(), quantity);
    }

    private void rotateAroundX(Material material){

        float height = material.getHeightCm();
        float width = material.getWidthCm();

        material.setHeightCm(width);
        material.setWidthCm(height);
    }

    private void rotateAroundY(Material material){

        float width = material.getWidthCm();
        float length = material.getLengthCm();

        material.setLengthCm(width);
        material.setWidthCm(length);
    }

    private void rotateAroundZ(Material material){

        float height = material.getHeightCm();
        float length = material.getLengthCm();

        material.setHeightCm(length);
        material.setLengthCm(height);
    }
}
