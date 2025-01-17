// 
// Decompiled by Procyon v0.5.36
// 

package com.artifex.mupdfdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.mylibrary.R;
import com.lonelypluto.pdflibrary.utils.SharedPreferencesUtil;
import com.lonelypluto.pdflibrary.utils.StickerImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PageView extends ViewGroup {
    private static final float ITEM_SELECT_BOX_WIDTH = 4.0f;
    private static final int HIGHLIGHT_COLOR = -2130749662;
    private int LINK_COLOR;
    private static final int BOX_COLOR = -9868951;
    private int INK_COLOR;
    private float INK_THICKNESS;
    private float current_scale;
    private static final int BACKGROUND_COLOR = -1;
    private static final int PROGRESS_DIALOG_DELAY = 200;
    protected final Context mContext;
    protected int mPageNumber;
    private Point mParentSize;
    protected Point mSize;
    protected float mSourceScale;
    private ImageView mEntire;
    private Bitmap mEntireBm;
    private Matrix mEntireMat;
    private AsyncTask<Void, Void, TextWord[][]> mGetText;
    private AsyncTask<Void, Void, LinkInfo[]> mGetLinkInfo;
    private CancellableAsyncTask<Void, Void> mDrawEntire;
    private Point mPatchViewSize;
    private Rect mPatchArea;
    private ImageView mPatch;
    private Bitmap mPatchBm;
    private CancellableAsyncTask<Void, Void> mDrawPatch;
    private RectF[] mSearchBoxes;
    protected LinkInfo[] mLinks;
    private RectF mSelectBox;
    private TextWord[][] mText;
    private RectF mItemSelectBox;
    protected ArrayList<ArrayList<PointF>> mDrawing;
    private View mSearchView;
    private FrameLayout mImageView;
    private boolean mIsBlank;
    private boolean mHighlightLinks;
    private ProgressBar mBusyIndicator;
    private final Handler mHandler;

    private boolean isNightMode;
    StickerImageView stickerImageView;
    List<SavePdf.DataSaveFilePdf> bmImage;

    public void setNightMode(boolean nightMode) {
        isNightMode = nightMode;
    }


    public PageView(final Context c, final Point parentSize, final Bitmap sharedHqBm) {
        super(c);
        this.LINK_COLOR = -2130749662;
        this.INK_COLOR = -16777216;
        this.INK_THICKNESS = 10.0f;
        this.mHandler = new Handler();
        this.mContext = c;
        this.mParentSize = parentSize;
        this.setBackgroundColor(-1);
        this.mEntireBm = Bitmap.createBitmap(parentSize.x, parentSize.y, Bitmap.Config.ARGB_8888);
        this.mPatchBm = sharedHqBm;
        this.mEntireMat = new Matrix();
    }

    protected abstract CancellableTaskDefinition<Void, Void> getDrawPageTask(final Bitmap p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);

    protected abstract CancellableTaskDefinition<Void, Void> getUpdatePageTask(final Bitmap p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);

    protected abstract LinkInfo[] getLinkInfo();

    protected abstract TextWord[][] getText();

    protected abstract void addMarkup(final PointF[] p0, final Annotation.Type p1);

    private void reinit() {
        if (this.mDrawEntire != null) {
            this.mDrawEntire.cancelAndWait();
            this.mDrawEntire = null;
        }
        if (this.mDrawPatch != null) {
            this.mDrawPatch.cancelAndWait();
            this.mDrawPatch = null;
        }
        if (this.mGetLinkInfo != null) {
            this.mGetLinkInfo.cancel(true);
            this.mGetLinkInfo = null;
        }
        if (this.mGetText != null) {
            this.mGetText.cancel(true);
            this.mGetText = null;
        }
        this.mIsBlank = true;
        this.mPageNumber = 0;
        if (this.mSize == null) {
            this.mSize = this.mParentSize;
        }
        if (this.mEntire != null) {
            this.mEntire.setImageBitmap((Bitmap) null);
            this.mEntire.invalidate();
        }
        if (this.mPatch != null) {
            this.mPatch.setImageBitmap((Bitmap) null);
            this.mPatch.invalidate();
        }
        this.mPatchViewSize = null;
        this.mPatchArea = null;
        this.mSearchBoxes = null;
        this.mLinks = null;
        this.mSelectBox = null;
        this.mText = null;
        this.mItemSelectBox = null;
    }

    public void releaseResources() {
        this.reinit();
        if (this.mBusyIndicator != null) {
            this.removeView((View) this.mBusyIndicator);
            this.mBusyIndicator = null;
        }
    }

    public void releaseBitmaps() {
        this.reinit();
        if (this.mEntireBm != null) {
            this.mEntireBm.recycle();
        }
        this.mEntireBm = null;
        if (this.mPatchBm != null) {
            this.mPatchBm.recycle();
        }
        this.mPatchBm = null;
    }

    public void blank(final int page) {
        this.reinit();
        this.mPageNumber = page;
        if (this.mBusyIndicator == null) {
            (this.mBusyIndicator = new ProgressBar(this.mContext)).setIndeterminate(true);
            this.addView((View) this.mBusyIndicator);
        }
        this.setBackgroundColor(-1);
    }

    private Bitmap invert(Bitmap src) {
        int height = src.getHeight();
        int width = src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0, paint);
        // src.recycle();
        return bitmap;
    }


    public void setPage(final int page, final PointF size) {
        Log.e("PageView", "setPage: " + page);
        if (this.mDrawEntire != null) {
            this.mDrawEntire.cancelAndWait();
            this.mDrawEntire = null;
        }
        this.mIsBlank = false;
        if (this.mSearchView != null) {
            this.mSearchView.invalidate();
        }
        if (this.mImageView != null) {
            this.mImageView.invalidate();
        }

        this.mPageNumber = page;
        if (this.mEntire == null) {
            (this.mEntire = (ImageView) new OpaqueImageView(this.mContext)).setScaleType(ImageView.ScaleType.MATRIX);
            this.addView((View) this.mEntire);
        }
        this.mSourceScale = Math.min(this.mParentSize.x / size.x, this.mParentSize.y / size.y);
        this.mSize = new Point((int) (size.x * this.mSourceScale), (int) (size.y * this.mSourceScale));
        this.mEntire.setImageBitmap((Bitmap) null);
        this.mEntire.invalidate();
        (this.mGetLinkInfo = new AsyncTask<Void, Void, LinkInfo[]>() {
            protected LinkInfo[] doInBackground(final Void... v) {
                return PageView.this.getLinkInfo();
            }

            protected void onPostExecute(final LinkInfo[] v) {
                PageView.this.mLinks = v;
                if (PageView.this.mSearchView != null) {
                    PageView.this.mSearchView.invalidate();
                }
            }
        }).execute();
        (this.mDrawEntire = new CancellableAsyncTask<Void, Void>(this.getDrawPageTask(this.mEntireBm, this.mSize.x, this.mSize.y, 0, 0, this.mSize.x, this.mSize.y)) {
            @Override
            public void onPreExecute() {
                PageView.this.setBackgroundColor(-1);
                PageView.this.mEntire.setImageBitmap(null);
                PageView.this.mEntire.invalidate();
                if (PageView.this.mBusyIndicator == null) {
                    PageView.this.mBusyIndicator = new ProgressBar(PageView.this.mContext);
                    PageView.this.mBusyIndicator.setIndeterminate(true);
                    PageView.this.addView((View) PageView.this.mBusyIndicator);
                    PageView.this.mBusyIndicator.setVisibility(View.INVISIBLE);
                    PageView.this.mHandler.postDelayed(() -> {
                        if (PageView.this.mBusyIndicator != null) {
                            PageView.this.mBusyIndicator.setVisibility(View.VISIBLE);
                        }
                    }, 200L);
                }
            }

            @Override
            public void onPostExecute(final Void result) {
                PageView.this.removeView((View) PageView.this.mBusyIndicator);
                PageView.this.mBusyIndicator = null;
                PageView.this.mEntire.setImageBitmap(isNightMode ? invert(PageView.this.mEntireBm) : PageView.this.mEntireBm);
                PageView.this.mEntire.invalidate();
                PageView.this.setBackgroundColor(Color.BLACK);
            }
        }).execute(new Void[0]);

        if (this.mSearchView == null) {
            this.addView(this.mSearchView = new View(this.mContext) {
                protected void onDraw(final Canvas canvas) {
                    super.onDraw(canvas);


                    final float scale = PageView.this.mSourceScale * this.getHeight() / PageView.this.mSize.y;
                    PageView.this.current_scale = scale;
                    final Paint paint = new Paint();
                    if (bmImage != null && bmImage.size() > 0) {
                        for (SavePdf.DataSaveFilePdf dataSaveFilePdf : bmImage) {
                            if (dataSaveFilePdf.getPageNum() - 1 == mPageNumber)
                                canvas.drawBitmap(dataSaveFilePdf.getBitmap(), (dataSaveFilePdf.getWidth()), (dataSaveFilePdf.getHeight()), paint);
                        }
                    }
                    if (!PageView.this.mIsBlank && PageView.this.mSearchBoxes != null) {
                        paint.setColor(SharedPreferencesUtil.getSearchTextColor());
                        for (final RectF rect : PageView.this.mSearchBoxes) {
                            canvas.drawRect(rect.left * scale, rect.top * scale, rect.right * scale, rect.bottom * scale, paint);
                        }
                    }
                    if (!PageView.this.mIsBlank && PageView.this.mLinks != null && PageView.this.mHighlightLinks) {
                        paint.setColor(PageView.this.LINK_COLOR);
                        for (final LinkInfo link : PageView.this.mLinks) {
                            canvas.drawRect(link.rect.left * scale, link.rect.top * scale, link.rect.right * scale, link.rect.bottom * scale, paint);
                        }
                    }
                    if (PageView.this.mSelectBox != null && PageView.this.mText != null) {
                        paint.setColor(-2130749662);
                        PageView.this.processSelectedText(new TextProcessor() {
                            RectF rect;

                            @Override
                            public void onStartLine() {
                                this.rect = new RectF();
                            }

                            @Override
                            public void onWord(final TextWord word) {
                                this.rect.union((RectF) word);
                            }

                            @Override
                            public void onEndLine() {
                                if (!this.rect.isEmpty()) {
                                    canvas.drawRect(this.rect.left * scale, this.rect.top * scale,
                                            this.rect.right * scale, this.rect.bottom * scale, paint);
                                }
                            }
                        });
                    }
                    if (PageView.this.mItemSelectBox != null) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(4.0f);
                        paint.setColor(-9868951);
                        canvas.drawRect(PageView.this.mItemSelectBox.left * scale, PageView.this.mItemSelectBox.top * scale,
                                PageView.this.mItemSelectBox.right * scale, PageView.this.mItemSelectBox.bottom * scale, paint);
                    }
                    if (PageView.this.mDrawing != null) {
                        final Path path = new Path();
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setStrokeWidth(PageView.this.INK_THICKNESS * scale);
                        paint.setColor(PageView.this.INK_COLOR);
                        for (final ArrayList<PointF> arc : PageView.this.mDrawing) {
                            if (arc.size() >= 2) {
                                final Iterator<PointF> iit = arc.iterator();
                                PointF p = iit.next();
                                float mX = p.x * scale;
                                float mY = p.y * scale;
                                path.moveTo(mX, mY);
                                while (iit.hasNext()) {
                                    p = iit.next();
                                    final float x = p.x * scale;
                                    final float y = p.y * scale;
                                    path.quadTo(mX, mY, (x + mX) / 2.0f, (y + mY) / 2.0f);
                                    mX = x;
                                    mY = y;
                                }
                                path.lineTo(mX, mY);
                            } else {
                                final PointF p = arc.get(0);
                                canvas.drawCircle(p.x * scale, p.y * scale, PageView.this.INK_THICKNESS * scale / 2.0f, paint);
                            }
                        }
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path, paint);
                    }
                }
            });
        }

        this.requestLayout();
    }

    public void setSearchBoxes(final RectF[] searchBoxes) {
        this.mSearchBoxes = searchBoxes;
        if (this.mSearchView != null) {
            this.mSearchView.invalidate();
        }
    }

    public void setLinkHighlighting(final boolean f) {
        this.mHighlightLinks = f;
        if (this.mSearchView != null) {
            this.mSearchView.invalidate();
        }
    }

    public void setLinkHighlightColor(final int color) {
        this.LINK_COLOR = color;
        if (this.mHighlightLinks && this.mSearchView != null) {
            this.mSearchView.invalidate();
        }
    }

    public void deselectText() {
        this.mSelectBox = null;
        this.mSearchView.invalidate();
    }

    public void selectText(final float x0, final float y0, final float x1, final float y1) {
        final float scale = this.mSourceScale * this.getWidth() / this.mSize.x;
        final float docRelX0 = (x0 - this.getLeft()) / scale;
        final float docRelY0 = (y0 - this.getTop()) / scale;
        final float docRelX2 = (x1 - this.getLeft()) / scale;
        final float docRelY2 = (y1 - this.getTop()) / scale;
        if (docRelY0 <= docRelY2) {
            this.mSelectBox = new RectF(docRelX0, docRelY0, docRelX2, docRelY2);
        } else {
            this.mSelectBox = new RectF(docRelX2, docRelY2, docRelX0, docRelY0);
        }
        this.mSearchView.invalidate();
        if (this.mGetText == null) {
            (this.mGetText = new AsyncTask<Void, Void, TextWord[][]>() {
                protected TextWord[][] doInBackground(final Void... params) {
                    return PageView.this.getText();
                }

                protected void onPostExecute(final TextWord[][] result) {
                    PageView.this.mText = result;
                    PageView.this.mSearchView.invalidate();
                }
            }).execute();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void addImage(SavePdf.DataSaveFilePdf dataSaveFilePdf) {
        if (bmImage == null)
            bmImage = new ArrayList<>();
        int[] location = new int[2];
        mSearchView.getLocationOnScreen(location);
        int xx = location[0];
        int yy = location[1];

        imageX = dataSaveFilePdf.getWidth() - xx;
        imageY = dataSaveFilePdf.getHeight() - yy;
        dataSaveFilePdf.setWidth(imageX);
        dataSaveFilePdf.setHeight(imageY);
        this.bmImage.add(dataSaveFilePdf);
        Log.e("TAG", "addImage  page:"+mPageNumber+"  location: " + xx + "/" + yy);
        Log.e("TAG", "addImage local image : " + dataSaveFilePdf.getWidth() + "/" + dataSaveFilePdf.getHeight());
        Log.e("TAG", "addImage size image: " + dataSaveFilePdf.getBitmap().getWidth() + "/" + dataSaveFilePdf.getBitmap().getHeight());
        Log.e("TAG", "addImage size page: " + mSearchView.getWidth() + "/" + mSearchView.getHeight());
        this.mSearchView.invalidate();
        requestLayout();
    }

    public void startDraw(final float x, final float y) {
        final float scale = this.mSourceScale * this.getWidth() / this.mSize.x;
        final float docRelX = (x - this.getLeft()) / scale;
        final float docRelY = (y - this.getTop()) / scale;
        if (this.mDrawing == null) {
            this.mDrawing = new ArrayList<ArrayList<PointF>>();
        }
        final ArrayList<PointF> arc = new ArrayList<PointF>();
        arc.add(new PointF(docRelX, docRelY));
        this.mDrawing.add(arc);
        this.mSearchView.invalidate();
    }

    public void continueDraw(final float x, final float y) {
        final float scale = this.mSourceScale * this.getWidth() / this.mSize.x;
        final float docRelX = (x - this.getLeft()) / scale;
        final float docRelY = (y - this.getTop()) / scale;
        if (this.mDrawing != null && this.mDrawing.size() > 0) {
            final ArrayList<PointF> arc = this.mDrawing.get(this.mDrawing.size() - 1);
            arc.add(new PointF(docRelX, docRelY));
            this.mSearchView.invalidate();
        }
    }

    float imageX = 100;
    float imageY = 100;

    public void cancelDraw() {
        this.mDrawing = null;
        this.mSearchView.invalidate();
    }

    protected PointF[][] getDraw() {
        if (this.mDrawing == null) {
            return null;
        }
        final PointF[][] path = new PointF[this.mDrawing.size()][];
        for (int i = 0; i < this.mDrawing.size(); ++i) {
            final ArrayList<PointF> arc = this.mDrawing.get(i);
            path[i] = arc.toArray(new PointF[arc.size()]);
        }
        return path;
    }

    public void setInkColor(final int color) {
        this.INK_COLOR = color;
    }

    public void setPaintStrockWidth(final float inkThickness) {
        this.INK_THICKNESS = inkThickness;
    }

    protected float getInkThickness() {
        if (this.current_scale == 0.0f) {
            return 4.537815f;
        }
        return this.INK_THICKNESS / 2.0f;
    }

    public float getCurrentScale() {
        if (this.current_scale == 0.0f) {
            return 9.07563f;
        }
        return this.current_scale;
    }

    protected float[] getColor() {
        return this.changeColor(this.INK_COLOR);
    }

    private float[] changeColor(final int color) {
        final int red = (color & 0xFF0000) >> 16;
        final int green = (color & 0xFF00) >> 8;
        final int blue = color & 0xFF;
        final float[] colors = {red / 255.0f, green / 255.0f, blue / 255.0f};
        return colors;
    }

    protected void processSelectedText(final TextProcessor tp) {
        new TextSelector(this.mText, this.mSelectBox).select(tp);
    }

    public void setItemSelectBox(final RectF rect) {
        this.mItemSelectBox = rect;
        if (this.mSearchView != null) {
            this.mSearchView.invalidate();
        }
    }

    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int x = 0;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case 0: {
                x = this.mSize.x;
                break;
            }
            default: {
                x = MeasureSpec.getSize(widthMeasureSpec);
                break;
            }
        }
        int y = 0;
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case 0: {
                y = this.mSize.y;
                break;
            }
            default: {
                y = MeasureSpec.getSize(heightMeasureSpec);
                break;
            }
        }
        this.setMeasuredDimension(x, y);
        if (this.mBusyIndicator != null) {
            final int limit = Math.min(this.mParentSize.x, this.mParentSize.y) / 2;
            this.mBusyIndicator.measure(Integer.MIN_VALUE | limit, Integer.MIN_VALUE | limit);
        }
    }

    protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {
        final int w = right - left;
        final int h = bottom - top;
        if (this.mEntire != null) {
            if (this.mEntire.getWidth() != w || this.mEntire.getHeight() != h) {
                this.mEntireMat.setScale(w / (float) this.mSize.x, h / (float) this.mSize.y);
//                this.mEntireMat.setScale(1, 1);
                this.mEntire.setImageMatrix(this.mEntireMat);
                this.mEntire.invalidate();
            }
            this.mEntire.layout(0, 0, w, h);
        }
        if (this.mSearchView != null) {
            this.mSearchView.layout(0, 0, w, h);
        }
        if (this.mImageView != null) {
            this.mImageView.layout(0, 0, w, h);
        }
        if (this.mPatchViewSize != null) {
            if (this.mPatchViewSize.x != w || this.mPatchViewSize.y != h) {
                this.mPatchViewSize = null;
                this.mPatchArea = null;
                if (this.mPatch != null) {
                    this.mPatch.setImageBitmap((Bitmap) null);
                    this.mPatch.invalidate();
                }
            } else {
                this.mPatch.layout(this.mPatchArea.left, this.mPatchArea.top, this.mPatchArea.right, this.mPatchArea.bottom);
            }
        }
        if (this.mBusyIndicator != null) {
            final int bw = this.mBusyIndicator.getMeasuredWidth();
            final int bh = this.mBusyIndicator.getMeasuredHeight();
            this.mBusyIndicator.layout((w - bw) / 2, (h - bh) / 2, (w + bw) / 2, (h + bh) / 2);
        }
    }

    public void updateHq(final boolean update) {
        final Rect viewArea = new Rect(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
        if (viewArea.width() == this.mSize.x || viewArea.height() == this.mSize.y) {
            if (this.mPatch != null) {
                this.mPatch.setImageBitmap((Bitmap) null);
                this.mPatch.invalidate();
            }
        } else {
            final Point patchViewSize = new Point(viewArea.width(), viewArea.height());
            final Rect patchArea = new Rect(0, 0, this.mParentSize.x, this.mParentSize.y);
            if (!patchArea.intersect(viewArea)) {
                return;
            }
            patchArea.offset(-viewArea.left, -viewArea.top);
            final boolean area_unchanged = patchArea.equals((Object) this.mPatchArea) && patchViewSize.equals((Object) this.mPatchViewSize);
            if (area_unchanged && !update) {
                return;
            }
            final boolean completeRedraw = !area_unchanged;
            if (this.mDrawPatch != null) {
                this.mDrawPatch.cancelAndWait();
                this.mDrawPatch = null;
            }
            if (this.mPatch == null) {
                (this.mPatch = (ImageView) new OpaqueImageView(this.mContext)).setScaleType(ImageView.ScaleType.MATRIX);
                this.addView((View) this.mPatch);
                if (this.mSearchView != null)
                    this.mSearchView.bringToFront();
            }
            CancellableTaskDefinition<Void, Void> task;
            if (completeRedraw) {
                task = this.getDrawPageTask(this.mPatchBm, patchViewSize.x, patchViewSize.y, patchArea.left, patchArea.top, patchArea.width(), patchArea.height());
            } else {
                task = this.getUpdatePageTask(this.mPatchBm, patchViewSize.x, patchViewSize.y, patchArea.left, patchArea.top, patchArea.width(), patchArea.height());
            }
            (this.mDrawPatch = new CancellableAsyncTask<Void, Void>(task) {
                @Override
                public void onPostExecute(final Void result) {
                    PageView.this.mPatchViewSize = patchViewSize;
                    PageView.this.mPatchArea = patchArea;
                    PageView.this.mPatch.setImageBitmap(isNightMode ? invert(PageView.this.mPatchBm) : PageView.this.mPatchBm);
                    PageView.this.mPatch.invalidate();
                    PageView.this.mPatch.layout(PageView.this.mPatchArea.left, PageView.this.mPatchArea.top, PageView.this.mPatchArea.right, PageView.this.mPatchArea.bottom);
                }
            }).execute(new Void[0]);
        }
    }

    public void update() {
        if (this.mDrawEntire != null) {
            this.mDrawEntire.cancelAndWait();
            this.mDrawEntire = null;
        }
        if (this.mDrawPatch != null) {
            this.mDrawPatch.cancelAndWait();
            this.mDrawPatch = null;
        }
        (this.mDrawEntire = new CancellableAsyncTask<Void, Void>(
                this.getUpdatePageTask(this.mEntireBm, this.mSize.x, this.mSize.y, 0, 0, this.mSize.x, this.mSize.y)) {
            @Override
            public void onPostExecute(final Void result) {
                PageView.this.mEntire.setImageBitmap(isNightMode ? invert(PageView.this.mEntireBm) : PageView.this.mEntireBm);
                PageView.this.mEntire.invalidate();
            }
        }).execute(new Void[0]);
        this.updateHq(true);
    }

    public void removeHq() {
        if (this.mDrawPatch != null) {
            this.mDrawPatch.cancelAndWait();
            this.mDrawPatch = null;
        }
        this.mPatchViewSize = null;
        this.mPatchArea = null;
        if (this.mPatch != null) {
            this.mPatch.setImageBitmap(null);
            this.mPatch.invalidate();
        }
    }

    public int getPage() {
        return this.mPageNumber;
    }

    public boolean isOpaque() {
        return true;
    }
}
