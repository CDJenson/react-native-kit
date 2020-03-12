package com.coship.rnkit.utils;

/**
 *  author: zoujunda 
 *  date: 2019/7/4 13:42 
 *	version: 1.0 
 *  description: One-sentence description
 */
public class HexUtils {

    /**
     * An array of lowercase characters used to create the output of hexadecimal characters
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * An array of uppercase characters used to establish the output of hexadecimal characters
     */
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Converts an array of bytes to an array of hexadecimal characters
     *
     * @param data
     *            byte[]
     * @return hexadecimal char[]
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    /**
     * Converts an array of bytes to an array of hexadecimal characters
     *
     * @param data
     *            byte[]
     * @param toLowerCase
     *            <code>true</code> Convert to lowercase ， <code>false</code> Convert to uppercase
     * @return hexadecimal char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * Converts an array of bytes to an array of hexadecimal characters
     *
     * @param data
     *            byte[]
     * @param toDigits
     *
     * @return hexadecimal char[]
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    /**
     * Converts an array of bytes to a hexadecimal string
     *
     * @param data
     *            byte[]
     * @return hexadecimal char[]
     */
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    /**
     * Converts an array of bytes to a hexadecimal string
     *
     * @param data
     *            byte[]
     * @param toLowerCase
     *            <code>true</code>
     * @return
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * Converts an array of bytes to a hexadecimal string
     *
     * @param data
     *            byte[]
     * @param toDigits
     *
     * @return
     */
    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    /**
     * Converts an array of hexadecimal characters into a byte array
     *
     * @param data
     *
     * @return byte[]
     * @throws RuntimeException
     *
     */
    public static byte[] decodeHex(char[] data) {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * Converts a hexadecimal character to an integer
     *
     * @param ch
     *
     * @param index
     *
     * @return
     * @throws RuntimeException
     *
     */
    protected static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }

}
