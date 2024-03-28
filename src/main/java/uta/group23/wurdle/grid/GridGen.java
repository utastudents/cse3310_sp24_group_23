package uta.group23.wurdle.grid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Timer;

enum Direction {
    Horizontal,
    Vertical,
    Diagonal
}

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
        assert this.wordList.exists();

    }

    public GridGen() {
    }

    public void getDensity(int startIndex, int endIndex) {
    }

    public int getCellIndex(int x, int y, Grid grid) {
        return x * grid.getWidth() + y;
    }

    public static boolean wordExists(String word, Grid grid) {
        // check if word exists in grid
        // adapted from the paper
        // https://ijses.com/wp-content/uploads/2022/01/68-IJSES-V6N1.pdf
        boolean found = false;
        for (int i = 0; i < grid.getWidth(); i++) {
            for (int j = 0; j < grid.getHeight(); j++) {
                if (grid.getCell(i, j).getLetter() == word.charAt(0)) {
                    // scan

                    // scan right
                    if (j + word.length() <= grid.getWidth()) { // check if word fits in length
                        found = true;
                        // we need to see if any char breaks the word
                        for (int k = 1; k < word.length(); k++) {
                            if (grid.getCell(i, j + k).getLetter() != word.charAt(k)) {
                                found = false;
                                break;
                            }
                        }
                        if (found) {
                            return true;
                        }

                        // scan left
                        if (j - word.length() >= 0) { // check if word fits in length
                            found = true;
                            // we need to see if any char breaks the word
                            for (int k = 1; k < word.length(); k++) {
                                if (grid.getCell(i, j - k).getLetter() != word.charAt(k)) {
                                    found = false;
                                    break;
                                }
                            }
                            if (found) {
                                return true;
                            }
                        }

                        // scan down

                        if (i + word.length() <= grid.getHeight()) { // check if word fits in length
                            found = true;
                            // we need to see if any char breaks the word
                            for (int k = 1; k < word.length(); k++) {
                                if (grid.getCell(i + k, j).getLetter() != word.charAt(k)) {
                                    found = false;
                                    break;
                                }
                            }
                            if (found) {
                                return true;
                            }
                        }

                        // scan up
                        if (i - word.length() >= 0) { // check if word fits in length
                            found = true;
                            // we need to see if any char breaks the word
                            for (int k = 1; k < word.length(); k++) {
                                if (grid.getCell(i - k, j).getLetter() != word.charAt(k)) {
                                    found = false;
                                    break;
                                }
                            }
                            if (found) {
                                return true;
                            }

                            // scan down diagonal
                            if (i + word.length() <= grid.getHeight() && j + word.length() <= grid.getWidth()) { // check
                                                                                                                 // if
                                                                                                                 // word
                                                                                                                 // fits
                                                                                                                 // in
                                                                                                                 // length
                                found = true;
                                // we need to see if any char breaks the word
                                for (int k = 1; k < word.length(); k++) {
                                    if (grid.getCell(i + k, j + k).getLetter() != word.charAt(k)) {
                                        found = false;
                                        break;
                                    }
                                }
                                if (found) {
                                    return true;
                                }
                            }

                            // scan up diagonal
                            if (i - word.length() >= 0 && j - word.length() >= 0) { // check if word fits in length
                                found = true;
                                // we need to see if any char breaks the word
                                for (int k = 1; k < word.length(); k++) {
                                    if (grid.getCell(i - k, j - k).getLetter() != word.charAt(k)) {
                                        found = false;
                                        break;
                                    }
                                }
                                if (found) {
                                    return true;
                                }
                            }
                        }
                    }

                }

            }
        }
        return false;
    }

    public void addWordToGrid(String word, Grid grid) {

        // adapted from the paper
        // https://ijses.com/wp-content/uploads/2022/01/68-IJSES-V6N1.pdf
        // get random starting point
        int x = (int) (Math.random() * grid.getWidth());
        int y = (int) (Math.random() * grid.getHeight());

        // get random direction
        Direction dir = Direction.values()[(int) (Math.random() * 3)];

        // get random operation mode
        op = OperationMode.values()[(int) (Math.random() * 2)];

        // check if word fits in grid
        if (word.length() > grid.getWidth() || word.length() > grid.getHeight()) {
            return;
        }

        // check if word fits in grid
        if (dir == Direction.Horizontal) {
            if (op == OperationMode.Forward) {
                if (y + word.length() > grid.getWidth()) {
                    return;
                }
            } else {
                if (y - word.length() < 0) {
                    return;
                }
            }
        } else if (dir == Direction.Vertical) {
            if (op == OperationMode.Forward) {
                if (x + word.length() > grid.getHeight()) {
                    return;
                }
            } else {
                if (x - word.length() < 0) {
                    return;
                }
            }
        } else {
            if (op == OperationMode.Forward) {
                if (x + word.length() > grid.getHeight() || y + word.length() > grid.getWidth()) {
                    return;
                }
            } else {
                if (x - word.length() < 0 || y - word.length() < 0) {
                    return;
                }
            }
        }

        // add word to grid
        for (int i = 0; i < word.length(); i++) {
            // word scope
            // according to the paper, it should be easy to add
            // diagonal words
            if (dir == Direction.Horizontal) {
                // horizontal forward
                if (op == OperationMode.Forward) {
                    grid.setCell(x, y + i, word.charAt(i));
                } else {
                    grid.setCell(x, y - i, word.charAt(i));
                }
            } else if (dir == Direction.Vertical) {
                if (op == OperationMode.Forward) {
                    // vertical forward
                    grid.setCell(x + i, y, word.charAt(i));
                } else {
                    // vertical backward
                    grid.setCell(x - i, y, word.charAt(i));
                }
            } else {
                // diagonal
                if (op == OperationMode.Forward) {
                    grid.setCell(x + i, y + i, word.charAt(i));
                }

                else {
                    grid.setCell(x - i, y - i, word.charAt(i));
                }

            }
        }
    }

    public void generateGrid(Grid grid) throws Exception {
        // make sure word fits in grid and when the grid is full, stop
        // if the word fits, add it to the grid

        BufferedReader reader = new BufferedReader(new FileReader(wordList));

        String line = reader.readLine();
        while (line != null) {
            if (line.length() <= grid.getWidth() && line.length() <= grid.getHeight()) {
                // add word to grid
                addWordToGrid(line, grid);
            }
            line = reader.readLine();
        }
    }

}
