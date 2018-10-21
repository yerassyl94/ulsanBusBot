package net.daum.android.map.util;

public class URLEncoder {
    static final String digits = "0123456789ABCDEF";

    private URLEncoder() {
    }

    public static String encode(byte[] data) {
        StringBuilder buf = new StringBuilder(data.length + 16);
        for (int i = 0; i < data.length; i++) {
            char ch = (char) data[i];
            if ((ch >= 'a' && ch <= 'z') || ((ch >= 'A' && ch <= 'Z') || ((ch >= '0' && ch <= '9') || ".-*_".indexOf(ch) > -1))) {
                buf.append(ch);
            } else if (ch == ' ') {
                buf.append('+');
            } else {
                buf.append('%');
                buf.append(digits.charAt((data[i] & 240) >> 4));
                buf.append(digits.charAt(data[i] & 15));
            }
        }
        return buf.toString();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int decode(java.lang.String r10, byte[] r11) {
        /*
        r3 = r10.length();
        r2 = 0;
        r4 = 0;
        r5 = r4;
    L_0x0007:
        if (r2 >= r3) goto L_0x0070;
    L_0x0009:
        r0 = r10.charAt(r2);
        switch(r0) {
            case 37: goto L_0x0031;
            case 43: goto L_0x0019;
            default: goto L_0x0010;
        };
    L_0x0010:
        r4 = r5 + 1;
        r7 = (byte) r0;
        r11[r5] = r7;
        r2 = r2 + 1;
        r5 = r4;
        goto L_0x0007;
    L_0x0019:
        r4 = r5 + 1;
        r7 = 32;
        r11[r5] = r7;
        r2 = r2 + 1;
        r5 = r4;
        goto L_0x0007;
    L_0x0023:
        r4 = r5 + 1;
        r7 = (byte) r6;
        r11[r5] = r7;	 Catch:{ NumberFormatException -> 0x0071 }
        r2 = r2 + 3;
        if (r2 >= r3) goto L_0x0030;
    L_0x002c:
        r0 = r10.charAt(r2);	 Catch:{ NumberFormatException -> 0x0071 }
    L_0x0030:
        r5 = r4;
    L_0x0031:
        r7 = r2 + 2;
        if (r7 >= r3) goto L_0x0007;
    L_0x0035:
        r7 = 37;
        if (r0 != r7) goto L_0x0007;
    L_0x0039:
        r7 = r2 + 1;
        r8 = r2 + 3;
        r7 = r10.substring(r7, r8);	 Catch:{ NumberFormatException -> 0x0051 }
        r8 = 16;
        r6 = java.lang.Integer.parseInt(r7, r8);	 Catch:{ NumberFormatException -> 0x0051 }
        if (r6 >= 0) goto L_0x0023;
    L_0x0049:
        r7 = new java.lang.IllegalArgumentException;	 Catch:{ NumberFormatException -> 0x0051 }
        r8 = "URLDecoder: Illegal hex characters in escape (%) pattern - negative value";
        r7.<init>(r8);	 Catch:{ NumberFormatException -> 0x0051 }
        throw r7;	 Catch:{ NumberFormatException -> 0x0051 }
    L_0x0051:
        r1 = move-exception;
        r4 = r5;
    L_0x0053:
        r7 = new java.lang.IllegalArgumentException;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "URLDecoder: Illegal hex characters in escape (%) pattern - ";
        r8 = r8.append(r9);
        r9 = r1.getMessage();
        r8 = r8.append(r9);
        r8 = r8.toString();
        r7.<init>(r8);
        throw r7;
    L_0x0070:
        return r5;
    L_0x0071:
        r1 = move-exception;
        goto L_0x0053;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.daum.android.map.util.URLEncoder.decode(java.lang.String, byte[]):int");
    }
}
