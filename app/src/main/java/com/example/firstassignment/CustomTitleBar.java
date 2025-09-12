package com.example.firstassignment;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomTitleBar extends LinearLayout {

    private TextView titleTextView;
    private String titleText;

    public CustomTitleBar(Context context) {
        super(context);
        init(null);
    }

    public CustomTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // 加载布局
        LayoutInflater.from(getContext()).inflate(R.layout.custom_title_bar, this, true);
        titleTextView = findViewById(R.id.titleTextView);

        // 从XML属性中获取标题文本
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTitleBar);
            titleText = typedArray.getString(R.styleable.CustomTitleBar_title);
            if (titleText != null) {
                titleTextView.setText(titleText);
            }
            typedArray.recycle();
        }
    }

    // 设置标题的方法
    public void setTitle(String title) {
        this.titleText = title;
        titleTextView.setText(title);
    }

    // 获取标题的方法
    public String getTitle() {
        return titleText;
    }
}