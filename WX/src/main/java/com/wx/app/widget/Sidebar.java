package com.wx.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wx.app.R;
import com.wx.app.fx.others.ContactAdapter;
import com.wx.app.utils.CommonUtils;

import java.util.List;

/**
 * Created by darren foung on 2016/3/2.
 */
public class Sidebar extends View{


    private static final String TAG = "Sidebar";
    private String[] sections = new String[]{"↑","☆","A","B","C","D","E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","#"};
    private Context context;
    private Paint paint;
    private TextView header;
    private int height;
    private ListView listView;

    public Sidebar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Sidebar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();

    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GRAY);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(CommonUtils.dp2px(context, 10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        height = getHeight() / sections.length;
        for(int i = 0; i < sections.length; i++){
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                setBackgroundResource(R.drawable.slidetab_bg_press);
                if(header == null) {
                    header = (TextView) ((View) getParent()).findViewById(R.id.floating_header);
                }
                header.setVisibility(VISIBLE);
                return true;
            case MotionEvent.ACTION_MOVE:
                setHeaderTextAndScroll(event);
                return true;
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT);
                header.setVisibility(INVISIBLE);
                return true;
            case MotionEvent.ACTION_CANCEL:
                setBackgroundColor(Color.TRANSPARENT);
                header.setVisibility(INVISIBLE);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void setHeaderTextAndScroll(MotionEvent event) {
        String headerString = sections[sectionForPoint(event.getY())];
        header.setText(headerString);
        HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
        ContactAdapter contactAdapter = (ContactAdapter) headerViewListAdapter.getWrappedAdapter();
        String[] adapterSections = (String[]) contactAdapter.getSections();
        for(int i = adapterSections.length -1 ; i > -1; i--){
            Log.d(TAG, "==="+adapterSections[i]);
            if(adapterSections[i].equals(headerString)){
                listView.setSelection(contactAdapter.getPositionForSection(i));
                break;
            }
        }
    }

    private int sectionForPoint(float y){
        int index = (int) (y / height);
        if(index < 0){
            index = 0;
        }
        if(index > sections.length -1){
            index = sections.length -1;
        }
        return index;
    }

    public void setListView(ListView listView){
        this.listView = listView;
    }
}
