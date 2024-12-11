package app.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Bruger beregningseksempel fra dette link til at teste ud fra:
// https://startupsvar.dk/efter-start/oeg-dit-overskud/daekningsbidrag
class SalePriceCalculatorTest {

    @Test
    void calculateSalePrice() {
        double expected = 112;
        double actual = SalePriceCalculator.calculateSalePrice(90, 19.6);
        assertEquals(expected, Math.round(actual));
    }

    @Test
    void calculateSalePriceInclVAT() {
        double expected = 140;
        double actual = SalePriceCalculator.calculateSalePriceInclVAT(90, 19.6);
        assertEquals(expected, Math.round(actual));
    }

    @Test
    void calculateMarginAmount() {
        double expected = 22;
        double actual = SalePriceCalculator.calculateMarginAmount(90, 19.6);
        assertEquals(expected, Math.round(actual));
    }

}