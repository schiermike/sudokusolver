SudokuSolver
============

This repository contains two solvers: A Java version with a graphical user interface, and an earlier console version written in C. The graphical version is useful to figure out which strategies are applied to solve Sudokus in a step-by-step manner.

The input format is pretty simple. A sudoku riddle is defined by a single line where dots represent unknown entries.

Example:

    6.2.5.........4.3..........43...8....1....2........7..5..27...........81...6.....

is to be read as

    -------------------------
    | 6   2 |   5   |       |
    |       |     4 |   3   |
    |       |       |       |
    -------------------------
    | 4 3   |     8 |       |
    |   1   |       | 2     |
    |       |       | 7     |
    -------------------------
    | 5     | 2 7   |       |
    |       |       |   8 1 |
    |       | 6     |       |
    -------------------------

There are still some Sudoku constellations out there that need to be taken into account! A nice place to get testcases from is [sudokuwiki.org](http://www.sudokuwiki.org/sudoku.htm)