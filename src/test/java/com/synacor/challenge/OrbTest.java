package com.synacor.challenge;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;

import static com.synacor.challenge.Orb.*;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrbTest {

    private static Orb orb;

    @BeforeAll
    static void setup() {
        orb = new Orb();
    }

    @Test
    void testMaze() {
        final String[] maze = VM.MAZE;
        final Queue<String> expected =
                new LinkedList<>(asList(NORTH, EAST, EAST, NORTH, WEST, SOUTH, EAST, EAST, WEST, NORTH, NORTH, EAST));
        assertEquals(expected, orb.solveOrb(maze));
    }
}