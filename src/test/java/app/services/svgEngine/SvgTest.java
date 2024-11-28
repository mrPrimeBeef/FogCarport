package app.services.svgEngine;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SvgTest {

    private static final Svg svg = new Svg("0", "0", "100%", "0 0 100 100");

//    @BeforeAll
//    static void setUpClass() {
//        Svg svg = new Svg("0", "0", "100%", "0 0 100 100");
//
//    }

//    @Test
//    void emptySvg() {
//

    /// /        Svg svg = new Svg("0", "0", "100%", "0 0 100 100");
//
//
//        assertEquals("<svg version=\"1.1\" x=\"0\" y=\"0\" width=\"100%\" viewBox=\"0 0 100 100\" preserveAspectRatio=\"xMinYMin\"><defs>\n" +
//                "        <marker id=\"beginArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"0\" refY=\"6\" orient=\"auto\">\n" +
//                "            <path d=\"M0,6 L12,0 L12,12 L0,6\" style=\"fill: #000000;\" />\n" +
//                "        </marker>\n" +
//                "        <marker id=\"endArrow\" markerWidth=\"12\" markerHeight=\"12\" refX=\"12\" refY=\"6\" orient=\"auto\">\n" +
//                "            <path d=\"M0,0 L12,6 L0,12 L0,0 \" style=\"fill: #000000;\" />\n" +
//                "        </marker>\n" +
//                "    </defs></svg>", svg.toString());
//
//
//    }
    @Test
    void addText() {
        String expected = "<text x=\"50.000000\" y=\"100.000000\" text-anchor=\"middle\" alignment-baseline=\"middle\" transform=\"rotate(90 50.000000 100.000000)\">Lorem Ipsum</text>";
        String actual = svg.addText("Lorem Ipsum", 50,100, 90);
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
