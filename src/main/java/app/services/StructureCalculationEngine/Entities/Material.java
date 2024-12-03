package app.services.StructureCalculationEngine.Entities;

public class Material {

    private int materialId;
    private String name;
    private String description;
    private String itemType;
    private MaterialType materialType;
    private float lengthCm;
    private float drawnLengthCm;
    private float widthCm;
    private float drawnWidthCm;
    private float heightCm;
    private float drawnHeightCm;
    private int packageAmount;
    private String packageType;
    private float costPrice;

    public Material(int materialId, String name, String description, String itemType, String strMaterialType, float lengthCm, float widthMm, float heightMm, int packageAmount, String packageType, float costPrice) {
        this.materialId = materialId;
        this.name = name;
        this.description = description;
        this.itemType = itemType;
        this.lengthCm = lengthCm;
        this.widthCm = widthMm / 10;
        this.heightCm = heightMm / 10;
        this.packageAmount = packageAmount;
        this.packageType = packageType;
        this.costPrice = costPrice;

        this.materialType = convertToMaterialType(strMaterialType);
    }

    private MaterialType convertToMaterialType(String strMaterialType) {
        String normalizedString = strMaterialType
                .toUpperCase()
                .replace(".", "")
                .replace(" ", "_")
                .replace("Æ", "AE")
                .replace("Ø", "OE")
                .replace("Å", "AA");
        try {
            return MaterialType.valueOf(normalizedString);
        } catch (IllegalArgumentException e) {
            return MaterialType.UNKNOWN;
        }
    }

    public Material cloneMaterial(Material original) {
        return new Material(
                original.materialId,
                original.name,
                original.description,
                original.itemType,
                original.materialType.name(),
                original.lengthCm,
                original.widthCm * 10,
                original.heightCm * 10,
                original.packageAmount,
                original.packageType,
                original.costPrice
        );
    }


    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getItemType(){
        return itemType;
    }

    public MaterialType getMaterialType(){
        return materialType;
    }

    public float getLengthCm(){
        return lengthCm;
    }

    public float getDrawnLengthCm(){
        return drawnLengthCm;
    }

    public float getWidthCm(){
        return widthCm;
    }

    public float getDrawnWidthCm(){
        return drawnWidthCm;
    }

    public float getHeightCm(){
        return heightCm;
    }

    public float getDrawnHeightCm(){
        return drawnHeightCm;
    }

    public int getPackageAmount(){
        return packageAmount;
    }

    public String getPackageType(){
        return packageType;
    }

    public float getCostPrice(){
        return costPrice;
    }

    public void setHeightCm(float heightCm){
        this.heightCm = heightCm;
    }

    public void setDrawnHeightCm(float drawnHeightCm){
        this.drawnHeightCm = drawnHeightCm;
    }

    public void setWidthCm(float widthCm){
        this.widthCm = widthCm;
    }

    public void setDrawnWidthCm(float drawnWidthCm){
        this.drawnWidthCm = drawnWidthCm;
    }

    public void setLengthCm(float lengthCm){
        this.lengthCm = lengthCm;
    }

    public void setDrawnLengthCm(float drawnLengthCm){
        this.drawnLengthCm = drawnLengthCm;
    }
}
