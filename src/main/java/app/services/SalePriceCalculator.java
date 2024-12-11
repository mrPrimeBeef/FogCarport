package app.services;

public class SalePriceCalculator {

    public static double calculateSalePrice(double costPrice, double marginPercentage) {
        return 100 * costPrice / (100 - marginPercentage);
    }

    public static double calculateSalePriceInclVAT(double costPrice, double marginPercentage) {
        return 1.25 * calculateSalePrice(costPrice, marginPercentage);
    }

    public static double calculateMarginAmount(double costPrice, double marginPercentage) {
        return calculateSalePrice(costPrice, marginPercentage) - costPrice;
    }

}
