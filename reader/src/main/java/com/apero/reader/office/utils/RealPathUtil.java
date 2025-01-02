package com.apero.reader.office.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RealPathUtil {

    public static String getRealPath(Context context, Uri fileUri) {
        String realPath;
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = RealPathUtil.getRealPathFromURIBelowAPI11(context, fileUri);
        }
        // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19) {
            realPath = RealPathUtil.getRealPathFromURIAPI11to18(context, fileUri);
        }
        // SDK > 19 (Android 4.4) and up
        else {
            realPath = RealPathUtil.getRealPathFromURIAPI19(context, fileUri);
        }
        return realPath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURIAPI11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }

    public static String getRealPathFromURIBelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = 0;
        String result = "";
        if (cursor != null) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getRealPathFromURIAPI19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            } else if (Utils.isGoogleGmailUri(uri)) {
                return Utils.getPathUriGmail(context, uri);
            }else if (Utils.isOutlookUri(uri)) {
                return Utils.getPathFromOutlook(context, uri);
            }
            if (Utils.isWhatAppUri(uri)) {
                return Utils.getPathUriGmail(context, uri);
            }
            // telegram
            else if (Utils.isTelegramUri(uri)) {

                final String nameFile = getNameFile(context, uri, null, null);
                String path = Environment.getExternalStorageDirectory() + "/Telegram/Telegram Documents/" + nameFile;
                File file = new File(path);
                if (file.exists())
                    return path;
                return null;
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String fileName = getNameFile(context, uri,null,null);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }
                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/all_downloads"), Long.valueOf(id));
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isVsmart(uri)) {
                final String nameFile = getNameFile(context, uri, null, null);
                Log.e("nameFile",nameFile);
                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/all_downloads"), Long.valueOf(id));
//                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getPathVsmart(context, uri, null, null);
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
            } else if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri, context);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {
                MediaStore.Images.Media.DATA
        };

        try {
            cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
            String name[] = cursor.getColumnNames();

            if (cursor != null && cursor.moveToFirst()) {
//                final int index = cursor.getColumnIndexOrThrow(column);
                final int document_id = cursor.getColumnIndexOrThrow("document_id");
                final int last_modified = cursor.getColumnIndexOrThrow("last_modified");
                final int mime_type = cursor.getColumnIndexOrThrow("mime_type");
                final int flags = cursor.getColumnIndexOrThrow("flags");
                Log.e("ColumsData",cursor.getString(document_id));
                Log.e("ColumsData",cursor.getString(last_modified));
                Log.e("ColumsData",cursor.getString(mime_type));
                Log.e("ColumsData",cursor.getString(flags));
//                return cursor.getString(index);
            }
        } catch (Exception e) {
            Log.e("getDataColumn", "error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getPathVsmart(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {
                MediaStore.Images.Media.DATA
        };

        try {
            cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
            String name[] = cursor.getColumnNames();

            if (cursor != null && cursor.moveToFirst()) {
//                final int index = cursor.getColumnIndexOrThrow(column);
                final int document_id = cursor.getColumnIndexOrThrow("document_id");
                final int last_modified = cursor.getColumnIndexOrThrow("last_modified");
                final int mime_type = cursor.getColumnIndexOrThrow("mime_type");
                final int flags = cursor.getColumnIndexOrThrow("flags");
                Log.e("ColumsData",cursor.getString(document_id));
                Log.e("ColumsData",cursor.getString(last_modified));
                Log.e("ColumsData",cursor.getString(mime_type));
                Log.e("ColumsData",cursor.getString(flags));
                String doc = cursor.getString(document_id);
                if (doc.contains("home")){
                    return Environment.getExternalStorageDirectory().toString() + "/Documents/" + doc.replace("home:","");
                }else if (doc.contains("downloads")){
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + doc.replace("downloads:","");
                }else {
                    return Environment.getExternalStorageDirectory().toString()  + doc.replace("primary:","/");
                }
//                return cursor.getString(index);
            }

        } catch (Exception e) {
            Log.e("getDataColumn", "error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getNameFile(Context context, Uri uri, String selection,
                                     String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DISPLAY_NAME;

        try {
            cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception e) {
            Log.e("getNameFile", "error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    private static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority()) || "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isVsmart(Uri uri) {
        return "com.vsmart.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getPathFromData(Context context,Uri data){
        String pathFile;
        try {
            Log.e("DataPDF", "data:" + data.toString());
            pathFile = RealPathUtil.getRealPath(context, data);
            if (pathFile == null)
                pathFile = Utils.getPathFromUri(context, data);
            if (pathFile == null) {
                pathFile = data.toString();
                int index = pathFile.indexOf(":");
                if (index > 0) {
                    pathFile = pathFile.substring(index + 3);
                }
                pathFile = Uri.decode(pathFile);
            }
            if (!TextUtils.isEmpty(pathFile) && pathFile.contains("/raw:")) {
                pathFile = pathFile.substring(pathFile.indexOf("/raw:") + 5);
            }

            Log.e("DataPDF", "pathFile:" + pathFile);

        } catch (Exception e) {
            Log.e("DataPDF", e.getMessage());
            pathFile = String.valueOf(Uri.decode(data.getPath())).replace("/root", "").replace("external/", "storage/emulated/0/")
                    .replace("external_files/", "storage/emulated/0/");
            if (pathFile.contains("SkypeDownload")) {
                int lastIndex = pathFile.lastIndexOf("_");
                pathFile = pathFile.substring(0, lastIndex) + ".pdf";
                pathFile = pathFile.replace("SkypeDownload/", "storage/emulated/0/Download/");
            } else if (pathFile.contains("Telegram")) {
                pathFile = pathFile.replace("device_storage", "storage/emulated/0");
            } else if (pathFile.contains("org.telegram.messenger") || pathFile.contains("media")) {
                pathFile = pathFile.replace("media", "storage/emulated/0");
            }

            ///document/downloads:tessss.pdf

            Log.e("DataPDF", "Exception:" + pathFile);
        }
        return pathFile;
    }

    public static boolean isDriveFile(Uri uri) {
        if ("com.google.android.apps.docs.storage".equals(uri.getAuthority()))
            return true;
        return "com.google.android.apps.docs.storage.legacy".equals(uri.getAuthority());
    }
}