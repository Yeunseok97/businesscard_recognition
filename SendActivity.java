package com.example.go.ui_protocol_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SendActivity extends AppCompatActivity {

    private EditText editText;
    private TextView textView, textView2;
    private ImageView imageView;
    private Handler handler = new Handler();

    private Bitmap receivedImageBitmap;
    private Uri receivedImageUri;

    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendToServer();
                    }
                }).start();
            }
        });

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToResultActivity();
            }
        });

        if (getIntent().hasExtra("imageBitmap")) {
            receivedImageBitmap = getIntent().getParcelableExtra("imageBitmap");
            setImageBitmap(receivedImageBitmap);
        } else if (getIntent().hasExtra("imageUri")) {
            receivedImageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            setImageUri(receivedImageUri);
        }

        startServer();
    }

    private void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    private void setImageUri(Uri imageUri) {
        imageView.setImageURI(imageUri);
    }

    private void printClientLog(final String data) {
        Log.d("SendActivity", data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(data + "\n");
            }
        });
    }

    private void printServerLog(final String data) {
        Log.d("SendActivity", data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView2.append(data + "\n");
            }
        });
    }

    private void sendToServer() {
        try {
            int portNumber = 5001;
            Socket sock = new Socket("localhost", portNumber);
            printClientLog("소켓 연결함");

            if (receivedImageBitmap != null) {
                sendImage(sock, receivedImageBitmap);
            } else if (receivedImageUri != null) {
                Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(getContentResolver().openInputStream(receivedImageUri));
                sendImage(sock, bitmap);
            }

            ObjectInputStream insTream = new ObjectInputStream(sock.getInputStream());
            String serverResponse = (String) insTream.readObject();
            printServerLog("서버로부터 받은 응답: " + serverResponse);

            // 클라이언트에서 이미지 데이터 전송 성공 알림 수신 후에 리절트 액티비티로 이미지를 전달
            if (serverResponse.equals("이미지 데이터 전송 성공 알림")) {
                sendToResultActivity();
            }

            sock.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendImage(Socket socket, Bitmap imageBitmap) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            printClientLog("이미지 데이터 전송함");
            outStream.writeObject(byteArray);
            outStream.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int portNumber = 5001;
                    serverSocket = new ServerSocket(portNumber);
                    printServerLog("서버 시작함: " + portNumber);

                    while (true) {
                        Socket sock = serverSocket.accept();
                        InetAddress clientHost = sock.getLocalAddress();
                        int clientPort = sock.getPort();
                        printServerLog("클라이언트 연결됨: " + clientHost + " : " + clientPort);

                        ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
                        Object obj = instream.readObject();

                        if (obj instanceof byte[]) {
                            printServerLog("이미지 파일을 받음");
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream((byte[]) obj);
                            Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(byteArrayInputStream);
                            setImageBitmap(bitmap);
                            printClientLog("이미지 데이터 전송 성공 알림 수신");

                            // 클라이언트에서 이미지 데이터 전송 성공 알림 수신 후에 리절트 액티비티로 이미지를 전달
                            sendToResultActivity();
                        } else {
                            printServerLog("알 수 없는 형식의 데이터를 받음");
                            printClientLog("클라이언트에게 결과 전달: " + obj);
                        }

                        ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
                        outstream.writeObject(obj + " from Server.");
                        outstream.flush();

                        printServerLog("수신완료 알림 from Server.");

                        sock.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeServer();
    }

    private void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                printServerLog("서버 종료함");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToResultActivity() {
        Bitmap imageBitmapToSend = getImageBitmapToSend();
        Intent resultActivityIntent = new Intent(SendActivity.this, ResultActivity.class);
        resultActivityIntent.putExtra("imageBitmap", imageBitmapToSend);
        startActivity(resultActivityIntent);
    }

    private Bitmap getImageBitmapToSend() {
        return receivedImageBitmap;
    }
}
