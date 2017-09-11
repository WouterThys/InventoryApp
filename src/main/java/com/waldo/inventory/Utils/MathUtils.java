package com.waldo.inventory.Utils;

public class MathUtils {

    /**
     * Greatest Common Divisor of two numbers
     * @param a first number
     * @param b second number
     * @return gcd for a and b
     */
    public static int gcd(int a, int b) {
        while (b > 0) {
            int tmp = b;
            b = a % b;
            a = tmp;
        }
        return a;
    }

    /**
     * Greatest Common Divisor of multiple numbers
     * @param input array of integers
     * @return gcd of all integers in array
     */
    public static int gcd(int[] input) {
        int result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = gcd(result, input[i]);
        }
        return result;
    }

    /**
     * Least Common Multiple of two numbers
     * @param a first number
     * @param b second number
     * @return lcm of a and b
     */
    public static int lcm(int a, int b) {
        return a * (b / gcd(a, b));
    }


    public static int lcm(Integer[] input) {
        int result = input[0];
        for (int i = 1; i < input.length; i++) {
            result = lcm(result, input[i]);
        }
        return result;
    }
}
