package nl.rug.aoop.asteroids.util;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Randomizers class -  utility class used to generate random alphanumeric ID keys,
 * random ints, doubles,longs ;
 */
public class Randomizer {
    /**
     * Generate a random alphanumeric id for the room. Used for both
     * identification of rooms in the world construction methods and
     * the scattering probability of item subtypes per room using RegEx patterns.
     */
    private static final SecureRandom rand = new SecureRandom();
    private char[] strand;
    private Random random;
    private char[] chars;
    public static final String upcase = "ABCDGWXYZ";
    public static final String lowcase = upcase.toLowerCase(Locale.ROOT);
    public static final String digits = "1234567890";
    public static final String allchar = upcase + lowcase + digits;

    public Randomizer(int length, Random random, String symbols) {
        if (length < 1)
            throw new IllegalArgumentException();
        if (symbols.length() < 2)
            throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.chars = symbols.toCharArray();
        this.strand = new char[length];
    }

    public String generateId() {
        for (int idx = 0; idx < strand.length; ++idx)
            strand[idx] = chars[random.nextInt(chars.length)];
        return new String(strand);
    }

    public String generateIpv4() {
        StringBuilder builder = new StringBuilder();
        for (int idx = 0; idx < 3; idx++) {
            builder.append(ThreadLocalRandom.current().nextInt(0, 255)).append(".");
        }
        return builder.substring(0, builder.length() - 1);
    }

    public int genRandIx(int b) {
        return rand.nextInt(b);
    }
    public long randomLong(long b) {
        return rand.nextInt(Math.toIntExact(b));
    }


    public <T> T getRandomElement(T[] options) {
        return options[rand.nextInt(options.length)];
    }

    public double randomDoubles(double[] options) {
        double out = options[genRandIx(options.length)];
        return rand.nextDouble() * out;
    }

    public double randomPercentage(double value, double[] options) {
        double out = options[genRandIx(options.length)];
        out *= rand.nextDouble();
        return (value + (value / 100 * out));

    }

    public Randomizer(int length){
        this(length,rand,allchar);
    }

    public Randomizer(int length, Random random) {
        this(length, random, allchar);
    }
}
