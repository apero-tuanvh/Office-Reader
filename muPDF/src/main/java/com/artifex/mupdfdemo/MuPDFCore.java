// 
// Decompiled by Procyon v0.5.36
// 

package com.artifex.mupdfdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;

import com.artifex.mupdfdemo.utils.FileUtils;
import com.example.mylibrary.R;

import java.util.ArrayList;

public class MuPDFCore {
    private static final String TAG;
    private static boolean gs_so_available;
    private int height, width;
    private int numPages;
    private float pageWidth;
    private float pageHeight;
    private long globals;
    private byte[] fileBuffer;
    private String file_format;
    private boolean isUnencryptedPDF;
    private final boolean wasOpenedFromBuffer;

    private boolean isSearch;

    private Context context;

    private String filePath;

    public void setSearch(boolean search) {
        isSearch = search;
    }


    private static native boolean gprfSupportedInternal();

    private native long openFile(final String p0);

    private native long openBuffer(final String p0);

    private native String fileFormatInternal();

    private native boolean isUnencryptedPDFInternal();

    private native int countPagesInternal();

    private native void gotoPageInternal(final int p0);

    private native float getPageWidth();

    private native float getPageHeight();

    private native void drawPage(final Bitmap p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final long p7);

    private native void updatePageInternal(final Bitmap p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final long p8);

    private native RectF[] searchPage(final String p0);

    private native TextChar[][][][] text();

    private native byte[] textAsHtml();

    private native void addMarkupAnnotationInternal(final PointF[] p0, final int p1);

    private native void addInkAnnotationInternal(final PointF[][] p0, final float p1, final float p2, final float p3, final float p4);

    private native void deleteAnnotationInternal(final int p0);

    private native int passClickEventInternal(final int p0, final float p1, final float p2);

    private native void setFocusedWidgetChoiceSelectedInternal(final String[] p0);

    private native String[] getFocusedWidgetChoiceSelected();

    private native String[] getFocusedWidgetChoiceOptions();

    private native int getFocusedWidgetSignatureState();

    private native String checkFocusedSignatureInternal();

    private native boolean signFocusedSignatureInternal(final String p0, final String p1);

    private native int setFocusedWidgetTextInternal(final String p0);

    private native String getFocusedWidgetTextInternal();

    private native int getFocusedWidgetTypeInternal();

    private native LinkInfo[] getPageLinksInternal(final int p0);

    private native RectF[] getWidgetAreasInternal(final int p0);

    private native Annotation[] getAnnotationsInternal(final int p0);

    private native OutlineItem[] getOutlineInternal();

    private native boolean hasOutlineInternal();

    private native boolean needsPasswordInternal();

    private native boolean authenticatePasswordInternal(final String p0);

    private native MuPDFAlertInternal waitForAlertInternal();

    private native void replyToAlertInternal(final MuPDFAlertInternal p0);

    private native void startAlertsInternal();

    private native void stopAlertsInternal();

    private native void destroying();

    private native boolean hasChangesInternal();

    private native void saveInternal();

    private native long createCookie();

    private native void destroyCookie(final long p0);

    private native void abortCookie(final long p0);

    private native String startProofInternal(final int p0);

    private native void endProofInternal(final String p0);

    private native int getNumSepsOnPageInternal(final int p0);

    private native int controlSepOnPageInternal(final int p0, final int p1, final boolean p2);

    private native Separation getSepInternal(final int p0, final int p1);

    public native boolean javascriptSupported();

