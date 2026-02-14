package com.example.flamingcoding.androidTrials;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flamingcoding.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitView();
    }

    private void InitView() {
        Button homeButton1 = findViewById(R.id.HomeButton1);
        homeButton1.setOnClickListener(v -> {
//            Toast.makeText(this, "HomeButton1 Click", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, WorkManagerPracticeActivity.class);
            startActivity(intent);
        });

        Button homeButton2 = findViewById(R.id.HomeButton2);
        homeButton2.setOnClickListener(v -> {
            // 跳转到SecondActivity
//            Intent intent = new Intent(this, SecondActivity.class);
//            this.startActivity(intent);

            // 传递数据
//            Intent intent = new Intent(this, SecondActivity.class);
//            String data = "Home To Second";
//            intent.putExtra("extra_data", data);
//            this.startActivity(intent);

            // 接受下一个Activity销毁时返回的数据
            Intent intent = new Intent(this, SecondActivity.class);
            startActivityForResult(intent, 1);
        });

        Button homeButton3 = findViewById(R.id.HomeButton3);
        homeButton3.setOnClickListener(v -> {
            // 隐式跳转到SecondActivity
//            Intent intent = new Intent("com.example.flamingcoding.ACTION_START").setClassName(getPackageName(), "com.example.flamingcoding.AndroidTrials.SecondActivity");;
//            this.startActivity(intent);

            // 隐式打开浏览器，跳转到百度
            Intent intent1 = new Intent(Intent.ACTION_VIEW);
            intent1.setData(Uri.parse("https://www.baidu.com"));
            startActivity(intent1);

            // 隐式拉起通话 10086
//            Intent intent2 = new Intent(Intent.ACTION_DIAL);
//            intent2.setData(Uri.parse("tel:10086"));
//            startActivity(intent2);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_item) {
            Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.remove_item) {
            Toast.makeText(this, "You clicked Remove", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    // 通过onActivityResult接受回来的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String returnedData = data.getStringExtra("data_return");
                    StringBuilder builder = new StringBuilder("returned data is ");
                    builder.append(returnedData);
                    if (returnedData != null) {
                        Log.d("HomeActivity", builder.toString());
                    }
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ActivityLifeCycle", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ActivityLifeCycle", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ActivityLifeCycle", "onDestroy");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("ActivityLifeCycle", "onRestart");
    }
}