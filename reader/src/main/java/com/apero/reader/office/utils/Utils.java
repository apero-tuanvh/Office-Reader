package com.apero.reader.office.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Utils {
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Gmail.
     */
    public static boolean isGoogleGmailUri(Uri uri) {
        return "com.google.android.gm.sapi".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Outlook email.
     */
    public static boolean isOutlookUri(Uri uri) {
        return "com.microsoft.office.outlook.fileprovider".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is WhatApp.
     */
    public static boolean isWhatAppUri(Uri uri) {
        return "com.whatsapp.provider.media".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Telegram.
     */
    public static boolean isTelegramUri(Uri uri) {
        return "org.telegram.messenger.provider".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getPathFromUri(final Context context, final Uri uri) {
        Log.e("getPathFromUri", uri.getPath());
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            //Documents/DocumentsScanner/anhanh.pdf
            //content://com.vsmart.android.externalstorage.documents/document/home%3ADocumentsScanner%2Fanhanh.pdf
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        } else if (isGoogleGmailUri(uri)) {
            return getPathUriGmail(context, uri);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getPathUriGmail(Context context, Uri uri) {

        InputStream is = null;
        FileOutputStream os = null;
        String fullPath = null;

        try {

            String scheme = uri.getScheme();
            String name = null;

            if (scheme.equals("content")) {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{
                        MediaStore.MediaColumns.DISPLAY_NAME
                }, null, null, null);
                cursor.moveToFirst();
                int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    name = cursor.getString(nameIndex);
                }
            } else {
                return null;
            }

            if (name == null) {
                return null;
            }

            int n = name.lastIndexOf(".");
            String fileName, fileExt;

            if (n == -1) {
                return null;
            }

            fullPath = context.getCacheDir() + "/" + name;

            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(fullPath);

            byte[] buffer = new byte[4096];
            int count;
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e1) {
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e1) {
                }
            }
            if (fullPath != null) {
                File f = new File(fullPath);
                f.delete();
            }
            e.printStackTrace();
        }


        return fullPath;
    }

    public static String getPathFromOutlook(Context context, Uri uri) {
        // uri2 = Uri.parse("content://com.microsoft.office.outlook.fileprovider/outlookfile/data/user/0/com.microsoft.office.outlook/cache/file-download/file--1723028522/Sachvui.Com-Phi-ly-tri-Dan-Ariely-scan.pdf");
        ///outlookfile/data/data/com.microsoft.office.outlook/cache/file-download/file-1754115030/Doc 19-02-2021 15_00 CH.pdf
        String path = uri.getPath();
        path = path.replace("/outlookfile/data", "storage/emulated/0");


        return path;
    }
}
