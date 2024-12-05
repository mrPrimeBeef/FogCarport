package app.services.svgEngine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SvgTest {

    private Svg svg;

    @BeforeEach
    void setUp() {
        svg = new Svg(-100, -100, 700, 500);
    }

    @Test
    void openAndCloseTags() {
        String actualSvg = svg.close();
        String expectedOpenTag = "<svg version=\"1.1\" x=\"-100\" y=\"-100\" width=\"100%\" viewBox=\"-100 -100 800 600\" preserveAspectRatio=\"xMinYMin\">";
        String expectedCloseTag = "</svg>";
        assertTrue(actualSvg.contains(expectedOpenTag));
        assertTrue(actualSvg.contains(expectedCloseTag));
    }

    @Test
    void addRectangle() {
        svg.addRectangle(-95, 225.09, 60.5, 80.21, "stroke:black; fill: white");
        String actualSvg = svg.close();
        String expectedRectangle = "<rect x=\"-95.000000\" y=\"225.090000\" width=\"60.500000\" height=\"80.210000\" style=\"stroke:black; fill: white\"/>";
        assertTrue(actualSvg.contains(expectedRectangle));
    }

    @Test
    void addLine() {
        svg.addLine(50.5, 0, 350.8, 272.99, "stroke: black");
        String actualSvg = svg.close();
        String expectedLine = "<line x1=\"50.500000\" y1=\"0.000000\" x2=\"350.800000\" y2=\"272.990000\" style=\"stroke: black\"/>";
        assertTrue(actualSvg.contains(expectedLine));
    }

    @Test
    void dimensionText() {
        svg.addDimension(50.5, -19, 50.5, 223, OffsetDirection.DOWN);
        String actualSvg = svg.close();
        String expectedDimensionText = "<text x=\"50.500000\" y=\"21.000000\" text-anchor=\"middle\" alignment-baseline=\"middle\" transform=\"rotate(0.000000 50.500000 21.000000)\">2,42</text>";
        assertTrue(actualSvg.contains(expectedDimensionText));
    }

}