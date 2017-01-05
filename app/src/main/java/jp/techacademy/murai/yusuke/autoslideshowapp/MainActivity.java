package jp.techacademy.murai.yusuke.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Button playButton;              //ボタン
    Button forwardBut;
    Button backBut;
    ArrayList<Uri> arrayList ;      //Uri型のArrayListを作りUriを格納していく



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayList = new ArrayList<Uri>();

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミzッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();
        }
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setTag(0);
        playButton.setOnClickListener(this);

        forwardBut = (Button) findViewById(R.id.forwardBut);
        forwardBut.setOnClickListener(this);

        backBut = (Button) findViewById(R.id.backBut);
        backBut.setOnClickListener(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();

                }
                break;
            default:
                break;
        }
    }



    private void getContentsInfo() {


        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()){
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex =cursor.getColumnIndex(MediaStore.Images.Media._ID);

                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                Log.d("ANDROID", "URI: " + imageUri.toString());

                arrayList.add(imageUri);

//                ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
//                imageVIew.setImageURI(imageUri);

            } while (cursor.moveToNext());
        }
        cursor.close();



    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.forwardBut) {
            Log.d("ANDROID", "forwardButが押されました");


        } else if (v.getId() == R.id.playButton) {
            int numMaxArr = arrayList.size();
            Log.d("ANDROID", "onClick内 arrayList.size= " + numMaxArr);

            for (int l = 0; l < numMaxArr; l++) {
                Log.d("ANDROID", " arrayList.get(l) = " + arrayList.get(l));
            }

        } else if (v.getId() == R.id.backBut) {
            Log.d("ANDROID", "backButが押されました");
        }

    }



}
