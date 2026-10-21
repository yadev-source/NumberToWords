package com.yadev.numbertowords;

import java.math.BigDecimal;

public class NumberToWords {

    private static final String[] UNITS = {
        "", "SATU", "DUA", "TIGA", "EMPAT", "LIMA",
        "ENAM", "TUJUH", "DELAPAN", "SEMBILAN", "SEPULUH", "SEBELAS"
    };

    private static final String[] SCALES = {
        "", "RIBU", "JUTA", "MILIAR", "TRILIUN", "KUADRILIUN"
    };

    private NumberToWords() {}

    public static String convert(long number) {
        if (number == 0) return "NOL";
        if (number < 0) return "MINUS " + convert(-number);
        if (number == 1000) return "SERIBU";

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

    public static String convert(double number) {
        // Pakai BigDecimal biar gak kena scientific notation & floating error
        return convert(new BigDecimal(String.valueOf(number)).toPlainString());
    }

    public static String convert(String numberStr) {
        if (numberStr == null || numberStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Number string cannot be null or empty");
        }
        numberStr = numberStr.trim().replace(",", ".");
        boolean isNegative = numberStr.startsWith("-");
        if (isNegative) numberStr = numberStr.substring(1);

        String[] parts = numberStr.split("\\.");
        long integerPart;
        String decimalPart = "";

        try {
            integerPart = Long.parseLong(parts[0].isEmpty() ? "0" : parts[0]);
        } catch (NumberFormatException e) {
            // Kalau lebih besar dari Long, ambil 3 digit terakhir aja secara manual
            // untuk library simple, kita fallback ke convert BigDecimal chunk
            // biar gak ribet, kita parse dengan loop yang sama
            integerPart = 0;
            // Untuk super besar, kita pakai logic string
            return convertBigString((isNegative ? "-" : "") + numberStr);
        }

        if (parts.length > 1) {
            decimalPart = parts[1].replaceAll("0+$", "");
            if (decimalPart.length() > 10) decimalPart = decimalPart.substring(0, 10);
        }

        String integerWords = convert(integerPart);
        if (decimalPart.isEmpty()) {
            return isNegative ? "MINUS " + integerWords : integerWords;
        }

        StringBuilder decimalWords = new StringBuilder();
        for (char c : decimalPart.toCharArray()) {
            int digit = c - '0';
            decimalWords.append(digit == 0 ? " NOL" : " " + UNITS[digit]);
        }

        String full = integerWords + " KOMA" + decimalWords;
        return (isNegative ? "MINUS " : "") + full.trim().replaceAll("\\s+", " ");
    }

    private static String convertBigString(String fullNumberStr) {
        // Handler untuk angka super besar via String, tanpa Double
        boolean isNegative = fullNumberStr.startsWith("-");
        if (isNegative) fullNumberStr = fullNumberStr.substring(1);
        String intPart = fullNumberStr.split("\\.")[0];
        
        long number = 0;
        // Sederhana: untuk demo, kita potong per 1000 dari belakang pakai String
        StringBuilder result = new StringBuilder();
        int scale = 0;
        for (int i = intPart.length(); i > 0; i -= 3) {
            int start = Math.max(0, i - 3);
            int chunk = Integer.parseInt(intPart.substring(start, i));
            if (chunk != 0) {
                String cw = convertChunk(chunk, scale);
                result.insert(0, result.length() > 0 ? cw + " " : cw);
            }
            scale++;
        }
        String finalStr = result.length() == 0 ? "NOL" : result.toString().trim();
        return isNegative ? "MINUS " + finalStr : finalStr;
    }

    private static String convertChunk(int number, int scaleIndex) {
        if (number == 0) return "";
        if (scaleIndex == 1 && number == 1) return "SERIBU";

        StringBuilder sb = new StringBuilder();
        int hundreds = number / 100;
        int remainder = number % 100;

        if (hundreds > 0) {
            sb.append(hundreds == 1 ? "SERATUS" : UNITS[hundreds] + " RATUS");
            if (remainder > 0) sb.append(" ");
        }

        if (remainder > 0) {
            if (remainder < 12) {
                sb.append(UNITS[remainder]);
            } else if (remainder < 20) {
                sb.append(UNITS[remainder - 10]).append(" BELAS");
            } else {
                int tens = remainder / 10;
                int units = remainder % 10;
                sb.append(UNITS[tens]).append(" PULUH");
                if (units > 0) sb.append(" ").append(UNITS[units]);
            }
        }

        String chunkResult = sb.toString();
        if (scaleIndex > 0 && !chunkResult.isEmpty()) {
            if (!(scaleIndex == 1 && chunkResult.equals("SERIBU"))) {
                chunkResult += " " + SCALES[scaleIndex];
            }
        }
        return chunkResult;
    }

    public static void main(String[] args) {
        long[] tests = {0, 1, 11, 12, 100, 101, 1000, 1001, 2000, 12345, 1000000, 1234567};
        for (long n : tests) System.out.printf("%,d = %s%n", n, NumberToWords.convert(n));
        System.out.println("123.45 = " + NumberToWords.convert("123.45"));
    }
}