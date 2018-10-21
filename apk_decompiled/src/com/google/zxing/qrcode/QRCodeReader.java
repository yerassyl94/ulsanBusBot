package com.google.zxing.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.DetectorResult;
import com.google.zxing.qrcode.decoder.Decoder;
import com.google.zxing.qrcode.detector.Detector;
import java.util.Hashtable;

public class QRCodeReader implements Reader {
    private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
    private final Decoder decoder = new Decoder();

    protected Decoder getDecoder() {
        return this.decoder;
    }

    public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
        return decode(image, null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, ChecksumException, FormatException {
        DecoderResult decoderResult;
        ResultPoint[] points;
        if (hints == null || !hints.containsKey(DecodeHintType.PURE_BARCODE)) {
            DetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect(hints);
            decoderResult = this.decoder.decode(detectorResult.getBits(), hints);
            points = detectorResult.getPoints();
        } else {
            decoderResult = this.decoder.decode(extractPureBits(image.getBlackMatrix()), hints);
            points = NO_POINTS;
        }
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
        if (decoderResult.getByteSegments() != null) {
            result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, decoderResult.getByteSegments());
        }
        if (decoderResult.getECLevel() != null) {
            result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, decoderResult.getECLevel().toString());
        }
        return result;
    }

    public void reset() {
    }

    private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {
        int[] leftTopBlack = image.getTopLeftOnBit();
        int[] rightBottomBlack = image.getBottomRightOnBit();
        if (leftTopBlack == null || rightBottomBlack == null) {
            throw NotFoundException.getNotFoundInstance();
        }
        int moduleSize = moduleSize(leftTopBlack, image);
        int top = leftTopBlack[1];
        int bottom = rightBottomBlack[1];
        int left = leftTopBlack[0];
        int matrixWidth = ((rightBottomBlack[0] - left) + 1) / moduleSize;
        int matrixHeight = ((bottom - top) + 1) / moduleSize;
        if (matrixWidth == 0 || matrixHeight == 0) {
            throw NotFoundException.getNotFoundInstance();
        } else if (matrixHeight != matrixWidth) {
            throw NotFoundException.getNotFoundInstance();
        } else {
            int nudge = moduleSize >> 1;
            top += nudge;
            left += nudge;
            BitMatrix bits = new BitMatrix(matrixWidth, matrixHeight);
            for (int y = 0; y < matrixHeight; y++) {
                int iOffset = top + (y * moduleSize);
                for (int x = 0; x < matrixWidth; x++) {
                    if (image.get((x * moduleSize) + left, iOffset)) {
                        bits.set(x, y);
                    }
                }
            }
            return bits;
        }
    }

    private static int moduleSize(int[] leftTopBlack, BitMatrix image) throws NotFoundException {
        int height = image.getHeight();
        int width = image.getWidth();
        int x = leftTopBlack[0];
        int y = leftTopBlack[1];
        while (x < width && y < height && image.get(x, y)) {
            x++;
            y++;
        }
        if (x == width || y == height) {
            throw NotFoundException.getNotFoundInstance();
        }
        int moduleSize = x - leftTopBlack[0];
        if (moduleSize != 0) {
            return moduleSize;
        }
        throw NotFoundException.getNotFoundInstance();
    }
}