    public MuPDFCore(final Context context, final String filename) throws Exception {
        this.numPages = -1;
        this.globals = this.openFile(filename);
        if (this.globals == 0L) {
            throw new Exception(String.format(context.getString(R.string.cannot_open_file_Path), filename));
        }
        this.file_format = this.fileFormatInternal();
        this.isUnencryptedPDF = this.isUnencryptedPDFInternal();
        this.wasOpenedFromBuffer = false;
        this.context = context;
        this.filePath = filename;
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
            width = displayMetrics.widthPixels;
        } catch (Exception e) {
            Log.e(TAG, "MuPDFCore: ", e);
            height = 1080;
            width = 1920;
        }
    }

    public MuPDFCore(final Context context, final String filePath, @NonNull final String password) throws Exception {
        this(context, filePath);
        this.authenticatePasswordInternal(password);
    }

    public MuPDFCore(final Context context, final byte[] buffer, final String magic) throws Exception {
        this.numPages = -1;
        this.fileBuffer = buffer;
        this.globals = this.openBuffer((magic != null) ? magic : "");
        if (this.globals == 0L) {
            throw new Exception(context.getString(R.string.cannot_open_buffer));
        }
        this.file_format = this.fileFormatInternal();
        this.isUnencryptedPDF = this.isUnencryptedPDFInternal();
        this.wasOpenedFromBuffer = true;
        this.context = context;
    }

    public int countPages() {
        if (this.numPages < 0) {
            this.numPages = this.countPagesSynchronized();
        }
        return this.numPages;
    }

    public String fileFormat() {
        return this.file_format;
    }

    public boolean isFileError() {
        if (this.file_format.contains("Error")) {
            return true;
        }
        if (this.file_format.contains("0.0")) {
            return true;
        }
        if (!this.needsPassword()){
            return !FileUtils.canOpenFilePdf(this.context, this.filePath);
        }
        return false;
    }

    public boolean isUnencryptedPDF() {
        return this.isUnencryptedPDF;
    }

    public boolean wasOpenedFromBuffer() {
        return this.wasOpenedFromBuffer;
    }

    private synchronized int countPagesSynchronized() {
        return this.countPagesInternal();
    }

    private void gotoPage(int page) {
        if (page > this.numPages - 1) {
            page = this.numPages - 1;
        } else if (page < 0) {
            page = 0;
        }
        this.gotoPageInternal(page);
        this.pageWidth = getPageWidth();
        this.pageHeight = getPageHeight();
    }

    public synchronized PointF getPageSize(final int page) {
        this.gotoPage(page);
        return new PointF(this.pageWidth, this.pageHeight);
    }

    public MuPDFAlert waitForAlert() {
        final MuPDFAlertInternal alert = this.waitForAlertInternal();
        return (alert != null) ? alert.toAlert() : null;
    }

    public void replyToAlert(final MuPDFAlert alert) {
        this.replyToAlertInternal(new MuPDFAlertInternal(alert));
    }

    public void stopAlerts() {
        this.stopAlertsInternal();
    }

    public void startAlerts() {
        this.startAlertsInternal();
    }

    public synchronized void onDestroy() {
        this.destroying();
        this.globals = 0L;
    }

    public synchronized void drawPage(final Bitmap bm, final int page, final int pageW, final int pageH, final int patchX,
                                      final int patchY, final int patchW, final int patchH, final Cookie cookie) {
        this.gotoPage(page);
        this.drawPage(bm, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
    }

    public synchronized void updatePage(final Bitmap bm, final int page, final int pageW, final int pageH, final int patchX, final int patchY, final int patchW, final int patchH, final Cookie cookie) {
        this.updatePageInternal(bm, page, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
    }

    public synchronized PassClickResult passClickEvent(final int page, final float x, final float y) {
        final boolean changed = this.passClickEventInternal(page, x, y) != 0;
        switch (WidgetType.values()[this.getFocusedWidgetTypeInternal()]) {
            case TEXT: {
                return new PassClickResultText(changed, this.getFocusedWidgetTextInternal());
            }
            case LISTBOX:
            case COMBOBOX: {
                return new PassClickResultChoice(changed, this.getFocusedWidgetChoiceOptions(), this.getFocusedWidgetChoiceSelected());
            }
            case SIGNATURE: {
                return new PassClickResultSignature(changed, this.getFocusedWidgetSignatureState());
            }
            default: {
                return new PassClickResult(changed);
            }
        }
    }

    public synchronized boolean setFocusedWidgetText(final int page, final String text) {
        this.gotoPage(page);
        final boolean success = this.setFocusedWidgetTextInternal(text) != 0;
        return success;
    }

    public synchronized void setFocusedWidgetChoiceSelected(final String[] selected) {
        this.setFocusedWidgetChoiceSelectedInternal(selected);
    }

    public synchronized String checkFocusedSignature() {
        return this.checkFocusedSignatureInternal();
    }

    public synchronized boolean signFocusedSignature(final String keyFile, final String password) {
        return this.signFocusedSignatureInternal(keyFile, password);
    }

    public synchronized LinkInfo[] getPageLinks(final int page) {
        return this.getPageLinksInternal(page);
    }

    public synchronized RectF[] getWidgetAreas(final int page) {
        return this.getWidgetAreasInternal(page);
    }

    public synchronized Annotation[] getAnnoations(final int page) {
        return this.getAnnotationsInternal(page);
    }

    public synchronized RectF[] searchPage(final int page, final String text) {
        this.gotoPage(page);
        return this.searchPage(text);
    }

    public synchronized byte[] html(final int page) {
        this.gotoPage(page);
        return this.textAsHtml();
    }

    public synchronized TextWord[][] textLines(final int page) {
        this.gotoPage(page);
        final TextChar[][][][] chars = this.text();
        final ArrayList<TextWord[]> lns = new ArrayList<TextWord[]>();
        for (final TextChar[][][] bl : chars) {
            if (bl != null) {
                for (final TextChar[][] ln : bl) {
                    final ArrayList<TextWord> wds = new ArrayList<TextWord>();
                    TextWord wd = new TextWord();
                    for (final TextChar[] array4 : ln) {
                        final TextChar[] sp = array4;
                        for (final TextChar tc : array4) {
                            if (tc.c != ' ') {
                                wd.Add(tc);
                            } else if (wd.w.length() > 0) {
                                wds.add(wd);
                                wd = new TextWord();
                            }
                        }
                    }
                    if (wd.w.length() > 0) {
                        wds.add(wd);
                    }
                    if (wds.size() > 0) {
                        lns.add(wds.toArray(new TextWord[wds.size()]));
                    }
                }
            }
        }
        return lns.toArray(new TextWord[lns.size()][]);
    }

    public synchronized void addMarkupAnnotation(final int page, final PointF[] quadPoints, final Annotation.Type type) {
        this.gotoPage(page);
        this.addMarkupAnnotationInternal(quadPoints, type.ordinal());
    }

    public synchronized void addInkAnnotation(final int page, final PointF[][] arcs, final float[] color, final float inkThickness) {
        this.gotoPage(page);
        Log.e("zyw", "color = " + color[0] + " " + color[1] + " " + color[2]);
        this.addInkAnnotationInternal(arcs, color[0], color[1], color[2], inkThickness);
    }

    public synchronized void deleteAnnotation(final int page, final int annot_index) {
        this.gotoPage(page);
        this.deleteAnnotationInternal(annot_index);
    }

    public synchronized boolean hasOutline() {
        return this.hasOutlineInternal();
    }

    public synchronized OutlineItem[] getOutline() {
        return this.getOutlineInternal();
    }

    public synchronized boolean needsPassword() {
        return this.needsPasswordInternal();
    }

    public synchronized boolean authenticatePassword(final String password) {
        return this.authenticatePasswordInternal(password);
    }

    public synchronized boolean hasChanges() {
        return this.hasChangesInternal();
    }

    public synchronized void save() {
        this.saveInternal();
    }

    public synchronized String startProof(final int resolution) {
        return this.startProofInternal(resolution);
    }

    public synchronized void endProof(final String filename) {
        this.endProofInternal(filename);
    }

    public static boolean gprfSupported() {
        return MuPDFCore.gs_so_available && gprfSupportedInternal();
    }

    public boolean canProof() {
        final String format = this.fileFormat();
        return format.contains("PDF");
    }

    public synchronized int getNumSepsOnPage(final int page) {
        return this.getNumSepsOnPageInternal(page);
    }

    public synchronized int controlSepOnPage(final int page, final int sep, final boolean disable) {
        return this.controlSepOnPageInternal(page, sep, disable);
    }

    public synchronized Separation getSep(final int page, final int sep) {
        return this.getSepInternal(page, sep);
    }

    static {
        TAG = MuPDFCore.class.getSimpleName();
        MuPDFCore.gs_so_available = false;
        Log.e(MuPDFCore.TAG, "Loading dll");
        System.loadLibrary("mupdf");
        Log.e(MuPDFCore.TAG, "Loaded dll");
    }

    public class Cookie {
        private final long cookiePtr;

        public Cookie() {
            this.cookiePtr = MuPDFCore.this.createCookie();
            if (this.cookiePtr == 0L) {
                throw new OutOfMemoryError();
            }
        }

        public void abort() {
            MuPDFCore.this.abortCookie(this.cookiePtr);
        }

        public void destroy() {
            MuPDFCore.this.destroyCookie(this.cookiePtr);
        }
    }
}
