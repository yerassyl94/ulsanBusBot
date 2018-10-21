package com.google.zxing.qrcode.decoder;

public final class ErrorCorrectionLevel {
    private static final ErrorCorrectionLevel[] FOR_BITS = new ErrorCorrectionLevel[]{f10M, f9L, f8H, f11Q};
    /* renamed from: H */
    public static final ErrorCorrectionLevel f8H = new ErrorCorrectionLevel(3, 2, "H");
    /* renamed from: L */
    public static final ErrorCorrectionLevel f9L = new ErrorCorrectionLevel(0, 1, "L");
    /* renamed from: M */
    public static final ErrorCorrectionLevel f10M = new ErrorCorrectionLevel(1, 0, "M");
    /* renamed from: Q */
    public static final ErrorCorrectionLevel f11Q = new ErrorCorrectionLevel(2, 3, "Q");
    private final int bits;
    private final String name;
    private final int ordinal;

    private ErrorCorrectionLevel(int ordinal, int bits, String name) {
        this.ordinal = ordinal;
        this.bits = bits;
        this.name = name;
    }

    public int ordinal() {
        return this.ordinal;
    }

    public int getBits() {
        return this.bits;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    public static ErrorCorrectionLevel forBits(int bits) {
        if (bits >= 0 && bits < FOR_BITS.length) {
            return FOR_BITS[bits];
        }
        throw new IllegalArgumentException();
    }
}
