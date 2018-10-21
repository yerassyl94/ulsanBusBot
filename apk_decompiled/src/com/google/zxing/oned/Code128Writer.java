package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class Code128Writer extends UPCEANWriter {
    private static final int CODE_CODE_B = 100;
    private static final int CODE_CODE_C = 99;
    private static final int CODE_START_B = 104;
    private static final int CODE_START_C = 105;
    private static final int CODE_STOP = 106;

    public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Hashtable hints) throws WriterException {
        if (format == BarcodeFormat.CODE_128) {
            return super.encode(contents, format, width, height, hints);
        }
        throw new IllegalArgumentException("Can only encode CODE_128, but got " + format);
    }

    public byte[] encode(String contents) {
        int length = contents.length();
        if (length < 1 || length > 80) {
            throw new IllegalArgumentException("Contents length should be between 1 and 80 characters, but got " + length);
        }
        for (int i = 0; i < length; i++) {
            char c = contents.charAt(i);
            if (c < ' ' || c > '~') {
                throw new IllegalArgumentException("Contents should only contain characters between ' ' and '~'");
            }
        }
        Vector patterns = new Vector();
        int checkSum = 0;
        int checkWeight = 1;
        int codeSet = 0;
        int position = 0;
        while (position < length) {
            int newCodeSet;
            int patternIndex;
            int requiredDigitCount = codeSet == 99 ? 2 : 4;
            if (length - position < requiredDigitCount || !isDigits(contents, position, requiredDigitCount)) {
                newCodeSet = 100;
            } else {
                newCodeSet = 99;
            }
            if (newCodeSet != codeSet) {
                if (codeSet != 0) {
                    patternIndex = newCodeSet;
                } else if (newCodeSet == 100) {
                    patternIndex = 104;
                } else {
                    patternIndex = 105;
                }
                codeSet = newCodeSet;
            } else if (codeSet == 100) {
                patternIndex = contents.charAt(position) - 32;
                position++;
            } else {
                patternIndex = Integer.parseInt(contents.substring(position, position + 2));
                position += 2;
            }
            patterns.addElement(Code128Reader.CODE_PATTERNS[patternIndex]);
            checkSum += patternIndex * checkWeight;
            if (position != 0) {
                checkWeight++;
            }
        }
        patterns.addElement(Code128Reader.CODE_PATTERNS[checkSum % 103]);
        patterns.addElement(Code128Reader.CODE_PATTERNS[106]);
        int codeWidth = 0;
        Enumeration patternEnumeration = patterns.elements();
        while (patternEnumeration.hasMoreElements()) {
            for (int i2 : (int[]) patternEnumeration.nextElement()) {
                codeWidth += i2;
            }
        }
        byte[] result = new byte[codeWidth];
        patternEnumeration = patterns.elements();
        int pos = 0;
        while (patternEnumeration.hasMoreElements()) {
            pos += UPCEANWriter.appendPattern(result, pos, (int[]) patternEnumeration.nextElement(), 1);
        }
        return result;
    }

    private static boolean isDigits(String value, int start, int length) {
        int end = start + length;
        for (int i = start; i < end; i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
}
