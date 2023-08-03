package com.portabull.utils.encoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EncoderUtils {

    private final static Map<Character, Character> encoders;

    private final static Map<Character, Character> decoders;

    private static Map<Character, Character> tempDecoders;

    private static Map<Character, Character> tempEncoders;

    static {
        loadMappers();
        encoders = Collections.unmodifiableMap(tempEncoders);
        decoders = Collections.unmodifiableMap(tempDecoders);
    }

    public static String encode(String value) {
        StringBuilder builder = new StringBuilder();

        char[] chars = value.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char a;

            try {
                a = encoders.get(chars[i]);
            } catch (NullPointerException e) {
                a = chars[i];
            }

            builder.append(a);
        }

        return builder.toString();
    }

    public static String decode(String value) {
        StringBuilder builder = new StringBuilder();

        char[] chars = value.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char a;

            try {
                a = decoders.get(chars[i]);
            } catch (NullPointerException e) {
                a = chars[i];
            }

            builder.append(a);
        }

        return builder.toString();
    }

    private static void loadMappers() {
        tempEncoders = new HashMap<>();
        tempEncoders.put('a', 'o');
        tempEncoders.put('b', 'i');
        tempEncoders.put('c', 'p');
        tempEncoders.put('d', 'j');
        tempEncoders.put('e', 'l');
        tempEncoders.put('f', 'g');
        tempEncoders.put('g', 'y');
        tempEncoders.put('h', 'a');
        tempEncoders.put('i', 'q');
        tempEncoders.put('j', 'c');
        tempEncoders.put('k', 'f');
        tempEncoders.put('l', 'x');
        tempEncoders.put('m', 'w');
        tempEncoders.put('n', 'v');
        tempEncoders.put('o', 'z');
        tempEncoders.put('p', 'b');
        tempEncoders.put('q', 'h');
        tempEncoders.put('r', 's');
        tempEncoders.put('s', 'r');
        tempEncoders.put('t', 'm');
        tempEncoders.put('u', 'd');
        tempEncoders.put('v', 'u');
        tempEncoders.put('w', 'k');
        tempEncoders.put('x', 'n');
        tempEncoders.put('y', 't');
        tempEncoders.put('z', 'e');


        tempEncoders.put('A', 'E');
        tempEncoders.put('B', 'T');
        tempEncoders.put('C', 'N');
        tempEncoders.put('D', 'K');
        tempEncoders.put('E', 'U');
        tempEncoders.put('F', 'D');
        tempEncoders.put('G', 'M');
        tempEncoders.put('H', 'R');
        tempEncoders.put('I', 'S');
        tempEncoders.put('J', 'H');
        tempEncoders.put('K', 'B');
        tempEncoders.put('L', 'Z');
        tempEncoders.put('M', 'V');
        tempEncoders.put('N', 'W');
        tempEncoders.put('O', 'X');
        tempEncoders.put('P', 'F');
        tempEncoders.put('Q', 'C');
        tempEncoders.put('R', 'Q');
        tempEncoders.put('S', 'A');
        tempEncoders.put('T', 'Y');
        tempEncoders.put('U', 'G');
        tempEncoders.put('V', 'L');
        tempEncoders.put('W', 'J');
        tempEncoders.put('X', 'P');
        tempEncoders.put('Y', 'I');
        tempEncoders.put('Z', 'O');


        tempEncoders.put('1', '5');
        tempEncoders.put('2', '7');
        tempEncoders.put('3', '4');
        tempEncoders.put('4', '3');
        tempEncoders.put('5', '0');
        tempEncoders.put('6', '9');
        tempEncoders.put('7', '2');
        tempEncoders.put('8', '1');
        tempEncoders.put('9', '8');
        tempEncoders.put('0', '6');

        tempEncoders.put(' ', '-');

        tempDecoders = new HashMap<>();
        tempEncoders.forEach((k, v) ->
                tempDecoders.put(v, k)
        );
    }
}
