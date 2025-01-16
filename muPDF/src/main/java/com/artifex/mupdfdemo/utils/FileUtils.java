package com.artifex.mupdfdemo.utils;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;

public class FileUtils {
    public static boolean canOpenFilePdf(Context context, String path) {
        if (path == null) return false;
        try {
            File file = new File(path);
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            return true;
        } catch (Exception e) {
            Log.e("MuPDFCore", "canOpenFilePdf: ", e);
            return false;
        }
    }
}
