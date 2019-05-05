package com.synacor.challenge;

import java.util.*;
import java.util.function.IntBinaryOperator;

import static com.synacor.challenge.Orb.Operation.getOperatorBySimbol;

class Orb {
    // don't try too hard :)
    private static final int MAX_STEPS = 14;
    private static final int START = 22;
    private static final int GOAL = 30;
    private static final String END = "1";

    private Orb() {
    }

    static Queue<String> solveOrb() {
        final Cell[][] maze = buildMaze();
        final List<Cell> currPath = new ArrayList<>();
        //START
        currPath.add(maze[4][1]);
        //never go back to START
        maze[4][1] = new Cell(".", 4, 1);
        final List<Cell> res = findShortestPath(maze, MAX_STEPS, currPath, START);
        return buildDirections(res);
    }

    private static Queue<String> buildDirections(final List<Cell> res) {
        final Queue<String> directions = new LinkedList<>();
        for (int i = 1; i < res.size(); i++) {
            final Cell curr = res.get(i);
            final Cell prev = res.get(i - 1);
            final String direction;
            if (curr.j == prev.j && curr.i - 1 == prev.i) {
                direction = "south";
            } else if (curr.i == prev.i && curr.j + 1 == prev.j) {
                direction = "west";
            } else if (curr.i == prev.i && curr.j - 1 == prev.j) {
                direction = "east";
            } else {
                direction = "north";
            }
            directions.add(direction);
        }
        return directions;
    }

    private static Cell[][] buildMaze() {
        final String[] input = (". . . . . .\n" +
                ". * 8 - 1 .\n" +
                ". 4 * 11 * .\n" +
                ". + 4 - 18 .\n" +
                ". 22 - 9 * .\n" +
                ". . . . . .").split("\\s");
        final Cell[][] maze = new Cell[6][6];
        int index = 0;
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze.length; j++) {
                maze[i][j] = new Cell(input[index++], i, j);
            }
        }
        return maze;
    }

    private static List<Cell> findShortestPath(final Cell[][] maze, final int stepsLeft, final List<Cell> currPath,
                                               final int currNumber) {
        List<Cell> shortestPath = Collections.emptyList();
        final Cell currSymbol = currPath.get(currPath.size() - 1);
        //we reached the vault
        if (currSymbol.s.equals(END)) {
            //this is a solution!
            if (currNumber == GOAL) {
                return currPath;
            } else {
                return shortestPath;
            }
        } else if (stepsLeft == 0) {
            return shortestPath;
        }
        for (final Cell neighbour : neighbours(maze, currSymbol)) {
            final List<Cell> newPath = new ArrayList<>(currPath);
            newPath.add(neighbour);
            final int newNumber = compute(currNumber, currSymbol.s, neighbour.s);
            final List<Cell> res2 = findShortestPath(maze, stepsLeft - 1, newPath, newNumber);
            if (!res2.isEmpty() && (shortestPath.isEmpty() || res2.size() < shortestPath.size())) {
                shortestPath = res2;
            }
        }
        return shortestPath;
    }

    private static int compute(final int currNumber, final String symbol, final String number) {
        final IntBinaryOperator operator = getOperatorBySimbol(symbol);
        if (operator != null) {
            return operator.applyAsInt(currNumber, Integer.parseInt(number));
        }
        return currNumber;
    }

    private static List<Cell> neighbours(final Cell[][] maze, final Cell c) {
        final List<Cell> res = new ArrayList<>();
        final int i = c.i;
        final int j = c.j;
        if (!maze[i - 1][j].s.equals(".")) {
            res.add(maze[i - 1][j]);
        }
        if (!maze[i + 1][j].s.equals(".")) {
            res.add(maze[i + 1][j]);
        }
        if (!maze[i][j - 1].s.equals(".")) {
            res.add(maze[i][j - 1]);
        }
        if (!maze[i][j + 1].s.equals(".")) {
            res.add(maze[i][j + 1]);
        }
        return res;
    }

    enum Operation {
        PLUS("+", (a, b) -> a + b), MINUS("-", (a, b) -> a - b), MULT("*", (a, b) -> a * b);
        private final String symbol;
        private final IntBinaryOperator operator;
        private static final Map<String, IntBinaryOperator> SYMBOL_TO_OPERATOR = new HashMap<>();

        static {
            for (final Operation s : Operation.values()) {
                SYMBOL_TO_OPERATOR.put(s.symbol, s.operator);
            }
        }

        Operation(final String symbol, final IntBinaryOperator operator) {
            this.symbol = symbol;
            this.operator = operator;
        }

        static IntBinaryOperator getOperatorBySimbol(final String symbol) {
            return SYMBOL_TO_OPERATOR.get(symbol);
        }
    }

    private static class Cell {
        private final String s;
        private final int i;
        private final int j;

        Cell(final String s, final int i, final int j) {
            this.s = s;
            this.i = i;
            this.j = j;
        }
    }
}
