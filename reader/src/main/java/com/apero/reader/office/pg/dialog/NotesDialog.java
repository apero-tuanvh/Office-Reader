/*
 * 文件名称:           NotesDialog.java
 *  
 * 编译器:             android2.2
 * 时间:               上午11:21:10
 */
package com.apero.reader.office.pg.dialog;

import java.util.Vector;

import com.apero.reader.R;
import com.apero.reader.office.system.IControl;
import com.apero.reader.office.system.IDialogAction;
import com.apero.reader.office.system.beans.ADialog;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

/**
 * ppt show notes dialog
 * <p>
 * <p>
 * Read版本:       Read V1.0
 * <p>
 * 作者:           jhy1790
 * <p>
 * 日期:           2012-3-16
 * <p>
 * 负责人:         jhy1790
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public class NotesDialog extends ADialog
{
    /**
     * 
     */
    public NotesDialog(IControl control, Context context, IDialogAction action, Vector<Object> model, int dialogID)
    {
        super(control, context, action, model, dialogID, R.string.pg_toolsbar_note);
        init(context);
    }
       
    /**
     * 
     */
    public void init(Context context)
    { 
        notes = new EditText(context);
        notes.setBackgroundColor(Color.WHITE);
        notes.setTextSize(18);
        notes.setPadding(5, 2, 5, 2);
        notes.setGravity(Gravity.TOP);
        if (model != null)
        {
            dialogFrame.post(new Runnable()
            {
                /**
                 * 
                 *
                 */
                public void run()
                {
                    notes.setText((String)model.get(0));
                }
            });
        }
        scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.setHorizontalFadingEdgeEnabled(false);
        scrollView.setFadingEdgeLength(0);
        scrollView.addView(notes);
        dialogFrame.addView(scrollView);
        
        // ok
        ok = new Button(context);        
        ok.setText(R.string.sys_button_ok);
        ok.setOnClickListener(this);  
        dialogFrame.addView(ok);
    }
    
    /**
     * 
     *
     */
    public void doLayout()
    {
        int mWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        int mHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        // 需要减去标题栏的高度
        mHeight -= getWindow().getDecorView().getHeight() - dialogFrame.getHeight();
        mWidth -= GAP * 10;
        mHeight -= GAP * 10;
        LayoutParams params = new LayoutParams(mWidth - GAP * 2, mHeight - ok.getHeight());
        params.leftMargin = GAP;
        params.rightMargin = GAP;
        params.bottomMargin = GAP;
        scrollView.setLayoutParams(params);
    }
    
    /**
     * 
     *
     */
    public void onConfigurationChanged(Configuration newConfig)
    {        
        doLayout();
    }
    
    /**
     * 
     *
     */
    public void onClick(View v)
    {
        dismiss();
    }
    
    /**
     * 
     *
     */
    public void dispose()
    {
        super.dispose();
        scrollView = null;
        notes = null;
    }
    
    //
    private ScrollView scrollView;
    //
    private EditText notes;
}
