package com.example.go.flask_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 갤러리 열기 버튼 클릭 시
    public void openGallery(View view) {
        Intent intent = new Intent(this, ImageUploadActivity.class);
        startActivity(intent);
    }
}