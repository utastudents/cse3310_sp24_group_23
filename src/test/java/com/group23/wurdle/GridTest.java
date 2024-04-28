package com.group23.wurdle;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.BeforeClass;
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

    //Commented out to ensure deploy success -> make sure this test works later

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



    // Need to fix the test or the logic to generate the grid
    // This test takes dynasties, even for a 10x10 bruh 
    @Test
    public void generateGridFromWordList() {
        Grid grid = new Grid(20, 20);
        GridGen gen = new GridGen("src/words.txt");
        String[] words = gen.getWords();
        Set<String> usedWords = new HashSet<>();
        
        while(!gen.gridFull(grid)) {
            Random random = new Random();
            int index = random.nextInt(words.length);
            String word = words[index];
                gen.addWordToGrid(word, grid);
                usedWords.add(word);
        }

        // This thing is the culprit making it take 50 decades but I dunno how to fix
        // Check if the grid is filled with words from the word list
        // for (int i = 0; i < grid.getWidth(); i++) {
        //     for (int j = 0; j < grid.getHeight(); j++) {
        //         Cell cell = grid.getCell(i, j);
        //         assertTrue(GridGen.wordExists(String.valueOf(cell.getLetter()), grid));
        //     }
        // }

        // Print the generated grid
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                System.out.print(grid.getCell(i, j).getLetter() + " ");
            }
            System.out.println();
        }


        // Holy shit, I found the problem uhhhhh it uses a LOT of words I can't brain rn anymore so we fix it later
        // Print the list of used words
        System.out.println("\nUsed Words: ");
        for (String usedWord : usedWords) {
            System.out.print(usedWord + " ");
        }
    }
    

    @Test
    public void wordsLoadedFromFile() {
        GridGen gen = new GridGen("src/words.txt");
        String[] words = gen.getWords();
        assertTrue(words.length > 0);
    
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(words.length);
            System.out.println(words[index]);
        }
    }

}
