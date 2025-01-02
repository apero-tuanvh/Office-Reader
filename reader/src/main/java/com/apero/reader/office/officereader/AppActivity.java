/*
 * 文件名称:           MainControl.java
 *
 * 编译器:             android2.2
 * 时间:               下午1:34:44
 */

package com.apero.reader.office.officereader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.apero.reader.R;
import com.apero.reader.office.common.IOfficeToPicture;
import com.apero.reader.office.constant.EventConstant;
import com.apero.reader.office.constant.MainConstant;
import com.apero.reader.office.constant.wp.WPViewConstant;
import com.apero.reader.office.officereader.beans.AImageButton;
import com.apero.reader.office.officereader.beans.AImageCheckButton;
import com.apero.reader.office.officereader.beans.AToolsbar;
import com.apero.reader.office.officereader.beans.CalloutToolsbar;
import com.apero.reader.office.officereader.beans.PDFToolsbar;
import com.apero.reader.office.officereader.beans.PGToolsbar;
import com.apero.reader.office.officereader.beans.SSToolsbar;
import com.apero.reader.office.officereader.beans.WPToolsbar;
import com.apero.reader.office.officereader.database.DBService;
import com.apero.reader.office.res.ResKit;
import com.apero.reader.office.ss.sheetbar.SheetBar;
import com.apero.reader.office.system.FileKit;
import com.apero.reader.office.system.IControl;
import com.apero.reader.office.system.IMainFrame;
import com.apero.reader.office.system.MainControl;
import com.apero.reader.office.system.beans.pagelist.IPageListViewListener;
import com.apero.reader.office.utils.RealPathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件注释
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            梁金晶
 * <p>
 * 日期:            2011-10-27
 * <p>
 * 负责人:          梁金晶
 * <p>
 * 负责小组:
 * <p>
 * <p>
 */

public class AppActivity extends AppCompatActivity implements IMainFrame {
    /**
     * 构造器
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        control = new MainControl(this);
        appFrame = new AppFrame(getApplicationContext());
        appFrame.post(new Runnable() {
            /**
             */
            public void run() {
                init();
            }
        });
        control.setOffictToPicture(new IOfficeToPicture() {
            public Bitmap getBitmap(int componentWidth, int componentHeight) {
                if (componentWidth == 0 || componentHeight == 0) {
                    return null;
                }
                if (bitmap == null
                        || bitmap.getWidth() != componentWidth
                        || bitmap.getHeight() != componentHeight) {
                    // custom picture size
                    if (bitmap != null) {
                        bitmap.recycle();
                    }
                    //bitmap = Bitmap.createBitmap(800, 600,  Config.ARGB_8888);
                    //
                    bitmap = Bitmap.createBitmap((int) (componentWidth), (int) (componentHeight), Config.ARGB_8888);
                }
                return bitmap;
                //return null;
            }

            public void callBack(Bitmap bitmap) {
                // saveBitmapToFile(bitmap);
            }

            //
            private Bitmap bitmap;

            @Override
            public void setModeType(byte modeType) {
                // TODO Auto-generated method stub

            }

            @Override
            public byte getModeType() {
                // TODO Auto-generated method stub
                return VIEW_CHANGE_END;
            }

            @Override
            public boolean isZoom() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void dispose() {
                // TODO Auto-generated method stub

            }
        });
