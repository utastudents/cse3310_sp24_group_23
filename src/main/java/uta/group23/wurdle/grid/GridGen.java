/// BASE CLASS FOR PLUGGABLE GRID GENERATION ALGORITHMS
// CLASS CAN BE INTEGRATED WITH A GRID OBJECT TO GENERATE A GRID

package uta.group23.wurdle.grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;

public class GridGen {
    private File wordList;
    private int density;

    enum OperationMode {
        Forward,
        Backward
    }

    private OperationMode op;

    public GridGen(String wordList) {
        this.wordList = new File(wordList);
        if (!this.wordList.exists() || !this.wordList.isFile()) {
            throw new IllegalArgumentException("Invalid word list file path: " + this.wordList.getAbsolutePath());
        }
    }

    public GridGen() {
        this.wordList = null;
    }

    // public int getCellIndex(int x, int y, Grid grid) {
    // return x * grid.getWidth() + y;
    // }

    public static boolean wordExists(String word, Grid grid) {
        word = word.toUpperCase();
        // check if word exists in grid
        // adapted from the paper
        // https://ijses.com/wp-content/uploads/2022/01/68-IJSES-V6N1.pdf
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.getCell(i, j).getLetter() == word.charAt(0)) {
                    if (scanHorizontal(grid, word, i, j) || scanVertical(grid, word, i, j)
                            || scanDiagonalUp(grid, word, i, j) || scanDiagonalDown(grid, word, i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean scanHorizontal(Grid grid, String word, int x, int y) {
        int j = y;
        for (int k = 0; k < word.length(); k++, j++) {
            if (j >= grid.getWidth() || grid.getCell(x, j).getLetter() != word.charAt(k)) {
                // Check if the word is in the reverse direction
                j = y;
                for (int l = 0; l < word.length(); l++, j--) {
                    if (j < 0 || grid.getCell(x, j).getLetter() != word.charAt(l)) {
                        break;
                    }
                    if (l == word.length() - 1) {
                        return true;
                    }
                }
                return false;
            }
            if (k == word.length() - 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean scanVertical(Grid grid, String word, int x, int y) {
        int i = x;
        for (int k = 0; k < word.length(); k++, i++) {
            if (i >= grid.getHeight() || grid.getCell(i, y).getLetter() != word.charAt(k)) {
                // Check if the word is in the reverse direction
                i = x;
                for (int l = 0; l < word.length(); l++, i--) {
                    if (i < 0 || grid.getCell(i, y).getLetter() != word.charAt(l)) {
                        break;
                    }
                    if (l == word.length() - 1) {
                        return true;
                    }
                }
                return false;
            }
            if (k == word.length() - 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean scanDiagonalUp(Grid grid, String word, int x, int y) {
        int i = x;
        int j = y;
        for (int k = 0; k < word.length(); k++, i--, j++) {
            if (i < 0 || j >= grid.getWidth() || grid.getCell(i, j).getLetter() != word.charAt(k)) {
                // Check if the word is in the reverse direction
                i = x;
                j = y;
                for (int l = 0; l < word.length(); l++, i++, j--) {
                    if (i >= grid.getHeight() || j < 0 || grid.getCell(i, j).getLetter() != word.charAt(l)) {
                        break;
                    }
                    if (l == word.length() - 1) {
                        return true;
                    }
                }
                return false;
            }
            if (k == word.length() - 1) {
                return true;
            }
        }
        return false;
    }

    private static boolean scanDiagonalDown(Grid grid, String word, int x, int y) {
        int i = x;
        int j = y;
        for (int k = 0; k < word.length(); k++, i++, j++) {
            if (i >= grid.getHeight() || j >= grid.getWidth() || grid.getCell(i, j).getLetter() != word.charAt(k)) {
                // Check if the word is in the reverse direction
                i = x;
                j = y;
                for (int l = 0; l < word.length(); l++, i--, j--) {
                    if (i < 0 || j < 0 || grid.getCell(i, j).getLetter() != word.charAt(l)) {
                        break;
                    }
                    if (l == word.length() - 1) {
                        return true;
                    }
                }
                return false;
            }
            if (k == word.length() - 1) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidPlacement(String word, int x, int y, Direction dir, OperationMode op, Grid grid) {
        for (int i = 0; i < word.length(); i++) {
            int currX = x;
            int currY = y;

            if (dir == Direction.Horizontal) {
                if (op == OperationMode.Forward) {
                    currY += i;
                } else {
                    currY -= i;
                }
            } else if (dir == Direction.Vertical) {
                if (op == OperationMode.Forward) {
                    currX += i;
                } else {
                    currX -= i;
                }
            } else if (dir == Direction.DiagonalUp) {
                if (op == OperationMode.Forward) {
                    currX -= i;
                    currY += i;
                } else {
                    currX += i;
                    currY -= i;
                }
            } else { // Remaining Diection.DiagonalDown
                if (op == OperationMode.Forward) {
                    currX += i;
                    currY += i;
                } else {
                    currX -= i;
                    currY -= i;
                }
            }

            // Check if the current cell is out of bounds
            if (currX < 0 || currX >= grid.getHeight() || currY < 0 || currY >= grid.getWidth()) {
                return false;
            }

            // Check if there is an existing letter in the current cell
            char existingLetter = grid.getCell(currX, currY).getLetter();
            if (existingLetter != ' ' && existingLetter != word.charAt(i)) {
                return false;
            }
        }

        // No conflicting intersections found
        return true;
    }

    // This version of addWordToGrid tries multiple times to add the word
    public void addWordToGrid(String word, Grid grid) {
        word = word.toUpperCase();
        int maxRetries = 100; // Maximum number of retries to find a valid placement
        int retryCount = 0;

        while (retryCount < maxRetries) {
            // Get random starting point
            int x = (int) (Math.random() * grid.getHeight());
            int y = (int) (Math.random() * grid.getWidth());
            // Get random direction
            Direction dir = getRandomDirection(grid, word);
            if (dir == null) {
                retryCount++;
                continue; // No valid direction found, try again
            }

            // Check if word fits in grid
            if (dir == Direction.Horizontal) {
                if (y + word.length() > grid.getWidth()) {
                    retryCount++;
                    continue;
                }
                if (!canPlaceHorizontally(word, x, y, grid)) {
                    retryCount++;
                    continue;
                }
            } else if (dir == Direction.Vertical) {
                if (x + word.length() > grid.getHeight()) {
                    retryCount++;
                    continue;
                }
                if (!canPlaceVertically(word, x, y, grid)) {
                    retryCount++;
                    continue;
                }
            } else if (dir == Direction.DiagonalUp) {
                if (x - word.length() + 1 < 0 || y + word.length() - 1 >= grid.getWidth()) {
                    retryCount++;
                    continue;
                }
                if (!canPlaceDiagonallyUp(word, x, y, grid)) {
                    retryCount++;
                    continue;
                }
            } else { // DIAGONAL_DOWN
                if (x + word.length() - 1 >= grid.getHeight() || y + word.length() - 1 >= grid.getWidth()) {
                    retryCount++;
                    continue;
                }
                if (!canPlaceDiagonallyDown(word, x, y, grid)) {
                    retryCount++;
                    continue;
                }
            }

            // Add word to grid
            for (int i = 0; i < word.length(); i++) {
                int currX = x;
                int currY = y;
                if (dir == Direction.Horizontal) {
                    grid.setCell(x, y + i, word.charAt(i));
                } else if (dir == Direction.Vertical) {
                    grid.setCell(x + i, y, word.charAt(i));
                } else if (dir == Direction.DiagonalUp) {
                    grid.setCell(x - i, y + i, word.charAt(i));
                } else { // DiagionalDown
                    grid.setCell(x + i, y + i, word.charAt(i));
                }
            }
            grid.addWord(word, dir);
            grid.addCharCount(word.length());
            grid.calculateDensity();
            return; // Word successfully added, return
        }

        // Note: If the word manages to reach past the retry threshold, then that means
        // it didn't find a valid placement x number of times
        // Dunno if we wanna keep this implementation of adding a word
        // If we want, we can choose to skip the word or find a different way to handle
        // it?
    }

    private Direction getRandomDirection(Grid grid, String word) {
        Direction[] directions = Direction.values();
        int maxIndex = 0;
        for (Direction dir : directions) {
            if (isValidDirection(grid, word, dir)) {
                maxIndex++;
            }
        }
        if (maxIndex == 0) {
            return null;
        }
        return directions[(int) (Math.random() * maxIndex)];
    }

    private boolean isValidDirection(Grid grid, String word, Direction dir) {
        if (dir == Direction.Horizontal && word.length() <= grid.getWidth()) {
            return true;
        } else if (dir == Direction.Vertical && word.length() <= grid.getHeight()) {
            return true;
        } else if (dir == Direction.DiagonalUp && word.length() <= Math.min(grid.getHeight(), grid.getWidth())) {
            return true;
        } else if (dir == Direction.DiagonalDown && word.length() <= Math.min(grid.getHeight(), grid.getWidth())) {
            return true;
        }
        return false;
    }

    public boolean gridFull(Grid grid) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.getCell(i, j).getLetter() == ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    public void fillEmptyCells(Grid grid) {
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.getCell(i, j).getLetter() == ' ') {
                    grid.setCell(i, j, (char) (Math.random() * 26 + 'A'));
                }
            }
        }
    }

    // public void generateGrid(Grid grid) throws Exception {
    // // make sure word fits in grid and when the grid is full, stop
    // // if the word fits, add it to the grid

    // BufferedReader reader = new BufferedReader(new FileReader(wordList));

    // String line = reader.readLine();
    // while (line != null && !gridFull(grid)) {
    // if (line.length() <= grid.getWidth() && line.length() <= grid.getHeight()) {
    // // add word to grid
    // addWordToGrid(line, grid);
    // }
    // line = reader.readLine();
    // }

    // // fill empty cells
    // fillEmptyCells(grid);

    // reader.close();
    // }

    public void generateGrid(Grid grid) throws IOException {
        Set<String> usedWords = new HashSet<>();
        System.out.println("Wordlist: " + wordList);
        try (BufferedReader reader = new BufferedReader(new FileReader(wordList))) {
            String line;
            while ((line = reader.readLine()) != null && !gridFull(grid)) {
                if (line.length() <= grid.getWidth() && line.length() <= grid.getHeight()
                        && !usedWords.contains(line.toUpperCase())) {
                    addWordToGrid(line, grid);
                    usedWords.add(line.toUpperCase());
                }
            }
        }

        // Fill empty cells
        fillEmptyCells(grid);

    }

    private boolean canPlaceHorizontally(String word, int x, int y, Grid grid) {
        for (int i = 0; i < word.length(); i++) {
            if (grid.getCell(x, y + i).getLetter() != ' ' && grid.getCell(x, y + i).getLetter() != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlaceVertically(String word, int x, int y, Grid grid) {
        for (int i = 0; i < word.length(); i++) {
            if (grid.getCell(x + i, y).getLetter() != ' ' && grid.getCell(x + i, y).getLetter() != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlaceDiagonallyUp(String word, int x, int y, Grid grid) {
        for (int i = 0; i < word.length(); i++) {
            if (grid.getCell(x - i, y + i).getLetter() != ' '
                    && grid.getCell(x - i, y + i).getLetter() != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean canPlaceDiagonallyDown(String word, int x, int y, Grid grid) {
        for (int i = 0; i < word.length(); i++) {
            if (grid.getCell(x + i, y + i).getLetter() != ' '
                    && grid.getCell(x + i, y + i).getLetter() != word.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public void setWordList(String string) {
        // TODO Auto-generated method stub
        this.wordList = new File(string);
    }

}
