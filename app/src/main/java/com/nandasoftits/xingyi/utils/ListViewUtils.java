package com.nandasoftits.xingyi.utils;

import android.view.View;
import android.widget.ListView;

public class ListViewUtils {

    public static int getScrollY(ListView listview) {
        View c = listview.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = listview.getFirstVisiblePosition();
        int top = c.getTop();
        return -top + firstVisiblePosition * c.getHeight();
    }

}
