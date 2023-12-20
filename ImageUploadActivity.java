package com.example.go.flask_test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.go.flask_test.api.ApiService;
import com.example.go.flask_test.api.RetrofitClient;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploadActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);

        // 이미지 선택을 위한 갤러리 열기
        openGallery();
    }

    // 갤러리에서 이미지 선택 후 호출되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // 이미지 업로드
                File file = FileUtils.getFile(this, imageUri);
                if (file != null) {
                    uploadImage(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 이미지 업로드 메서드
    private void uploadImage(File file) throws IOException {
        // RequestBody 생성
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part 생성
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        // API 호출
        Call<ResponseBody> call = apiService.uploadImage(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // 서버 응답 성공
                    Log.d("UPLOAD", "Image uploaded successfully");
                } else {
                    // 서버 응답 실패
                    Log.d("UPLOAD", "Image upload failed");
                }

                // 이미지 업로드 완료 후 액티비티 종료
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // 통신 실패 (네트워크 문제 등)
                Log.e("UPLOAD", "Image upload failed", t);

                // 실패 시에도 액티비티 종료
                finish();
            }
        });
    }

    // 갤러리 열기
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }
}