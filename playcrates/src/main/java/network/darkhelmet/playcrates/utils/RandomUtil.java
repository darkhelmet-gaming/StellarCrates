package network.darkhelmet.playcrates.utils;

import java.util.Random;

public class RandomUtil {
    /**
     * RNG
     */
    protected static Random random = new Random();

    /**
     * Prevent instantiation
     */
    private RandomUtil() {}

    /**
     * Get a random double within a given range.
     *
     * @param min The mininum barrier
     * @param max The maximum barrier
     * @return The random double
     */
    public static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        return scaled + min;
    }
}
