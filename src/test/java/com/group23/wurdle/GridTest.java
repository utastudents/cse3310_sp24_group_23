package com.group23.wurdle;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.ObjectUtils.Null;
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
    public void canFindWord() {
        // might not pass all the time, but is meant to make sure that
        // the word list is being read correctly and actually contains valid words from
        // the list
        URL path = GridTest.class.getResource("./words.txt");
        File f = new File(path.getFile());

        GridGen gen = new GridGen(f.getAbsolutePath());
        Grid grid = new Grid(50, 50);
        try {
            assertTrue(gen.wordExists("worried", grid));
            assertTrue(!gen.wordExists("sndsdisndsf", grid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validGrid() throws Exception {
        Grid g = new Grid(50, 50);
        // wordlist is in same directory as the test
        URL path = GridTest.class.getResource("./words.txt");
        File f = new File(path.getFile());

        GridGen gen = new GridGen(f.getAbsolutePath());
        gen.generateGrid(g);

        // printing takes too long, so just make sure every cell is filled

        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                assertTrue(g.getGrid()[i][j].getLetter() != ' ');
            }
        }

    }
}
