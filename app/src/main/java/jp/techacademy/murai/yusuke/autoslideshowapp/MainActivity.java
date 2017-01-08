package jp.techacademy.murai.yusuke.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    Button playButton;              //ボタン
    Button forwardBut;
    Button backBut;
    TextView textView;

    Timer mainTimer;					//タイマー用
    MainTimerTask mainTimerTask;		//タイマタスククラス
    int count = 0;						//カウント
    Handler mHandler = new Handler();   //UI Threadへのpost用ハンドラ

    ContentResolver resolver;
    Cursor cursor;
    ImageView imageVIew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        playButton.setTag(0);        //Playボタンにタグ付け

        forwardBut = (Button) findViewById(R.id.forwardBut);
        forwardBut.setOnClickListener(this);

        backBut = (Button) findViewById(R.id.backBut);
        backBut.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.textViewTop);

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
        resolver = getContentResolver();
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            showImageURI();
        }else{
            cursor.close();
            playButton.setEnabled(false);
            forwardBut.setEnabled(false);
            backBut.setEnabled(false);
            textView.setText("画像はありません");
        }

    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.forwardBut) {
            Log.d("ANDROID", "forwardButが押されました");

            if (cursor.moveToNext()) {
                showImageURI();
            }else if(cursor.moveToFirst()){
                showImageURI();
            }

        } else if (v.getId() == R.id.backBut) {
            Log.d("ANDROID", "backButが押されました");
            if (cursor.moveToPrevious()) {
                showImageURI();
            } else if(cursor.moveToLast()){
                showImageURI();
            }


        } else if (v.getId() == R.id.playButton) {
            Log.d("ANDROID", "playButtonが押されました");

            switch ((Integer)v.getTag()) {
                case 0:
                    Log.d("ANDROID", "getTag = "+String.valueOf(v.getTag()));
                    mainTimer = new Timer();                //タイマーインスタンス生成
                    mainTimerTask = new MainTimerTask();    //タスククラスインスタンス生成
                    mainTimer.schedule(mainTimerTask, 0,2000); //タイマースケジュール設定＆開始
                    playButton.setText("停止");
                    playButton.setTag(1);
                    forwardBut.setEnabled(false);
                    backBut.setEnabled(false);
                    break;
                case 1:
                    Log.d("ANDROID", "getTag = "+String.valueOf(v.getTag()));
                    mainTimer.cancel();
                    playButton.setText("再生");
                    playButton.setTag(0);
                    forwardBut.setEnabled(true);
                    backBut.setEnabled(true);
                    break;
                default:
                    Log.d("javatest", "getTag=0,1以外");
                    break;
            }
        }
    }



    private void showImageURI() {
        int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
        Long id = cursor.getLong(fieldIndex);
        Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        imageVIew = (ImageView) findViewById(R.id.imageView);
        imageVIew.setImageURI(imageUri);

        Log.d("ANDROID", "cursor.getPosition()= "+cursor.getPosition());
        Log.d("ANDROID", "cursor.getColumnIndex()= "+ fieldIndex);
        Log.d("ANDROID", "cursor.getLong()= "+ id);
        Log.d("ANDROID", "imageUri= "+ imageUri);

    }


//     * タイマータスク派生クラス
//     * run()に定周期で処理したい内容を記述
    public class MainTimerTask extends TimerTask {

        @Override
        public void run() {
            //ここに定周期で実行したい処理を記述
            mHandler.post( new Runnable() {
                public void run() {


                    if (cursor.moveToNext()) {
                        showImageURI();
                    }else if(cursor.moveToFirst()){
                        showImageURI();
                    }

                }
            });
        }
    }



}