//        setTheme(control.getSysKit().isVertical(this) ? R.style.title_background_vertical   : R.style.title_background_horizontal);
        setContentView(createMainView());
    }

    RelativeLayout mainView;

    private void initAds() {
    }

    public RelativeLayout createMainView() {
        mainView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_view_office, null);
        RelativeLayout.LayoutParams adsLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        adsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        adsLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        RelativeLayout.LayoutParams appFrameLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainView.addView(appFrame, appFrameLayoutParams);
        return mainView;
    }

    /*private boolean fabExpanded = false;
    private FileItemInfo fileInfo = null;*/

    private void addFabView() {
        // create fab button
//        RelativeLayout fabView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.view_fab, null);
//        RelativeLayout.LayoutParams fabLayoutParams = new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        fabLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//        fabLayoutParams.addRule(RelativeLayout.ABOVE, bannerAdsView.findViewById(R.id.ad_view).getId());
//        fabLayoutParams.setMargins(0, 0, 0, 15);
//        mainView.addView(fabView, fabLayoutParams);
//
//        fabView.findViewById(R.id.fab).setOnClickListener(v -> {
//            if (fabExpanded) {
//                closeSubMenusFab(fabView);
//            } else {
//                openSubMenusFab(fabView);
//            }
//        });
//        if (fileInfo == null) {
//            fabView.findViewById(R.id.layout_fab_favourite).setVisibility(View.GONE);
//        } else {
//            fabView.findViewById(R.id.layout_fab_favourite).setVisibility(View.VISIBLE);
//            ((ImageView) fabView.findViewById(R.id.fab_favourite)).setImageResource(fileInfo.isFavourite() ? R.drawable.ic_favourite_active : R.drawable.ic_favourite_inactive);
//        }
//        fabView.findViewById(R.id.fab_share).setOnClickListener(v -> {
//            shareFileViaEmail(fileUri, fileName);
//            closeSubMenusFab(fabView);
//        });
//        fabView.findViewById(R.id.fab_favourite).setOnClickListener(v -> {
//            fileInfo.setFavourite(!fileInfo.isFavourite());
//            PreferencesUtil.Companion.saveFileInfoToPref(fileInfo);
//            ((ImageView) fabView.findViewById(R.id.fab_favourite)).setImageResource(fileInfo.isFavourite() ? R.drawable.ic_favourite_active : R.drawable.ic_favourite_inactive);
//            closeSubMenusFab(fabView);
//        });
//        fabView.findViewById(R.id.fab_snapscreen).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (control != null && control.canBackLayout()) {
//                    control.setLayoutThreadDied(true);
//                }
//                closeSubMenusFab(fabView);
//                Animation animZoomIn = AnimationUtils.loadAnimation(
//                        getApplicationContext(), R.anim.zoomout
//                );
//                mainView.startAnimation(animZoomIn);
//                new Handler().postDelayed(() -> {
//                    if (!isFinished()) {
//                        CommonUtil.Companion.takeScreenShotAndGotoImageEditor(AppActivity.this, Constants.Companion.getSNAP_SCREEN_FILE_PATH(), CropImage.FLAG_EDIT_SCREEN_SHOT);
//                    }
//                }, Constants.TIME_FOR_WAITING_SCREEN_SHOT);
//            }
//        });
//        fabView.findViewById(R.id.fab_gotopage).setOnClickListener(v -> {
//            showInputTextDiLog(
//                    "",
//                    getString(R.string.type_your_page_to_go), new InputDiLogOnClickListener() {
//                        @Override
//                        public void onOKButtonOnClick(@Nullable String text) {
//                            //TODO goto page
//                        }
//
//                        @Override
//                        public void onCancelButtonOnClick() {
//
//                        }
//                    }, InputType.TYPE_CLASS_NUMBER
//            );
//            closeSubMenusFab(fabView);
//        });
//        fabView.findViewById(R.id.fab_openwith).setOnClickListener(v -> {
//            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileUri.toString()));
//            CommonUtil.Companion.openFileWithOtherApp(this, fileUri, mimeType);
//            closeSubMenusFab(fabView);
//            showFullAds();
//        });
//        fabView.findViewById(R.id.layout_fab_gotopage).setVisibility(View.GONE);
//        closeSubMenusFab(fabView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE == requestCode && control.canBackLayout()) {
//            finish();
//            startActivity(getIntent());
//        }
    }

