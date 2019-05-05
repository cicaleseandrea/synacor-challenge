package com.synacor.challenge;

import java.util.HashMap;
import java.util.Map;

class Teleporter {
    private Teleporter() {
    }

    private static int solution = 0;
    //used for memoization
    private static final Map<Integer, Integer> cache = new HashMap<>();

    static int teleport() {
        while (solution < VM.MODULO) {
            cache.clear();
            if (optimizedAckermann41(optimizedAckermann41(solution)) == 6) {
                return solution;
            }
            solution++;
        }
        return 0;
    }

    private static int optimizedAckermann41(final int y) {
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
