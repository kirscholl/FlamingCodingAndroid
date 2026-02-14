package com.example.flamingcoding.androidTrials;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.flamingcoding.R;

public class CommonTitleLayoutJava extends LinearLayout {
    public CommonTitleLayoutJava(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.common_title, this);
        Button backBtn = findViewById(R.id.titleBack);
        Button editBtn = findViewById(R.id.titleEdit);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                activity.finish();
            }
        });
        editBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Click Edit Button Java", Toast.LENGTH_LONG).show();
            }
        });
    }
}
