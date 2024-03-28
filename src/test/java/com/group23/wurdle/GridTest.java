package com.group23.wurdle;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.grid.GridGen;

/**
 * Unit test for simple App.
 */
public class GridTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void testGridGen() {
        GridGen g = new GridGen("/home/skynse/projects/wurdle/src/main/java/uta/group23/wurdle/words.txt");
    }

    @Test
    public void printGrid() throws Exception {
        Grid g = new Grid(50, 50);
        GridGen gen = new GridGen("/home/skynse/projects/wurdle/src/main/java/uta/group23/wurdle/words.txt");
        gen.generateGrid(g);

        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                System.out.print(g.getGrid()[i][j].getLetter() + " ");
            }
            System.out.println();
        }

    }

}
