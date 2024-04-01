/// BASE CLASS FOR PLUGGABLE GRID GENERATION ALGORITHMS
// CLASS CAN BE INTEGRATED WITH A GRID OBJECT TO GENERATE A GRID

package uta.group23.wurdle.grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Timer;

enum OperationMode {
    Forward,
    Backward
}

public class GridGen {
    private File wordList;
    private int density;
    private int wordCount;

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

    public int getCellIndex(int x, int y, Grid grid) {
        return x * grid.getWidth() + y;
    }

    public static boolean wordExists(String word, Grid grid) {
        word = word.toUpperCase();
        // check if word exists in grid
        // adapted from the paper
        // https://ijses.com/wp-content/uploads/2022/01/68-IJSES-V6N1.pdf
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.getCell(i, j).getLetter() == word.charAt(0)) {
                    if (scanHorizontal(grid, word, i, j) || scanVertical(grid, word, i, j)
                            || scanDiagonal(grid, word, i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean scanHorizontal(Grid grid, String word, int x, int y) {
        int j = y;
        for (int k = 0; k < word.length(); k++, j++) {
            if (j >= grid.getWidth() || grid.getCell(x, j).getLetter() != word.charAt(k)) {
                break;
            }
            if (k == word.length() - 1) {
                return true;
            }

        }
        // left scan
        j = y;
        for (int k = 0; k < word.length(); k++, j--) {
            if (j < 0 || grid.getCell(x, j).getLetter() != word.charAt(k)) {
                break;
            }
            if (k == word.length() - 1) {
                return true;
            }

        }

        return false;

    }

    public static boolean scanVertical(Grid grid, String word, int x, int y) {
        int i = x;
        for (int k = 0; k < word.length(); k++, i++) {
            if (i >= grid.getHeight() || grid.getCell(i, y).getLetter() != word.charAt(k)) {
                break;
            }
            if (k == word.length() - 1) {
                return true;
            }

        }
        // up scan
        i = x;
        for (int k = 0; k < word.length(); k++, i--) {
            if (i < 0 || grid.getCell(i, y).getLetter() != word.charAt(k)) {
                break;
            }
            if (k == word.length() - 1) {
                return true;
            }

        }

        return false;
    }

    public static boolean scanDiagonal(Grid grid, String word, int x, int y) {
        int i = x;
        int j = y;
        for (int k = 0; k < word.length(); k++, i++, j++) {
            if (i >= grid.getHeight() || j >= grid.getWidth() || grid.getCell(i, j).getLetter() != word.charAt(k)) {
                break;
            }
            if (k == word.length() - 1) {
                return true;
            }

        }
        // up scan
        i = x;
        j = y;
        for (int k = 0; k < word.length(); k++, i--, j--) {
            if (i < 0 || j < 0 || grid.getCell(i, j).getLetter() != word.charAt(k)) {
                break;
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
            } else { // Diagonal
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
            char existingLetter = grid.getCell(currX, currY).letter;
            if (existingLetter != ' ' && existingLetter != word.charAt(i)) {
                return false;
            }
        }

        // No conflicting intersections found
        return true;
    }

    public void addWordToGrid(String word, Grid grid) {
        word = word.toUpperCase();
        // adapted from the paper
        // https://ijses.com/wp-content/uploads/2022/01/68-IJSES-V6N1.pdf
        // get random starting point
        int x = (int) (Math.random() * grid.getWidth());
        int y = (int) (Math.random() * grid.getHeight());
        // get random direction
        Direction dir = Direction.values()[(int) (Math.random() * 3)];
        // get random operation mode
        OperationMode op = OperationMode.values()[(int) (Math.random() * 2)];

        // check if word fits in grid
        if (dir == Direction.Horizontal && y + word.length() > grid.getWidth()) {
            return;
        } else if (dir == Direction.Vertical && x + word.length() > grid.getHeight()) {
            return;
        } else if (dir == Direction.Diagonal
                && (x + word.length() > grid.getHeight() || y + word.length() > grid.getWidth())) {
            return;
        }

        // check if word fits in grid
        if (!isValidPlacement(word, x, y, dir, op, grid)) {
            return;
        }

        // add word to grid
        grid.addCharCount(word.length()); // Add this line to increment the character count

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
            } else { // Diagonal
                if (op == OperationMode.Forward) {
                    currX += i;
                    currY += i;
                } else {
                    currX -= i;
                    currY -= i;
                }
            }
            grid.setCell(currX, currY, word.charAt(i));
        }
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

    public void generateGrid(Grid grid) throws Exception {
        // make sure word fits in grid and when the grid is full, stop
        // if the word fits, add it to the grid

        BufferedReader reader = new BufferedReader(new FileReader(wordList));

        String line = reader.readLine();
        while (line != null && !gridFull(grid)) {
            if (line.length() <= grid.getWidth() && line.length() <= grid.getHeight()) {
                // add word to grid
                addWordToGrid(line, grid);
            }
            line = reader.readLine();
        }

        // fill empty cells
        fillEmptyCells(grid);

        reader.close();
    }

}
