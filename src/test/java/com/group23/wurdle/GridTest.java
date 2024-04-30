package com.group23.wurdle;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uta.group23.wurdle.grid.Cell;
import uta.group23.wurdle.grid.Grid;
import uta.group23.wurdle.grid.GridGen;
import uta.group23.wurdle.models.Colour;
import uta.group23.wurdle.models.Player;
import uta.group23.wurdle.socket.Lobby;
import uta.group23.wurdle.socket.Mode;
import uta.group23.wurdle.socket.Status;

/**
 * Unit test for simple App.
 */
public class GridTest {
    /**
     * Rigorous Test :-)
     */

    @Test
    public void TestWordListValid() throws Exception {

        Lobby l = new Lobby("lobbyName", "lobbyID", Status.WAITING, 0, Mode.Timer, "password", 4,
                new Player("bot", Colour.Red));

        assert (l.getGame().getGrid().getWords().size() == 0);
        GridGen gen = new GridGen();

        gen.setWordList("src/words.txt");
        gen.generateGrid(l.getGame().getGrid());

        assert (l.getGame().getGrid().getWords().size() > 0);
    }

    @Test
    public void testGridGenWithWordlist() throws Exception {
        Grid g = new Grid(20, 20);
        GridGen gen = new GridGen();

        gen.setWordList("src/words.txt");

        gen.generateGrid(g);

        // print the grid
        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {

                System.out.print(g.getCell(i, j).getLetter() + " ");

            }
            System.out.println();
        }
    }

    private void add10Words(GridGen g, Grid grid) {
        g.addWordToGrid("Trung", grid);
        g.addWordToGrid("Austin", grid);
        g.addWordToGrid("Wurdle", grid);
        g.addWordToGrid("Java", grid);
        g.addWordToGrid("Computer", grid);
        g.addWordToGrid("Science", grid);
        g.addWordToGrid("Engineering", grid);
        g.addWordToGrid("Software", grid);
        g.addWordToGrid("Development", grid);
        g.addWordToGrid("Test", grid);

        g.fillEmptyCells(grid);
    }

    // Commented out to ensure deploy success -> make sure this test works later

    @Test
    public void canFindWordMultiple() {
        // might not pass all the time, but is meant to make sure that
        // the word list is being read correctly and actually contains valid words from
        // the list

        GridGen gen = new GridGen();
        Grid grid = new Grid(20, 20);
        gen.addWordToGrid("Trung", grid);
        gen.addWordToGrid("Austin", grid);

        assertTrue(GridGen.wordExists("Trung", grid));
        assertTrue(GridGen.wordExists("Austin", grid));

        // print the grid
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {

                System.out.print(grid.getCell(i, j).getLetter() + " ");

            }
            System.out.println();
        }

    }

    @Test
    public void validGrid() throws Exception {
        Grid g = new Grid(50, 50);

        GridGen gen = new GridGen();
        add10Words(gen, g);
        // Generate the grid
        // Check each cell in the grid
        for (int i = 0; i < g.getWidth(); i++) {
            for (int j = 0; j < g.getHeight(); j++) {
                Cell c = g.getCell(i, j);

                // Ensure that each cell has a letter (not empty)
                assertNotEquals(' ', c.getLetter());
            }
        }
    }

    @Test
    public void validGrid2() throws Exception {
        // visual test
        Grid g = new Grid(20, 20);

        GridGen gen = new GridGen();
        add10Words(gen, g);

        System.out.println("Char count: " + g.getCharCount());
        g.calculateDensity();
        System.out.println("Grid density: " + g.getDensity());
        System.out.println("Words: " + g.getWords().size());

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

        GridGen gen = new GridGen();
        add10Words(gen, g);
        long end = System.currentTimeMillis();

        assertTrue(end - start < 1000);

    }

}
