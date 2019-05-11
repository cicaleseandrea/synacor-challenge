package com.synacor.challenge;

import java.util.HashMap;
import java.util.Map;

class Teleporter {
    private final int goal;
    private int solution;
    //used for memoization
    private final Map<Integer, Integer> cache;

    Teleporter(final int goal) {
        solution = 0;
        cache = new HashMap<>();
        this.goal = goal;
    }

    int teleport() {
        while (solution < VM.MODULO) {
            cache.clear();
            if (optimizedAckermann41(optimizedAckermann41(solution)) == goal) {
                return solution;
            }
            solution++;
        }
        return -1;
    }

    private int optimizedAckermann41(final int y) {
        if (cache.containsKey(y)) {
            return cache.get(y);
        }
        int res = solution * (solution + 3) + 1;
        for (int i = 0; i < y; i++) {
            res = res * (solution + 1) + 2 * solution + 1;
        }
        res %= VM.MODULO;
        cache.put(y, res);
        return res;
    }
}
