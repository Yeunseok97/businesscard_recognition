package com.example.go.ui_protocol_test;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResultActivity extends AppCompatActivity {

    private TextView nameTextView, numberTextView, informationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        nameTextView = findViewById(R.id.nameTextView);
        numberTextView = findViewById(R.id.numberTextView);
        informationTextView = findViewById(R.id.informationTextView);

        // 앱이 처음 실행될 때 텍스트 파일에서 데이터를 읽어와서 화면에 표시
        String name = readTextFileFromAssets("name/name.txt");
        String number = readTextFileFromAssets("number/number.txt");
        String information = readTextFileFromAssets("information/information.txt");

        if (name != null && number != null) {
            nameTextView.setText("이름: " + name);
            numberTextView.setText("전화번호: " + number);
            informationTextView.setText("부가정보: " + information);
        }

        // 저장 버튼 클릭 이벤트 처리
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 저장 버튼 클릭 이벤트 처리 로직
                if (name == null || number == null) {
                    String errorMessage = "이름 또는 번호가 인식되지 않았습니다.";
                    Toast.makeText(ResultActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    nameTextView.setText("이름: " + name);
                    numberTextView.setText("전화번호: " + number);
                    informationTextView.setText("부가정보: " + information);

                    if (name.isEmpty() || number.isEmpty()) {
                        String errorMessage = "이름 또는 번호가 인식되지 않아 저장할 수 없습니다.";
                        Toast.makeText(ResultActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else {
                        saveContact(name + information, number);
                        finishAffinity();
                    }
                }
            }
        });

        // 다시하기 버튼 클릭 이벤트 처리
        Button retryButton = findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivityIntent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
                finish();
            }
        });
    }

    private void saveContact(String name, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        startActivity(intent);
        Toast.makeText(this, "전화부로 이동합니다. 저장버튼을 눌러 저장하세요.", Toast.LENGTH_SHORT).show();
    }

    private String readTextFileFromAssets(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
            Log.d("FileRead", "File found: " + fileName);

        } catch (IOException e) {
            Log.e("FileRead", "File not found: " + fileName);
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}