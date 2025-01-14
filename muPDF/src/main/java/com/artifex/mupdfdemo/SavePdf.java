// 
// Decompiled by Procyon v0.5.36
// 

package com.artifex.mupdfdemo;

import java.io.ByteArrayOutputStream;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.DocumentException;
import com.lowagie.text.BadElementException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;

import android.util.Log;

import com.lowagie.text.Image;

import java.io.OutputStream;

import com.lowagie.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.pdf.PdfReader;

import android.graphics.Bitmap;

public class SavePdf {
    private float defaultScale;
    float widthScale;
    float heightScale;
    String inPath;
    String outPath;
    private int pageNum;
    private Bitmap bitmap;
    private float scale;
    private float density;
    private float width;
    private float height;
    private int marginTop;
    List<DataSaveFilePdf> list;

    public void setWidthScale(final float widthScale) {
        this.widthScale = widthScale;
    }

    public void setHeightScale(final float heightScale) {
        this.heightScale = heightScale;
    }

    public void setScale(final float scale) {
        this.scale = scale;
    }

    public void setWH(final float width, final float height) {
        this.width = width;
        this.height = height;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;

    }

    public void setDensity(final float density) {
        this.density = density;
    }

    public void setBitmap(final Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setPageNum(final int pageNum) {
        this.pageNum = pageNum;
    }

    public SavePdf() {
        this.defaultScale = 0.90756303f;
        list = new ArrayList<>();
    }

    public SavePdf(final String inPath, final String outPath) {
        list = new ArrayList<>();
        this.defaultScale = 0.90756303f;
        this.inPath = inPath;
        this.outPath = outPath;
    }

    public void setPath(final String inPath, final String outPath) {
        this.inPath = inPath;
        this.outPath = outPath;
    }

    public void addListData(DataSaveFilePdf dataSaveFilePdf) {
        list.add(dataSaveFilePdf);
    }

    public void addText() {
        try {
            final PdfReader reader = new PdfReader(this.inPath, "PDF".getBytes());

            final FileOutputStream outputStream = new FileOutputStream(this.outPath);
            final PdfStamper stamp = new PdfStamper(reader, (OutputStream) outputStream);
            for (DataSaveFilePdf dataSaveFilePdf : list) {
                bitmap = dataSaveFilePdf.getBitmap();
                pageNum = dataSaveFilePdf.getPageNum();
                width = dataSaveFilePdf.getWidth();
                height = dataSaveFilePdf.getHeight();

                final PdfContentByte over = stamp.getOverContent(this.pageNum);

                final byte[] bytes = this.Bitmap2Bytes(this.bitmap);
                final Image img = Image.getInstance(bytes);
                final Rectangle rectangle = reader.getPageSize(this.pageNum);

                img.setAlignment(1);
                Log.e("TAG", "position = " + rectangle.getWidth() * this.widthScale + "  " + rectangle.getHeight() * this.heightScale);
                Log.e("TAG", "density = " + this.density);
                Log.e("TAG", "scale = " + this.scale);
                Log.e("TAG", "widthScale = " + this.widthScale + "  heightScale = " + this.heightScale);
                Log.e("TAG", "bitmap.w = " + this.bitmap.getWidth() + "  bitmap.h = " + this.bitmap.getHeight());
                Log.e("TAG", "rectangle.getLeft = " + rectangle.getLeft() + "  rectangle.getBottom() = " + rectangle.getBottom());
                Log.e("TAG", "rectangle.getWidth = " + rectangle.getWidth() + "  rectangle.getHeight = " + rectangle.getHeight());
                Log.e("TAG", "scale w1 = " + rectangle.getWidth() / img.getWidth() * 100.0f);
                Log.e("TAG", "scale h2 = " + rectangle.getWidth() * this.widthScale * 100.0f);

                Log.e("TAG", "Difference = " + rectangle.getHeight() * (this.heightScale - this.widthScale));
                Log.e("TAG", "scaling ratio = " + this.scale / this.defaultScale);
                Log.e("TAG", "img height ratio = " + img.getHeight() / 2.0f * this.heightScale * 100.0f);

                float absoluteX = (this.width * (rectangle.getWidth() * this.widthScale) * this.scale);
//                float absoluteY = (this.height * (rectangle.getHeight() * this.widthScale) * this.scale);

                float absoluteY = rectangle.getHeight() - (this.height * (rectangle.getHeight() * this.heightScale) *
                        this.scale + img.getHeight() / 2.0F   )/(this.scale / this.defaultScale);

//                float absoluteY =rectangle.getHeight() - this.height * rectangle.getHeight() * this.heightScale *
//                                (this.scale / this.defaultScale) + img.getHeight() / 2.0F * this.widthScale * 100.0F;

                Log.e("TAG", "AbsolutePosition = " + absoluteX + " " + absoluteY);
                Log.e("TAG", "=================================================");

                img.scalePercent(rectangle.getWidth() * this.widthScale * 100.0f);

                img.setAbsolutePosition(absoluteX, absoluteY);
                over.addImage(img);
            }
            stamp.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        } catch (BadElementException e4) {
            e4.printStackTrace();
        } catch (DocumentException e5) {
            e5.printStackTrace();
        }
    }

    public byte[] Bitmap2Bytes(final Bitmap bm) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, (OutputStream) baos);
        return baos.toByteArray();
    }

    public static class DataSaveFilePdf {
        private float width;
        private float height;
        private int pageNum;
        private Bitmap bitmap;
        private float ratioY ;

        public DataSaveFilePdf(float width, float height, int pageNum, Bitmap bitmap) {
            this.width = width;
            this.height = height;
            this.pageNum = pageNum;
            this.bitmap = bitmap;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public float getRatioY() {
            return ratioY;
        }

        public void setRatioY(float ratioY) {
            this.ratioY = ratioY;
        }

        public int getPageNum() {
            return pageNum;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public void setHeight(float height) {
            this.height = height;
        }
    }
}
