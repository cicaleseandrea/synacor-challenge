package com.synacor.challenge;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TeleporterTest {

    static Stream<Arguments> teleportArgs() {
        return Stream.of(
                arguments(0, -1),
                arguments(1, -1),
                arguments(2, 0),
                arguments(3, -1),
                arguments(4, -1),
                arguments(5, 32764),
                arguments(6, 25734)
        );
    }

    @ParameterizedTest
    @MethodSource("teleportArgs")
    void teleport(final int goal, final int result) {
        final Teleporter teleporter = new Teleporter(goal);
        assertEquals(result, teleporter.teleport());
    }
}