package com.google.zxing.aztec;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.aztec.decoder.Decoder;
import com.google.zxing.aztec.detector.Detector;
import com.google.zxing.common.DecoderResult;
import java.util.Hashtable;

public final class AztecReader implements Reader {
    public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
        return decode(image, null);
    }

    public Result decode(BinaryBitmap image, Hashtable hints) throws NotFoundException, FormatException {
        AztecDetectorResult detectorResult = new Detector(image.getBlackMatrix()).detect();
        ResultPoint[] points = detectorResult.getPoints();
        if (!(hints == null || detectorResult.getPoints() == null)) {
            ResultPointCallback rpcb = (ResultPointCallback) hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK);
            if (rpcb != null) {
                for (ResultPoint foundPossibleResultPoint : detectorResult.getPoints()) {
                    rpcb.foundPossibleResultPoint(foundPossibleResultPoint);
                }
            }
        }
        DecoderResult decoderResult = new Decoder().decode(detectorResult);
        Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.AZTEC);
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
}