//    //closes FAB submenus
//    private void closeSubMenusFab(RelativeLayout fabView) {
//        if (fileInfo != null) {
//            fabView.findViewById(R.id.layout_fab_favourite).setVisibility(View.INVISIBLE);
//        }
////        fabView.findViewById(R.id.layout_fab_gotopage).setVisibility(View.INVISIBLE);
////        fabView.findViewById(R.id.layout_fab_openwith).setVisibility(View.INVISIBLE);
//        fabView.findViewById(R.id.layout_fab_share).setVisibility(View.INVISIBLE);
//        fabView.findViewById(R.id.layout_fab_snapscreen).setVisibility(View.INVISIBLE);
//        ((ImageView) fabView.findViewById(R.id.fab)).setImageResource(R.drawable.ic_baseline_add_24);
//        fabExpanded = false;
//    }
//
//    //Opens FAB submenus
//    private void openSubMenusFab(RelativeLayout fabView) {
//        if (fileInfo != null) {
//            fabView.findViewById(R.id.layout_fab_favourite).setVisibility(View.VISIBLE);
//        }
////        fabView.findViewById(R.id.layout_fab_gotopage).setVisibility(View.VISIBLE);
////        fabView.findViewById(R.id.layout_fab_openwith).setVisibility(View.VISIBLE);
//        fabView.findViewById(R.id.layout_fab_share).setVisibility(View.VISIBLE);
//        fabView.findViewById(R.id.layout_fab_snapscreen).setVisibility(View.VISIBLE);
//        ((ImageView) fabView.findViewById(R.id.fab)).setImageResource(R.drawable.ic_baseline_close_24);
//        fabExpanded = true;
//    }

    private void saveBitmapToFile(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        if (tempFilePath == null) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                tempFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            File file = new File(tempFilePath + File.separatorChar + "tempPic");
            if (!file.exists()) {
                file.mkdir();
            }
            tempFilePath = file.getAbsolutePath();
        }

        File file = new File(tempFilePath + File.separatorChar + "export_image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

        } catch (IOException e) {
        } finally {
            //bitmap.recycle();
        }
    }

    public void setButtonEnabled(boolean enabled) {
        if (fullscreen) {
            pageUp.setEnabled(enabled);
            pageDown.setEnabled(enabled);
            penButton.setEnabled(enabled);
            eraserButton.setEnabled(enabled);
            settingsButton.setEnabled(enabled);
        }
    }

    protected void onPause() {
        super.onPause();

        Object obj = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
        if (obj != null && (Boolean) obj) {
            wm.removeView(pageUp);
            wm.removeView(pageDown);
            wm.removeView(penButton);
            wm.removeView(eraserButton);
            wm.removeView(settingsButton);
        }
    }

    protected void onResume() {
        super.onResume();
//        control.setLayoutThreadDied(false);
        Object obj = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
        if (obj != null && (Boolean) obj) {
            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wm.addView(penButton, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height;
            wm.addView(eraserButton, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height * 2;
            wm.addView(settingsButton, wmParams);

            wmParams.gravity = Gravity.LEFT | Gravity.CENTER;
            wmParams.x = MainConstant.GAP;
            wmParams.y = 0;
            wm.addView(pageUp, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;
            wm.addView(pageDown, wmParams);
        }
    }

    /**
     *
     */
    public void onBackPressed() {
        Log.e("appactivity", "onBackPressed");
        if (isSearchbarActive()) {
            showSearchBar(false);
            updateToolsbarStatus();
        } else {
            Object obj = control.getActionValue(EventConstant.PG_SLIDESHOW, null);
            if (obj != null && (Boolean) obj) {
                fullScreen(false);
                //
                this.control.actionEvent(EventConstant.PG_SLIDESHOW_END, null);
            } else {
                if (control.getReader() != null) {
                    control.getReader().abortReader();
                }
                if (marked != dbService.queryItem(MainConstant.TABLE_STAR, filePath)) {
                    if (!marked) {
                        dbService.deleteItem(MainConstant.TABLE_STAR, filePath);
                    } else {
                        dbService.insertStarFiles(MainConstant.TABLE_STAR, filePath);
                    }

                    Intent intent = new Intent();
                    intent.putExtra(MainConstant.INTENT_FILED_MARK_STATUS, marked);
                    setResult(RESULT_OK, intent);
                }
                if (control != null && control.isAutoTest()) {
                    System.exit(0);
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    /**
     *
     */
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (isSearchbarActive()) {
            searchBar.onConfigurationChanged(newConfig);
        }
    }

    /**
     *
     */
    protected void onDestroy() {
        Log.e("appactivity", "onDestroy");
        dispose();
        super.onDestroy();
    }

    /**
     * (non-Javadoc)
     *
     * @see IMainFrame#showProgressBar(boolean)
     */
    public void showProgressBar(boolean visible) {
        setProgressBarIndeterminateVisibility(visible);
    }

    /**
     *
     */
    private void init() {
        Intent intent = getIntent();
        dbService = new DBService(getApplicationContext());
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            fileUri = intent.getData();
            if (fileUri != null) {
                Log.e("Intent ", "action = " + intent.getAction() + " type = " + intent.getType());
                Log.e("Intent ", " fileUri = " + fileUri.toString());
                Log.e("Intent ", " fileUri.getPath = " + fileUri.getPath());
                filePath = RealPathUtil.getRealPath(this, fileUri);
            }
        } else {
            fileUri = (Uri) intent.getExtras().get(MainConstant.INTENT_FILED_FILE_URI);
            filePath = intent.getStringExtra(MainConstant.INTENT_FILED_FILE_PATH);
            fileName = intent.getStringExtra(MainConstant.INTENT_FILED_FILE_NAME);

        }

        if (TextUtils.isEmpty(filePath)) {
            this.filePath = intent.getDataString();
            int index = getFilePath().indexOf(":");
            if (index > 0) {
                filePath = filePath.substring(index + 3);
            }
            filePath = Uri.decode(filePath);
        }

        if (!TextUtils.isEmpty(filePath) && filePath.contains("/raw:")) {
            filePath = filePath.substring(filePath.indexOf("/raw:") + 5);
        }

        // 显示打开文件名称
        if (TextUtils.isEmpty(fileName)) {
            int index = filePath.lastIndexOf(File.separator);
            if (index > 0) {
                fileName = filePath.substring(index + 1);
            } else {
                fileName = filePath;
            }
        }
        Log.e("Intent ", " filePath = " + filePath);
        Log.e("Intent ", " fileName = " + fileName);

        boolean isSupport = FileKit.instance().isSupport(filePath);
        //写入本地数据库
        if (isSupport) {
            dbService.insertRecentFiles(MainConstant.TABLE_RECENT, filePath);
        }
        // create view
        addToolBar();
        addFabView();

        initAds();
//        createView();
        // open file
        control.openFile(filePath);
        // initialization marked
//        initMarked();
    }

//    /**
//     * Used to get file detail from uri.
//     * <p>
//     * 1. Used to get file detail (name & size) from uri.
//     * 2. Getting file details from uri is different for different uri scheme,
//     * 2.a. For "File Uri Scheme" - We will get file from uri & then get its details.
//     * 2.b. For "Content Uri Scheme" - We will get the file details by querying content resolver.
//     *
//     * @param uri Uri.
//     * @return file detail.
//     */
//    public static FileDetail getFileDetailFromUri(final Context context, final Uri uri) {
//        FileDetail fileDetail = null;
//        if (uri != null) {
//            fileDetail = new FileDetail();
//            // File Scheme.
//            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
//                File file = new File(uri.getPath());
//                fileDetail.fileName = file.getName();
//                fileDetail.fileSize = file.length();
//                fileDetail.filePath = file.getPath();
//            }
//            // Content Scheme.
//            else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
//                Cursor returnCursor =
//                        context.getContentResolver().query(uri, null, null, null, null);
//                if (returnCursor != null && returnCursor.moveToFirst()) {
//                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                    int pathIndex = returnCursor.getColumnIndex(OpenableColumns.P);
//                    fileDetail.fileName = returnCursor.getString(nameIndex);
//                    fileDetail.fileSize = returnCursor.getLong(sizeIndex);
//                    fileDetail.filePath = file.getPath();
//                    returnCursor.close();
//                }
//            }
//        }
//        return fileDetail;
//    }
//

    /**
     * File Detail.
     * <p>
     * 1. Model used to hold file details.
     */
//    public static class FileDetail {
//
//        // fileSize.
//        public String fileName;
//
//        // fileSize.
//        public String filePath;
//
//        // fileSize in bytes.
//        public long fileSize;
//
//        /**
//         * Constructor.
//         */
//        public FileDetail() {
//
//        }
//    }
    public void addToolBar() {
        View toolBar = LayoutInflater.from(this).inflate(R.layout.view_toolbar, null, false);
//        int index = filePath.lastIndexOf(File.separator);
//        String name = "";
//        if (index > 0) {
//            name = filePath.substring(index + 1);
//        } else {
//            name = filePath;
//        }
//        toolBar.setBackgroundColor(DatabaseHelper.getColorTheme(this).getColor());
        toolBar.findViewById(R.id.imvSwitchMode).setVisibility(View.GONE);
//        if (!TextUtils.isEmpty(fileName) && (fileName.endsWith(".txt") || fileName.endsWith(".doc") || fileName.endsWith(".docx"))) {
//            toolBar.findViewById(R.id.imvSwitchMode).setVisibility(View.VISIBLE);
//        } else {
//            toolBar.findViewById(R.id.imvSwitchMode).setVisibility(View.GONE);
//        }
        ((TextView) toolBar.findViewById(R.id.tvTitle)).setText(fileName);
        toolBar.findViewById(R.id.imvBack).setOnClickListener(v -> finish());
        toolBar.findViewById(R.id.imvAction).setOnClickListener(v -> {
//            shareFileViaEmail(fileUri, fileName);
        });
        toolBar.findViewById(R.id.imvSwitchMode).setOnClickListener(v -> {
            ((MainControl) getControl()).switchViewMode();
        });
        appFrame.addView(toolBar);
    }

    /**
     * true: show message when zooming
     * false: not show message when zooming
     *
     * @return
     */
    public boolean isShowZoomingMsg() {
        return true;
    }

    /**
     * true: pop up diLog when throw err
     * false: not pop up diLog when throw err
     *
     * @return
     */
    public boolean isPopUpErrorDlg() {
        return true;
    }

    /**
     *
     */
    private void createView() {
        // word
        String file = filePath.toLowerCase();
        if (file.endsWith(MainConstant.FILE_TYPE_DOC) || file.endsWith(MainConstant.FILE_TYPE_DOCX)
                || file.endsWith(MainConstant.FILE_TYPE_TXT)
                || file.endsWith(MainConstant.FILE_TYPE_DOT)
                || file.endsWith(MainConstant.FILE_TYPE_DOTX)
                || file.endsWith(MainConstant.FILE_TYPE_DOTM)) {
            applicationType = MainConstant.APPLICATION_TYPE_WP;
            toolsbar = new WPToolsbar(getApplicationContext(), control);
        }
        // excel
        else if (file.endsWith(MainConstant.FILE_TYPE_XLS)
                || file.endsWith(MainConstant.FILE_TYPE_XLSX)
                || file.endsWith(MainConstant.FILE_TYPE_XLT)
                || file.endsWith(MainConstant.FILE_TYPE_XLTX)
                || file.endsWith(MainConstant.FILE_TYPE_XLTM)
                || file.endsWith(MainConstant.FILE_TYPE_XLSM)) {
            applicationType = MainConstant.APPLICATION_TYPE_SS;
            toolsbar = new SSToolsbar(getApplicationContext(), control);
        }
        // PowerPoint
        else if (file.endsWith(MainConstant.FILE_TYPE_PPT)
                || file.endsWith(MainConstant.FILE_TYPE_PPTX)
                || file.endsWith(MainConstant.FILE_TYPE_POT)
                || file.endsWith(MainConstant.FILE_TYPE_PPTM)
                || file.endsWith(MainConstant.FILE_TYPE_POTX)
                || file.endsWith(MainConstant.FILE_TYPE_POTM)) {
            applicationType = MainConstant.APPLICATION_TYPE_PPT;
            toolsbar = new PGToolsbar(getApplicationContext(), control);
        }
        // PDF document
        else if (file.endsWith(MainConstant.FILE_TYPE_PDF)) {
            applicationType = MainConstant.APPLICATION_TYPE_PDF;
            toolsbar = new PDFToolsbar(getApplicationContext(), control);
        } else {
            applicationType = MainConstant.APPLICATION_TYPE_WP;
            toolsbar = new WPToolsbar(getApplicationContext(), control);
        }
        // 添加tool bar
        appFrame.addView(toolsbar);
    }

    /**
     * @return
     */
    private boolean isSearchbarActive() {
        if (appFrame == null || isDispose) {
            return false;
        }
        int count = appFrame.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = appFrame.getChildAt(i);
            if (v instanceof FindToolBar) {
                return v.getVisibility() == View.VISIBLE;
            }
        }
        return false;
    }

    /**
     * show toolbar or search bar
     *
     * @param show
     */
    public void showSearchBar(boolean show) {
        //show search bar
        if (show) {
            if (searchBar == null) {
                searchBar = new FindToolBar(this, control);
                appFrame.addView(searchBar, 0);
            }
            searchBar.setVisibility(View.VISIBLE);
            toolsbar.setVisibility(View.GONE);
        }
        // hide search bar
        else {
            if (searchBar != null) {
                searchBar.setVisibility(View.GONE);
            }
            toolsbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * show toolbar or search bar
     *
     * @param show
     */
    public void showCalloutToolsBar(boolean show) {
        //show callout bar
        if (show) {
            if (calloutBar == null) {
                calloutBar = new CalloutToolsbar(getApplicationContext(), control);
                appFrame.addView(calloutBar, 0);
            }
            calloutBar.setCheckState(EventConstant.APP_PEN_ID, AImageCheckButton.CHECK);
            calloutBar.setCheckState(EventConstant.APP_ERASER_ID, AImageCheckButton.UNCHECK);
            calloutBar.setVisibility(View.VISIBLE);
            toolsbar.setVisibility(View.GONE);
        }
        // hide callout bar
        else {
            if (calloutBar != null) {
                calloutBar.setVisibility(View.GONE);
            }
            toolsbar.setVisibility(View.VISIBLE);
        }
    }

    public void setPenUnChecked() {
        if (fullscreen) {
            penButton.setState(AImageCheckButton.UNCHECK);
            penButton.postInvalidate();
        } else {
            calloutBar.setCheckState(EventConstant.APP_PEN_ID, AImageCheckButton.UNCHECK);
            calloutBar.postInvalidate();
        }
    }

    public void setEraserUnChecked() {
        if (fullscreen) {
            eraserButton.setState(AImageCheckButton.UNCHECK);
            eraserButton.postInvalidate();
        } else {
            calloutBar.setCheckState(EventConstant.APP_ERASER_ID, AImageCheckButton.UNCHECK);
            calloutBar.postInvalidate();
        }
    }

    /**
     * set the find back button and find forward button state
     *
     * @param state
     */
    public void setFindBackForwardState(boolean state) {
        if (isSearchbarActive()) {
            searchBar.setEnabled(EventConstant.APP_FIND_BACKWARD, state);
            searchBar.setEnabled(EventConstant.APP_FIND_FORWARD, state);
        }
    }

    /**
     * 发送邮件
     */
    public void fileShare() {
        ArrayList<Uri> list = new ArrayList<Uri>();

        File file = new File(filePath);
        list.add(Uri.fromFile(file));

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_STREAM, list);
        intent.setType("application/octet-stream");
//        startActivity(Intent.createChooser(intent, getResources().getText(R.string.sys_share_title)));
    }

    /**
     * @return
     */
    public void initMarked() {
        marked = dbService.queryItem(MainConstant.TABLE_STAR, filePath);
        if (marked) {
            toolsbar.setCheckState(EventConstant.FILE_MARK_STAR_ID, AImageCheckButton.CHECK);
        } else {
            toolsbar.setCheckState(EventConstant.FILE_MARK_STAR_ID, AImageCheckButton.UNCHECK);
        }
    }

    /**
     * @return
     */
    private void markFile() {
        marked = !marked;
    }

    public void resetTitle(String title) {
        if (title != null) {
            this.setTitle(title);
        }
    }

    public FindToolBar getSearchBar() {
        return searchBar;
    }

    /**
     *
     */
//    public DiLog onCreateDiLog(int id) {
//        return control.getDiLog(this, id);
//    }

    /**
     * 更新工具条的状态
     */
    public void updateToolsbarStatus() {
        if (appFrame == null || isDispose) {
            return;
        }
        int count = appFrame.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = appFrame.getChildAt(i);
            if (v instanceof AToolsbar) {
                ((AToolsbar) v).updateStatus();
            }
        }
    }

    /**
     * set word application default view (Normal view or Page view)
     * <p>
     * 1锟斤拷normal view mode
     * 2, print view mode
     */
    public void switchViewMode(IControl control, int viewMode) {
        if (control == null) {
            return;
        }
        if (viewMode < 0 || viewMode > 2) {
            viewMode = 0;
        }
        control.actionEvent(EventConstant.WP_SWITCH_VIEW, viewMode);
    }

    /**
     *
     */
    public IControl getControl() {
        return this.control;
    }

    /**
     *
     */
    public int getApplicationType() {
        return this.applicationType;
    }

    /**
     * @return Returns the filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     *
     */
    public Activity getActivity() {
        return this;
    }

    /**
     * do action，this is method don't call <code>control.actionEvent</code> method, Easily lead to infinite loop
     *
     * @param actionID action ID
     * @param obj      acValue
     * @return True if the listener has consumed the event, false otherwise.
     */
    public boolean doActionEvent(int actionID, Object obj) {
        try {
            switch (actionID) {
                case EventConstant.SYS_RESET_TITLE_ID:
                    setTitle((String) obj);
                    break;

                case EventConstant.SYS_ONBACK_ID:
//                    finish();
                    Log.e("appactivity", "do action SYS_ONBACK_ID");
                    onBackPressed();
                    break;

                case EventConstant.SYS_UPDATE_TOOLSBAR_BUTTON_STATUS: //update toolsbar state
                    updateToolsbarStatus();
                    break;

                case EventConstant.SYS_HELP_ID: //show help net
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
//                            .getString(R.string.sys_url_wxiwei)));
//                    startActivity(intent);
                    break;

                case EventConstant.APP_FIND_ID: //show search bar
                    showSearchBar(true);
                    break;

                case EventConstant.APP_SHARE_ID: //share file
                    fileShare();
                    break;

                case EventConstant.FILE_MARK_STAR_ID: //mark
                    markFile();
                    break;

                case EventConstant.APP_FINDING:
                    String content = ((String) obj).trim();
                    if (content.length() > 0 && control.getFind().find(content)) {
                        setFindBackForwardState(true);
                    } else {
                        setFindBackForwardState(false);
                        toast.setText(getLocalString("DILog_FIND_NOT_FOUND"));
                        toast.show();
                    }
                    break;

                case EventConstant.APP_FIND_BACKWARD:
                    if (!control.getFind().findBackward()) {
                        searchBar.setEnabled(EventConstant.APP_FIND_BACKWARD, false);
                        toast.setText(getLocalString("DILog_FIND_TO_BEGIN"));
                        toast.show();
                    } else {
                        searchBar.setEnabled(EventConstant.APP_FIND_FORWARD, true);
                    }
                    break;

                case EventConstant.APP_FIND_FORWARD:
                    if (!control.getFind().findForward()) {
                        searchBar.setEnabled(EventConstant.APP_FIND_FORWARD, false);
                        toast.setText(getLocalString("DILog_FIND_TO_END"));
                        toast.show();
                    } else {
                        searchBar.setEnabled(EventConstant.APP_FIND_BACKWARD, true);
                    }
                    break;

                case EventConstant.SS_CHANGE_SHEET:
                    Log.e("AppActivity.817", "" + obj);
                    bottomBar.setFocusSheetButton((Integer) obj);
                    break;

                case EventConstant.APP_DRAW_ID:
                    showCalloutToolsBar(true);
                    control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTDRAW);
                    appFrame.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);

                        }
                    });

                    break;

                case EventConstant.APP_BACK_ID:
                    showCalloutToolsBar(false);
                    control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                    break;

                case EventConstant.APP_PEN_ID:
                    if ((Boolean) obj) {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTDRAW);
                        setEraserUnChecked();
                        appFrame.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                control.actionEvent(EventConstant.APP_INIT_CALLOUTVIEW_ID, null);

                            }
                        });
                    } else {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                    }
                    break;

                case EventConstant.APP_ERASER_ID:
                    if ((Boolean) obj) {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_CALLOUTERASE);
                        setPenUnChecked();
                    } else {
                        control.getSysKit().getCalloutManager().setDrawingMode(MainConstant.DRAWMODE_NORMAL);
                    }
                    break;

                case EventConstant.APP_COLOR_ID:
