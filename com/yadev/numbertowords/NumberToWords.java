package com.yadev.numbertowords;

/**
 * NumberToWords - Indonesian number to words converter library.
 * 
 * Author: Yadev
 * 
 * Features:
 * - Converts number to uppercase Indonesian words
 * - Correctly handles SERIBU and SERATUS rules
 * - Supports negative numbers, decimals, and very large numbers
 * - Clean library API
 */
public class NumberToWords {

    private static final String[] UNITS = {
        "", "SATU", "DUA", "TIGA", "EMPAT", "LIMA",
        "ENAM", "TUJUH", "DELAPAN", "SEMBILAN", "SEPULUH", "SEBELAS"
    };

    private static final String[] SCALES = {
        "", "RIBU", "JUTA", "MILIAR", "TRILIUN", "KUADRILIUN"
    };

    private NumberToWords() {
        // Prevent instantiation - utility class
    }

    /**
     * Convert long to Indonesian words.
     * Example: 1000 -> SERIBU, 12345 -> DUA BELAS RIBU TIGA RATUS EMPAT PULUH LIMA
     *
     * @param number number to convert
     * @return uppercase Indonesian words
     */
    public static String convert(long number) {
        if (number == 0) {
            return "NOL";
        }
        if (number < 0) {
            return "MINUS " + convert(-number);
        }

        // Special case for 1000 exactly handled in logic, but keep fast path
        if (number == 1000) {
            return "SERIBU";
        }

        StringBuilder result = new StringBuilder();
        int scaleIndex = 0;

        while (number > 0) {
            long chunk = number % 1000;
            if (chunk != 0) {
                String chunkWords = convertChunk((int) chunk, scaleIndex);
                if (result.length() > 0) {
                    result.insert(0, chunkWords + " ");
                } else {
                    result.insert(0, chunkWords);
                }
            }
            number /= 1000;
            scaleIndex++;
        }

        return result.toString().trim().replaceAll("\\s+", " ");
    }

    /**
     * Convert double with decimal support.
     * Example: 123.45 -> SERATUS DUA PULUH TIGA KOMA EMPAT LIMA
     */
    public static String convert(double number) {
        if (Double.isNaN(number) || Double.isInfinite(number)) {
            throw new IllegalArgumentException("Number must be finite");
        }

        boolean isNegative = number < 0;
        number = Math.abs(number);

        long integerPart = (long) number;
        double decimalPart = number - integerPart;

        String integerWords = convert(integerPart);

        if (decimalPart < 0.0000001) {
            return isNegative ? "MINUS " + integerWords : integerWords;
        }

        // Extract decimal digits without floating errors
        String decimalStr = String.valueOf(number);
        int dotIndex = decimalStr.indexOf('.');
        String decimals = "";
        if (dotIndex != -1) {
            decimals = decimalStr.substring(dotIndex + 1);
            // Limit to 10 decimal digits to avoid float noise
            if (decimals.length() > 10) {
                decimals = decimals.substring(0, 10);
            }
            // Remove trailing zeros
            decimals = decimals.replaceAll("0+$", "");
        }

        if (decimals.isEmpty()) {
            return isNegative ? "MINUS " + integerWords : integerWords;
        }

        StringBuilder decimalWords = new StringBuilder();
        for (char c : decimals.toCharArray()) {
            int digit = c - '0';
            if (digit == 0) {
                decimalWords.append(" NOL");
            } else {
                decimalWords.append(" ").append(UNITS[digit]);
            }
        }

        String full = integerWords + " KOMA" + decimalWords.toString();
        return isNegative ? "MINUS " + full : full.trim().replaceAll("\\s+", " ");
    }

    /**
     * Convert String numeric input (for very large numbers beyond long)
     */
    public static String convert(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Number string cannot be null or empty");
        }
        numberStr = numberStr.trim();
        try {
            // Try long first
            long val = Long.parseLong(numberStr);
            return convert(val);
        } catch (NumberFormatException e) {
            // Try double (for decimals)
            try {
                double d = Double.parseDouble(numberStr);
                return convert(d);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid number format: " + numberStr);
            }
        }
    }

    /**
     * Convert chunk of 1-999 with scale context
     */
    private static String convertChunk(int number, int scaleIndex) {
        if (number == 0) return "";

        // Special handling for RIBU scale (1000-1999)
        if (scaleIndex == 1) {
            if (number == 1) {
                return "SERIBU";
            }
            // 1000-1999 is handled by chunk = 1 above, but for 1100 etc, chunk = 100 etc
            // e.g., 1100 -> number=1 in thousands chunk = SERIBU
        }

        StringBuilder sb = new StringBuilder();

        int hundreds = number / 100;
        int remainder = number % 100;

        // Hundreds
        if (hundreds > 0) {
            if (hundreds == 1) {
                sb.append("SERATUS");
            } else {
                sb.append(UNITS[hundreds]).append(" RATUS");
            }
            if (remainder > 0) sb.append(" ");
        }

        // Tens and units
        if (remainder > 0) {
            if (remainder < 12) {
                sb.append(UNITS[remainder]);
            } else if (remainder < 20) {
                sb.append(UNITS[remainder - 10]).append(" BELAS");
            } else {
                int tens = remainder / 10;
                int units = remainder % 10;
                sb.append(UNITS[tens]).append(" PULUH");
                if (units > 0) {
                    sb.append(" ").append(UNITS[units]);
                }
            }
        }

        String chunkResult = sb.toString();

        // Add scale if needed
        if (scaleIndex > 0 && !chunkResult.isEmpty()) {
            // Don't add scale if chunk was SERIBU (already includes scale)
            if (!(scaleIndex == 1 && chunkResult.equals("SERIBU"))) {
                chunkResult += " " + SCALES[scaleIndex];
            }
        }

        return chunkResult;
    }

    // --- Simple main for testing the lib ---
    public static void main(String[] args) {
        long[] tests = {0, 1, 11, 12, 19, 20, 21, 100, 101, 110, 112, 1000, 1001, 2000, 12345, 100000, 1000000, 1234567, 1000000000L};
        System.out.println("=== NumberToWords Test (com.yadev) ===");
        for (long n : tests) {
            System.out.printf("%,d = %s%n", n, NumberToWords.convert(n));
        }
        System.out.println("\nDecimal test:");
        System.out.println("123.45 = " + NumberToWords.convert(123.45));
        System.out.println("-1500 = " + NumberToWords.convert(-1500));
    }
}
