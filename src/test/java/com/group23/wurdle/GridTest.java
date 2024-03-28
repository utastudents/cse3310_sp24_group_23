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
    public void canFindWord() {
        // might not pass all the time, but is meant to make sure that
        // the word list is being read correctly and actually contains valid words from
        // the list
        GridGen g = new GridGen("/home/skynse/projects/wurdle/src/main/java/uta/group23/wurdle/words.txt");
        Grid grid = new Grid(50, 50);
        try {
            assertTrue(g.wordExists("worried", grid));
            assertTrue(!g.wordExists("sndsdisndsf", grid));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
