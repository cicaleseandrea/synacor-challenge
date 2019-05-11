package com.synacor.challenge;

import com.google.common.base.Preconditions;

import java.util.*;
import java.util.function.IntBinaryOperator;


class Orb {
    // don't try too hard :)
    private static final int MAX_STEPS = 14;
    private static final int START = 22;
    private static final int GOAL = 30;
    private static final String END = "1";

    static final String NORTH = "north";
    static final String EAST = "east";
    static final String WEST = "west";
    static final String SOUTH = "south";

    private static final Map<String, IntBinaryOperator> SYMBOL_TO_OPERATOR = new HashMap<>();

    static {
        SYMBOL_TO_OPERATOR.put("+", (a, b) -> a + b);
        SYMBOL_TO_OPERATOR.put("-", (a, b) -> a - b);
        SYMBOL_TO_OPERATOR.put("*", (a, b) -> a * b);
    }

    Queue<String> solveOrb(final String[] input) {
        final Cell[][] maze = buildMaze(input);
        Preconditions.checkArgument(maze.length == 6);
        Preconditions.checkArgument(maze[0].length == 6);
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
                direction = SOUTH;
            } else if (curr.i == prev.i && curr.j + 1 == prev.j) {
                direction = WEST;
            } else if (curr.i == prev.i && curr.j - 1 == prev.j) {
                direction = EAST;
            } else {
                direction = NORTH;
            }
            directions.add(direction);
        }
        return directions;
    }

    private static Cell[][] buildMaze(final String[] input) {
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
        if (SYMBOL_TO_OPERATOR.containsKey(symbol)) {
            return SYMBOL_TO_OPERATOR.get(symbol).applyAsInt(currNumber, Integer.parseInt(number));
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
