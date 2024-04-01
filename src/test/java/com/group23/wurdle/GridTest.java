package com.group23.wurdle;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.junit.Test;

import uta.group23.wurdle.grid.Cell;
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
    public void canFindWordMultiple() {
        // might not pass all the time, but is meant to make sure that
        // the word list is being read correctly and actually contains valid words from
        // the list

        // wordlist is in same directory as the test
        URL path = GridTest.class.getResource("./words.txt");
        File f = new File(path.getFile());

        GridGen gen = new GridGen();
        Grid grid = new Grid(20, 20);
        gen.addWordToGrid("Trung", grid);
        gen.addWordToGrid("Austin", grid);

        assertTrue(GridGen.wordExists("Trung", grid));
        assertTrue(GridGen.wordExists("Austin", grid));

    }

    @Test
    public void validGrid() throws Exception {
        Grid g = new Grid(50, 50);
        // wordlist is in same directory as the test
        URL path = GridTest.class.getResource("./words.txt");
        File f = new File(path.getFile());

        GridGen gen = new GridGen(f.getAbsolutePath());
        gen.generateGrid(g);

        // a cell MUST have a character, empty is ' ' which is invalid

        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                Cell c = g.getCell(i, j);
                assertTrue(c.getLetter() != ' ');
            }
        }

    }

    @Test
    public void validGrid2() throws Exception {
        // visual test
        Grid g = new Grid(20, 20);
        // wordlist is in same directory as the test
        URL path = GridTest.class.getResource("./words.txt");
        File f = new File(path.getFile());

        GridGen gen = new GridGen(f.getAbsolutePath());
        gen.generateGrid(g);

        System.out.println("Char count: " + g.getCharCount());

        System.out.println("Grid density: " + g.calculateDensity());

        // print the grid
        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {

                System.out.print(g.getCell(i, j).getLetter() + " ");

            }
            System.out.println();
        }

    }

    @Test
    public void gridUnder1Second() throws Exception {
        // test to make sure that the grid generation is under 1 second
        long start = System.currentTimeMillis();
        Grid g = new Grid(50, 50);
        // wordlist is in same directory as the test
        URL path = GridTest.class.getResource("./words.txt");
        File f = new File(path.getFile());

        GridGen gen = new GridGen(f.getAbsolutePath());
        gen.generateGrid(g);
        long end = System.currentTimeMillis();

        assertTrue(end - start < 1000);

    }
}
