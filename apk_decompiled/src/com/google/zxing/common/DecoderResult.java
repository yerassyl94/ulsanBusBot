package com.google.zxing.common;

import java.util.Vector;

public final class DecoderResult {
    private final Vector byteSegments;
    private final String ecLevel;
    private final byte[] rawBytes;
    private final String text;

    public DecoderResult(byte[] rawBytes, String text, Vector byteSegments, String ecLevel) {
        if (rawBytes == null && text == null) {
            throw new IllegalArgumentException();
        }
        this.rawBytes = rawBytes;
        this.text = text;
        this.byteSegments = byteSegments;
        this.ecLevel = ecLevel;
    }

    public byte[] getRawBytes() {
        return this.rawBytes;
    }

    public String getText() {
        return this.text;
    }

    public Vector getByteSegments() {
        return this.byteSegments;
    }

    public String getECLevel() {
        return this.ecLevel;
    }
}
