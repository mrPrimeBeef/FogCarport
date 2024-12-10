package app.services.StructureCalculationEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.ItemMapper;
import app.persistence.ItemSearchBuilder;
import app.services.StructureCalculationEngine.Entities.Carport;
import app.services.StructureCalculationEngine.Entities.Material;
import app.services.StructureCalculationEngine.Entities.PlacedMaterial;
import app.services.StructureCalculationEngine.Entities.Structure;

public class CarportCalculationStrategy implements CalculationStrategy{

    List<PlacedMaterial> placedMaterialList;

    //Every unit is in cm unless otherwise is specified
    int defaultOverhangY = 20;
    int defaultOverhangX = 100;
    int maxPillarDistanceX = 600 - 2 * defaultOverhangX;
    // int getMaxPillarDistanceY = 560 - 2 * defaultOverhangY;
    int rafterDistance = 50;

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

            //***** Stern Brædder Ender *****
            getFasciaEndsAndCalculate(carport);

            //***** Stern Brædder Sider *****
            getFasciaSidesAndCalculate(carport);

            //***** Tagplader *****
            getRoofPanelsAndCalculate(carport);

            //***** Tag Skruer *****
            getRoofScrewsAndCalculate(carport);

            //***** Hulbånd *****
            getFixatingStrapAndCalculate(carport);

        } catch (DatabaseException e) {
            e.printStackTrace();
        }

        return placedMaterialList;
    }

    // Most calculations are broken in to parts: One part to get the required material, and another to place
    // the material, and calculate the amount needed.
    private void getPillarsAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderPillar = new ItemSearchBuilder();
        Map<String, Object> filtersPillar = builderPillar
                .setItemType("Stolpe")
                .setHeightMm(97)
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
                float x;
                if(carport.getLength() > 400) {
                    x = defaultOverhangX + j * (float) (carport.getLength() - 2 * defaultOverhangX) / (amountOfPillarsX - 1);
                } else {
                    x = 40 + j * (float) (carport.getLength() - 2 * 40) / (amountOfPillarsX - 1);
                }
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
                .setWidthMm(45)
                .setHeightMm(195)
                .setLengthCm(carport.getLength())
                .build();
        Material beamMaterial = ItemMapper.searchSingleItem(filtersBeam, pool);

        calculateBeams(carport, beamMaterial);
    }

    private void calculateBeams(Carport carport, Material beamMaterial) {

        PlacedMaterial placedBeam = new PlacedMaterial(beamMaterial, 0, 0, 0);
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
                .setWidthMm(38)
                .setHeightMm(73)
                .setLengthCm(carport.getWidth())
                .build();
        Material rafterMaterial = ItemMapper.searchSingleItem(filtersRafter, pool);

        int amount = calculateRafters(carport, rafterMaterial);
        getRafterBracketsAndCalculate(carport, amount);
    }

    private int calculateRafters(Carport carport, Material rafterMaterial){

        int amountOfRaftersX = carport.getLength() / rafterDistance;
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
        return totalAmount;
    }

    private void getRafterBracketsAndCalculate(Carport carport, int amount) throws DatabaseException {

        ItemSearchBuilder builderRightBrackets = new ItemSearchBuilder();
        Map<String, Object> filtersRightBrackets = builderRightBrackets
                .setName("Universal beslag 190 mm")
                .setDescription("højre")
                .build();
        Material rightBracketMaterial = ItemMapper.searchSingleItem(filtersRightBrackets, pool);

        ItemSearchBuilder builderLeftBrackets = new ItemSearchBuilder();
        Map<String, Object> filtersLeftBrackets = builderLeftBrackets
                .setName("Universal beslag 190 mm")
                .setDescription("venstre")
                .build();
        Material leftBracketMaterial = ItemMapper.searchSingleItem(filtersLeftBrackets, pool);

        calculateRafterBrackets(carport, rightBracketMaterial, amount);
        calculateRafterBrackets(carport, leftBracketMaterial, amount);
    }

    private void calculateRafterBrackets(Carport carport, Material rafterBracket, int amount){
        calculatePartsList(carport, rafterBracket, amount);
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

    private void getRoofPanelsAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderRoofPanelsShort = new ItemSearchBuilder();
        Map<String, Object> filtersRoofPanelsShort = builderRoofPanelsShort
                .setName("Plastmo Ecolite blåtonet")
                .setItemType("Tagplade")
                .setLengthCm(360)
                .build();
        Material roofPanelShort = ItemMapper.searchSingleItem(filtersRoofPanelsShort, pool);

        ItemSearchBuilder builderRoofPanelsLong = new ItemSearchBuilder();
        Map<String, Object> filtersRoofPanelsLong = builderRoofPanelsLong
                .setName("Plastmo Ecolite blåtonet")
                .setItemType("Tagplade")
                .setLengthCm(600)
                .build();
        Material roofPanelLong = ItemMapper.searchSingleItem(filtersRoofPanelsLong, pool);

        calculateRoofPanels(carport, roofPanelShort, roofPanelLong);
    }

    void calculateRoofPanels(Carport carport, Material roofPanelShort, Material roofPanelLong){

        if(carport.getLength() < 360){
            calculatePartsList(carport, roofPanelShort, (int)Math.ceil((double) carport.getWidth() /100));

        }else if(carport.getLength() < 600){
            calculatePartsList(carport, roofPanelLong, (int)Math.ceil((double) carport.getWidth() /100));

        }else{
            calculatePartsList(carport, roofPanelShort, (int)Math.ceil((double) carport.getWidth() /100));
            calculatePartsList(carport, roofPanelLong, (int)Math.ceil((double) carport.getWidth() /100));
        }
    }

    private void getRoofScrewsAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderRoofScrew = new ItemSearchBuilder();
        Map<String, Object> filtersRoofScrew = builderRoofScrew
                .setName("Plastmo bundskruer")
                .setItemType("Bundskrue")
                .build();
        Material roofScrew = ItemMapper.searchSingleItem(filtersRoofScrew, pool);

        calculateRoofScrews(carport, roofScrew);
    }

    private void calculateRoofScrews(Carport carport, Material roofScrew){

        // 12 skruer pr. kvadratmeter
        int quantity = (int) Math.ceil(((double)(carport.getWidth()/100) * (double)(carport.getLength()/100)) * 12 / roofScrew.getPackageAmount());

        calculatePartsList(carport, roofScrew, quantity);
    }

    private void getFixatingStrapAndCalculate(Carport carport) throws DatabaseException {

        ItemSearchBuilder builderFixatingStrap = new ItemSearchBuilder();
        Map<String, Object> filtersFixatingStrap = builderFixatingStrap
                .setName("Hulbånd")
                .build();
        Material fixatingStrap = ItemMapper.searchSingleItem(filtersFixatingStrap, pool);

        calculateFixatingStrap(carport, fixatingStrap);
    }

    private void calculateFixatingStrap(Carport carport, Material fixatingStrap) {

        List<Float> fixatingStrapOneCoordinates = Arrays.asList(
                (float)rafterDistance,
                (float)defaultOverhangY,
                (float)carport.getLength() - rafterDistance,
                (float)carport.getWidth() - defaultOverhangY
        );

        List<Float> fixatingStrapTwoCoordinates = Arrays.asList(
                (float)rafterDistance,
                (float)carport.getWidth() - defaultOverhangY,
                (float)carport.getLength() - rafterDistance,
                (float)defaultOverhangY
        );

        carport.addFixatingStrapListXY(fixatingStrapOneCoordinates);
        carport.addFixatingStrapListXY(fixatingStrapTwoCoordinates);

        double fixatingStrapsLength = Math.sqrt(Math.pow(carport.getLength(), 2) + Math.pow(carport.getWidth(), 2)) * 2;
        int quantity = (int) (Math.ceil(fixatingStrapsLength / 100) * 0.1);

        calculatePartsList(carport, fixatingStrap, quantity);
    }

    private void calculatePartsList(Carport carport, Material material, int quantity) {
        carport.addToPartsList(material, quantity);
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