//                    ColorPickerDiLog dlg = new ColorPickerDiLog(this, control);
//                    dlg.show();
//                    dlg.setOnDismissListener(new OnDismissListener() {
//
//                        @Override
//                        public void onDismiss(DiLogInterface diLog) {
//                            setButtonEnabled(true);
//                        }
//                    });
//                    setButtonEnabled(false);
                    break;

                default:
                    return false;
            }
        } catch (Exception e) {
            control.getSysKit().getErrorKit().writerLog(e);
        }
        return true;
    }

    /**
     * pages count change
     */
    public void onPagesCountChange() {

    }

    /**
     * current display pags change
     */
    public void onCurrentPageChange() {

    }

    private boolean isOpenFileFinish = false;

    /**
     *
     */
    public void openFileFinish() {
        // 加一条与应用视图分隔的灰色线
        gapView = new View(getApplicationContext());
        gapView.setBackgroundColor(ContextCompat.getColor(this, R.color.black_overlay));
        appFrame.addView(gapView, new LayoutParams(LayoutParams.MATCH_PARENT, 1));
        //
        View app = control.getView();
        appFrame.addView(app,
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        /*if (applicationType == MainConstant.APPLICATION_TYPE_SS)
        {
            bottomBar = new SheetBar(getApplicationContext(), control, getResources().getDisplayMetrics().widthPixels);
            appFrame.addView(bottomBar, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }*/
    }

    /**
     *
     */
    public int getBottomBarHeight() {
        if (bottomBar != null) {
            return bottomBar.getSheetbarHeight();
        }
        return 0;
    }

    /**
     *
     */
    public int getTopBarHeight() {
        return 0;
    }

    /**
     * event method, office engine dispatch
     *
     * @param v      event source
     * @param e1     MotionEvent instance
     * @param e2     MotionEvent instance
     * @param xValue eventNethodType is ON_SCROLL, this is value distanceX
     *               eventNethodType is ON_FLING, this is value velocityY
     *               eventNethodType is other type, this is value -1
     * @param yValue eventNethodType is ON_SCROLL, this is value distanceY
     *               eventNethodType is ON_FLING, this is value velocityY
     *               eventNethodType is other type, this is value -1
     * @see IMainFrame#ON_CLICK
     * @see IMainFrame#ON_DOUBLE_TAP
     * @see IMainFrame#ON_DOUBLE_TAP_EVENT
     * @see IMainFrame#ON_DOWN
     * @see IMainFrame#ON_FLING
     * @see IMainFrame#ON_LONG_PRESS
     * @see IMainFrame#ON_SCROLL
     * @see IMainFrame#ON_SHOW_PRESS
     * @see IMainFrame#ON_SINGLE_TAP_CONFIRMED
     * @see IMainFrame#ON_SINGLE_TAP_UP
     * @see IMainFrame#ON_TOUCH
     */
    public boolean onEventMethod(View v, MotionEvent e1, MotionEvent e2, float xValue,
                                 float yValue, byte eventMethodType) {
        return false;
    }


    public void changePage() {
    }

    /**
     *
     */
    public String getAppName() {
        return "";
    }

    /**
     * 是否绘制页码
     */
    public boolean isDrawPageNumber() {
        return true;
    }

    /**
     * 是否支持zoom in / zoom out
     */
    public boolean isTouchZoom() {
        return true;
    }

    /**
     * Word application 默认视图(Normal or Page)
     *
     * @return WPViewConstant.PAGE_ROOT or WPViewConstant.NORMAL_ROOT
     */
    public byte getWordDefaultView() {
        return WPViewConstant.PAGE_ROOT;
        //return WPViewConstant.NORMAL_ROOT;
    }

    /**
     * normal view, changed after zoom bend, you need to re-layout
     *
     * @return true   re-layout
     * false  don't re-layout
     */
    public boolean isZoomAfterLayoutForWord() {
        return true;
    }

    /**
     * init float button, for slideshow pageup/pagedown
     */
    private void initFloatButton() {
        //icon width and height
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(getResources(), R.drawable.file_slideshow_left, opts);
//
//        //load page up button
//        Resources res = getResources();
//        pageUp = new AImageButton(this, control, res.getString(R.string.pg_slideshow_pageup), -1,
//                -1, EventConstant.APP_PAGE_UP_ID);
//        pageUp.setNormalBgResID(R.drawable.file_slideshow_left);
//        pageUp.setPushBgResID(R.drawable.file_slideshow_left_push);
//        pageUp.setLayoutParams(new LayoutParams(opts.outWidth, opts.outHeight));
//
//        //load page down button
//        pageDown = new AImageButton(this, control, res.getString(R.string.pg_slideshow_pagedown),
//                -1, -1, EventConstant.APP_PAGE_DOWN_ID);
//        pageDown.setNormalBgResID(R.drawable.file_slideshow_right);
//        pageDown.setPushBgResID(R.drawable.file_slideshow_right_push);
//        pageDown.setLayoutParams(new LayoutParams(opts.outWidth, opts.outHeight));
//
//        BitmapFactory.decodeResource(getResources(), R.drawable.file_slideshow_pen_normal, opts);
//        // load pen button
//        penButton = new AImageCheckButton(this, control,
//                res.getString(R.string.app_toolsbar_pen_check), res.getString(R.string.app_toolsbar_pen),
//                R.drawable.file_slideshow_pen_check, R.drawable.file_slideshow_pen_normal,
//                R.drawable.file_slideshow_pen_normal, EventConstant.APP_PEN_ID);
//        penButton.setNormalBgResID(R.drawable.file_slideshow_pen_normal);
//        penButton.setPushBgResID(R.drawable.file_slideshow_pen_push);
//        penButton.setLayoutParams(new LayoutParams(opts.outWidth, opts.outHeight));
//
//        // load eraser button
//        eraserButton = new AImageCheckButton(this, control,
//                res.getString(R.string.app_toolsbar_eraser_check), res.getString(R.string.app_toolsbar_eraser),
//                R.drawable.file_slideshow_eraser_check, R.drawable.file_slideshow_eraser_normal,
//                R.drawable.file_slideshow_eraser_normal, EventConstant.APP_ERASER_ID);
//        eraserButton.setNormalBgResID(R.drawable.file_slideshow_eraser_normal);
//        eraserButton.setPushBgResID(R.drawable.file_slideshow_eraser_push);
//        eraserButton.setLayoutParams(new LayoutParams(opts.outWidth, opts.outHeight));
//
//        // load settings button
//        settingsButton = new AImageButton(this, control, res.getString(R.string.app_toolsbar_color),
//                -1, -1, EventConstant.APP_COLOR_ID);
//        settingsButton.setNormalBgResID(R.drawable.file_slideshow_settings_normal);
//        settingsButton.setPushBgResID(R.drawable.file_slideshow_settings_push);
//        settingsButton.setLayoutParams(new LayoutParams(opts.outWidth, opts.outHeight));
//
//        wm = (WindowManager) getApplicationContext().getSystemService("window");
//        wmParams = new WindowManager.LayoutParams();
//
//        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
//        wmParams.format = PixelFormat.RGBA_8888;
//        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        wmParams.width = opts.outWidth;
//        wmParams.height = opts.outHeight;
    }

    /**
     * full screen, not show top tool bar
     */
    public void fullScreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            if (wm == null || wmParams == null) {
                initFloatButton();
            }

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wm.addView(penButton, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height;
            wm.addView(eraserButton, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.TOP;
            wmParams.x = MainConstant.GAP;
            wmParams.y = wmParams.height * 2;
            wm.addView(settingsButton, wmParams);

            wmParams.gravity = Gravity.LEFT | Gravity.CENTER;
            wmParams.x = MainConstant.GAP;
            wmParams.y = 0;
            wm.addView(pageUp, wmParams);

            wmParams.gravity = Gravity.RIGHT | Gravity.CENTER;
            wm.addView(pageDown, wmParams);

            //hide title and tool bar
            ((View) getWindow().findViewById(android.R.id.title).getParent())
                    .setVisibility(View.GONE);
            //hide status bar
            toolsbar.setVisibility(View.GONE);
            //
            gapView.setVisibility(View.GONE);

            penButton.setState(AImageCheckButton.UNCHECK);
            eraserButton.setState(AImageCheckButton.UNCHECK);

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            //landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        } else {
            wm.removeView(pageUp);
            wm.removeView(pageDown);
            wm.removeView(penButton);
            wm.removeView(eraserButton);
            wm.removeView(settingsButton);
            //show title and tool bar
            ((View) getWindow().findViewById(android.R.id.title).getParent())
                    .setVisibility(View.VISIBLE);
            toolsbar.setVisibility(View.VISIBLE);
            gapView.setVisibility(View.VISIBLE);

            //show status bar
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

    }

    /**
     *
     */

    public void changeZoom() {
    }

    /**
     *
     */
    public void error(int errorCode) {
    }

    /**
     * when need destroy the office engine instance callback this method
     */
    public void destroyEngine() {
        super.onBackPressed();
    }

    /**
     * get Internationalization resource
     *
     * @param resName Internationalization resource name
     */
    public String getLocalString(String resName) {
        return ResKit.instance().getLocalString(resName);
    }

    @Override
    public boolean isShowPasswordDlg() {
        return true;
    }

    @Override
    public boolean isShowProgressBar() {
        return true;
    }

    @Override
    public boolean isShowFindDlg() {
        return true;
    }

    @Override
    public boolean isShowTXTEncodeDlg() {
        return true;
    }

    /**
     * get txt default encode when not showing txt encode diLog
     *
     * @return null if showing txt encode diLog
     */
    public String getTXTDefaultEncode() {
        return "GBK";
    }

    /**
     *
     */
//    public DiLogListener getDiLogListener() {
//        return null;
//    }
    @Override
    public void completeLayout() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isChangePage() {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @param saveLog
     */
    public void setWriteLog(boolean saveLog) {
        this.writeLog = saveLog;
    }

    /**
     * @return
     */
    public boolean isWriteLog() {
        return writeLog;
    }

    /**
     * @param isThumbnail
     */
    public void setThumbnail(boolean isThumbnail) {
        this.isThumbnail = isThumbnail;
    }

    /**
     * get view backgrouond
     *
     * @return
     */
    public Object getViewBackground() {
        return bg;
    }

    /**
     * set flag whether fitzoom can be larger than 100% but smaller than the max zoom
     *
     * @param ignoreOriginalSize
     */
    public void setIgnoreOriginalSize(boolean ignoreOriginalSize) {

    }

    /**
     * @return true fitzoom may be larger than 100% but smaller than the max zoom
     * false fitzoom can not larger than 100%
     */
    public boolean isIgnoreOriginalSize() {
        return false;
    }

    public byte getPageListViewMovingPosition() {
        return IPageListViewListener.Moving_Horizontal;
    }

    /**
     * @return
     */
    public boolean isThumbnail() {
        return isThumbnail;
    }

    /**
     * @param viewList
     */
    public void updateViewImages(List<Integer> viewList) {

    }

    /**
     * @return
     */
    public File getTemporaryDirectory() {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = getExternalFilesDir(null);
        if (file != null) {
            return file;
        } else {
            return getFilesDir();
        }
    }

    /**
     * 释放内存
     */
    public void dispose() {
        isDispose = true;
        if (control != null) {
            control.dispose();
            control = null;
        }
        toolsbar = null;
        searchBar = null;
        bottomBar = null;
        if (dbService != null) {
            dbService.dispose();
            dbService = null;
        }
        if (appFrame != null) {
            int count = appFrame.getChildCount();
            for (int i = 0; i < count; i++) {
                View v = appFrame.getChildAt(i);
                if (v instanceof AToolsbar) {
                    ((AToolsbar) v).dispose();
                }
            }
            appFrame = null;
        }

        if (wm != null) {
            wm = null;
            wmParams = null;
            pageUp.dispose();
            pageDown.dispose();
            penButton.dispose();
            eraserButton.dispose();
            settingsButton.dispose();
            pageUp = null;
            pageDown = null;
            penButton = null;
            eraserButton = null;
            settingsButton = null;
        }
    }

    //
    private boolean isDispose;
    // 当前标星状态
    private boolean marked;
    //
    private int applicationType = -1;
    //
    private Uri fileUri;
    private String filePath;
    private String fileName;

    // application activity control
    private MainControl control;
    //
    private AppFrame appFrame;
    //tool bar
    private AToolsbar toolsbar;
    //search bar
    private FindToolBar searchBar;
    //
    private DBService dbService;
    //
    private SheetBar bottomBar;
    //
    private Toast toast;
    //
    private View gapView;

    //float button: PageUp/PageDown
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private AImageButton pageUp;
    private AImageButton pageDown;
    private AImageCheckButton penButton;
    private AImageCheckButton eraserButton;
    private AImageButton settingsButton;

    //whether write log to temporary file
    private boolean writeLog = true;
    //open file to get thumbnail, or not
    private boolean isThumbnail;
    //view background
    private Object bg = Color.LTGRAY;
    //    private Object bg = ContextCompat.getColor(getApplicationContext(), R.color.COLOR_GREY_DEFAULT);
    //
    private CalloutToolsbar calloutBar;
    //
    private boolean fullscreen;
    //
    private String tempFilePath;
}
