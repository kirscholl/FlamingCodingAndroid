package com.example.flamingcoding.common;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flamingcoding.R;

class Test {
    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    Test(String name, int age) {
        this.name = name;
        this.age = age;
    }

    String name = "";
    int age = 0;
}

public class FunctionalTestActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_functional_test_java);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = findViewById(R.id.functionalTestButtonJava);
        button.setOnClickListener(v -> {

        });
        test();
    }

    public void test() {
        final Test t = new Test("1", 2);
        t.name = "2";
        t.age = 12;
        System.out.println("t: " + t.name + t.age);
    }
}