package app.services.svgEngine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SvgTest {

    private static final Svg svg = new Svg("0", "0", "100%", "0 0 100 100");

    // TODO: Test empty SVG

    @Test
    void addRectangle() {
        String expected = "<rect x=\"95.000000\" y=\"100.000000\" width=\"700.000000\" height=\"400.000000\" style=\"stroke:black; fill: white\"/>";
        String actual = svg.addRectangle(95, 100, 700, 400, "stroke:black; fill: white");
        assertEquals(expected, actual);
    }


    @Test
    void addText() {
        String expected = "<text x=\"50.000000\" y=\"100.000000\" text-anchor=\"middle\" alignment-baseline=\"middle\" transform=\"rotate(90 50.000000 100.000000)\">Lorem Ipsum</text>";
        String actual = svg.addText("Lorem Ipsum", 50, 100, 90);
        assertEquals(expected, actual);
    }

    @Test
    void addLine() {
        String expected = "<line x1=\"50.000000\" y1=\"0.000000\" x2=\"350.000000\" y2=\"200.000000\" style=\"stroke: black\"/>";
        String actual = svg.addLine(50, 0, 350, 200, "stroke: black");
        assertEquals(expected, actual);
    }

    @Test
    void addDimensionLine() {
        String expected = "<line x1=\"50.000000\" y1=\"0.000000\" x2=\"350.000000\" y2=\"200.000000\" stroke=\"black\" marker-start=\"url(#beginArrow)\" marker-end=\"url(#endArrow)\"/>";
        String actual = svg.addDimensionLine(50, 0, 350, 200);
        assertEquals(expected, actual);
    }


}
