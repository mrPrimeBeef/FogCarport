package app.persistence;

import java.util.HashMap;
import java.util.Map;

// Makes an object to search dynamically in DB.
// All fields are null to start with.
// To start the search from another class, you do the following:
//
// ItemSearchBuilder builderPillar = new ItemSearchBuilder();
// Map<String, Object> filtersPillar = builderPillar
//        .setItemType("Stolpe")
//        .setLengthCm(carport.getHeight())
//        .build();

public class ItemSearchBuilder {
    private String name;
    private String description;
    private String itemType;
    private String materialType;
    private Integer lengthCm;
    private Integer widthMm;
    private Integer heightMm;
    private Integer packageAmount;
    private String packageType;
    private Float costPrice;

    public ItemSearchBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemSearchBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ItemSearchBuilder setItemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    public ItemSearchBuilder setMaterialType(String materialType) {
        this.materialType = materialType;
        return this;
    }

    public ItemSearchBuilder setLengthCm(Integer lengthCm) {
        this.lengthCm = lengthCm;
        return this;
    }

    public ItemSearchBuilder setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
        return this;
    }

    public ItemSearchBuilder setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
        return this;
    }

    public ItemSearchBuilder setPackageAmount(Integer packageAmount) {
        this.packageAmount = packageAmount;
        return this;
    }

    public ItemSearchBuilder setPackageType(String packageType) {
        this.packageType = packageType;
        return this;
    }

    public ItemSearchBuilder setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
        return this;
    }

    public Map<String, Object> build() {
        Map<String, Object> filters = new HashMap<>();
        if (name != null) filters.put("name", name);
        if (description != null) filters.put("description", description);
        if (itemType != null) filters.put("item_type", itemType);
        if (materialType != null) filters.put("material_type", materialType);
        if (lengthCm != null) filters.put("length_cm", lengthCm);
        if (widthMm != null) filters.put("width_mm", widthMm);
        if (heightMm != null) filters.put("height_mm", heightMm);
        if (packageAmount != null) filters.put("package_amount", packageAmount);
        if (packageType != null) filters.put("package_type", packageType);
        if (costPrice != null) filters.put("cost_price", costPrice);

        clear();
        return filters;
    }

    public ItemSearchBuilder clear() {
        this.name = null;
        this.description = null;
        this.itemType = null;
        this.materialType = null;
        this.lengthCm = null;
        this.widthMm = null;
        this.heightMm = null;
        this.packageAmount = null;
        this.packageType = null;
        this.costPrice = null;
        return this;
    }
}
