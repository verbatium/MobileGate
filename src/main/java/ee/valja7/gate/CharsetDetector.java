package ee.valja7.gate;

import java.nio.charset.Charset;
import java.util.Arrays;

class CharsetDetector {
    private final static String iso8859_13 = "öäüõžš" + "öäüõžš".toUpperCase();
    private final static String iso8859_5 = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" + "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toUpperCase();
    private final static byte[] iso8859_13Bytes = iso8859_13.getBytes(Charset.forName("ISO-8859-13"));
    private final static byte[] iso8859_5Bytes = iso8859_5.getBytes(Charset.forName("ISO-8859-5"));

    static String detect(byte[] buffer) {
        return detect(buffer, 0, buffer.length);
    }

    static String detect(byte[] buffer, int position, int size) {
        int e3 = 0;
        int e5 = 0;
        Arrays.sort(iso8859_13Bytes);
        Arrays.sort(iso8859_5Bytes);
        for (int i = position; i < position + size; i++) {
            if ((char) buffer[i] > (char) 127) {
                if (isBaltic(buffer[i])) e3++;
                if (isCyrillic(buffer[i])) e5++;
            }
        }
        if (e3 == 0 && e5 == 0) return "ASCII";
        if (e3 >= e5) return "ISO-8859-13";
        return "ISO-8859-5";
    }

    static boolean isBaltic(String c) {
        return iso8859_13.contains(c);
    }

    static boolean isCyrillic(String c) {
        return iso8859_5.contains(c);
    }

    static boolean isBaltic(byte b) {
        return Arrays.binarySearch(iso8859_13Bytes, b) >= 0;
    }

    static boolean isCyrillic(byte b) {
        return Arrays.binarySearch(iso8859_5Bytes, b) >= 0;
    }

    static String detect(String s) {
        int e3 = 0;
        int e5 = 0;
        Arrays.sort(iso8859_13Bytes);
        Arrays.sort(iso8859_5Bytes);
        for (int i = 0; i < s.length(); i++) {
            String ss = s.substring(i, i + 1);
            if (isBaltic(ss)) e3++;
            if (isCyrillic(ss)) e5++;
        }
        if (e3 == 0 && e5 == 0) return "ASCII";
        if (e3 >= e5) return "ISO-8859-13";
        return "ISO-8859-5";
    }
}
