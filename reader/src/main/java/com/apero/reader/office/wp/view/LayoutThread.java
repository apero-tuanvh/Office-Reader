/*
 * 文件名称:          LayoutThread.java
 *  
 * 编译器:            android2.2
 * 时间:              下午9:23:20
 */
package com.apero.reader.office.wp.view;

import android.util.Log;
import com.apero.reader.office.simpletext.control.IWord;
import com.apero.reader.office.simpletext.view.IRoot;
import com.apero.reader.office.simpletext.view.IView;

/**
 * 后台布局线程
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            ljj8494
 * <p>
 * 日期:            2011-11-17
 * <p>
 * 负责人:          ljj8494
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public class LayoutThread extends Thread
{
    private boolean isDied;
    /**
     * 
     * @param root
     */
    public LayoutThread(IRoot root)
    {
        this.root = root;
    }
    
    /**
     * 
     */
    public void run()
    {
        while (true)
        {
            if (isDied)
            {
                break;
            }
            try
            {
                Log.e("LayoutThread.run", "root.canBackLayout() = " + root.canBackLayout());
                if (root.canBackLayout())
                {
                    root.backLayout();
                    sleep(50);
                    continue;
                }
                else
                {
                    sleep(1000);
                }
            }
            catch (Exception e)
            {
                if (root != null)
                {
                    IWord word = ((IView)root).getContainer();
                    if (word != null && word.getControl() != null)
                    {
                        word.getControl().getSysKit().getErrorKit().writerLog(e);
                    }
                }
                break;
            }
        }
    }
    
    /**
     * 
     */
    public void setDied(boolean isDied)
    {
        this.isDied = isDied;
    }
    
    /**
     * 
     */
    public void dispose()
    {
        Log.e("LayoutThread", "dispose");
        root = null;
        isDied = true;
    }
    
    private IRoot root;
}
