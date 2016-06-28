package skin.example.com.rasberrypiled;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class NoroiActivity extends Activity implements View.OnClickListener {
    //初期化
    private int mCount=0;//釘を打った回数をカウントする
    final int on=1;//On,Offの値を定義
    final int off=0;
    private int mot=off;
    private int pel =off;
    private Timer mTimer = null;
    static int mTimeCheck=3000;//一回目の振動モータ停止時間
    private MediaPlayer kugiuchi;//釘打ち効果音
    private MediaPlayer mSuzumushi;//
    private MediaPlayer mDrodro;
    private LoudNess mLoudNess;//録音用
    private ImageView mRosokuOffLeft;
    private VideoView mRosokuOnLeft;
    private ImageView mRosokuOffRight;
    private VideoView mRosokuOnRight;
    private ImageView mRosokuOffCenter;
    private VideoView mRosokuOnCenter;
    private Handler mHandler = new Handler();
    private ImageView mFace;//顔表示用ImageView
    private String mFaceUri;//Uri格納
    private int mComplete=0;//初期化用変数



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroi);
        //全画面表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //釘を打ち付けたときの効果音
        kugiuchi = MediaPlayer.create(this, R.raw.kugiuchi2);
        //鈴虫のBGM
        mSuzumushi = MediaPlayer.create(this, R.raw.suzumushi);
        //ヒュードロドロでお馴染みのBGM
        mDrodro = MediaPlayer.create(this, R.raw.dorodoro);
        //鈴虫のBGMを再生する。
        mSuzumushi.start();



        findViewById(R.id.kugi_left).setVisibility(View.INVISIBLE);
        findViewById(R.id.kugi_right).setVisibility(View.INVISIBLE);
        findViewById(R.id.kugi_center).setVisibility(View.INVISIBLE);


        //MainActivityで取得した画像をUriで読み込んで、藁人形の顔に貼り付ける
        mFace = (ImageView)findViewById(R.id.face);
        Intent intent = getIntent();
        mFaceUri=intent.getStringExtra("FaceData");;
        Uri uri=Uri.parse(mFaceUri);
        try {
                Bitmap bmp = getBitmapFromUri(uri);
                mFace.setImageBitmap(bmp);

        } catch (IOException e) {
                e.printStackTrace();
        }
        //動作スイッチ、蝋燭のBGMとイメージの定義
        View mSwitch =findViewById(R.id.wara);
        mSwitch.setOnClickListener(this);
        mRosokuOnLeft = (VideoView) findViewById(R.id.rosoku_on);
        mRosokuOffLeft=(ImageView)findViewById(R.id.rosoku_off);
        mRosokuOnRight = (VideoView) findViewById(R.id.rosoku_on_left);
        mRosokuOffRight=(ImageView)findViewById(R.id.rosoku_off_left);
        mRosokuOnCenter = (VideoView) findViewById(R.id.rosoku_on_center);
        mRosokuOffCenter=(ImageView)findViewById(R.id.rosoku_off_center);




    }
    //クリックされた時の処理
    @Override
    public void onClick(View v){

        switch(v.getId()) {
            case R.id.wara:
                //カウントが3以上なら、処理をしない
                if (mCount == 3) {
                    break;
                } else {
                    mCount++;//カウンターを回す

                    //一回ボタンを押したら、振動モータを3秒間動作させる
                    if (mCount == 1&&mComplete==0) {
                        mot = on;
                        findViewById(R.id.kugi_left).setVisibility(View.VISIBLE);
                        mRosokuOffLeft.setVisibility(View.INVISIBLE);
                        kugiuchi.start();
                        mRosokuOnLeft.setVideoPath("android.resource://skin.example.com.rasberrypiled/" + R.raw.rosoku_on);
                        mRosokuOnLeft.start();
                        //動画が停止したら、シークバーを最初に戻して再度スタート
                        mRosokuOnLeft.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // 先頭に戻す
                                mRosokuOnLeft.seekTo(0);
                                // 再生開始
                                mRosokuOnLeft.start();
                            }
                        });

                        //振動モータのTimer制御
                       mTimer = new Timer(true);
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mot = off;
                                HttpGetTask task = new HttpGetTask(NoroiActivity.this);
                                task.execute(pel,mot);
                            }


                        }, mTimeCheck);

                        //一回ボタンを押したら、振動モータを3秒間動作させ、ペルチェ素子を動作させる
                    } else if (mCount == 2) {
                        pel = on;
                        mot = on;
                        findViewById(R.id.kugi_right).setVisibility(View.VISIBLE);
                        mRosokuOffRight.setVisibility(View.INVISIBLE);
                        kugiuchi.start();
                        mRosokuOnRight.setVideoPath("android.resource://skin.example.com.rasberrypiled/" + R.raw.rosoku_on);
                        mRosokuOnRight.start();
                        //動画が停止したら、シークバーを最初に戻して再度スタート
                        mRosokuOnLeft.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // 先頭に戻す
                                mRosokuOnLeft.seekTo(0);
                                mRosokuOnRight.seekTo(0);
                                // 再生開始
                                mRosokuOnLeft.start();
                                mRosokuOnRight.start();
                            }
                        });

                        //振動モータのTimer制御
                        mTimer = new Timer(true);
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mot = off;
                                HttpGetTask task = new HttpGetTask(NoroiActivity.this);
                                task.execute(pel,mot);

                            }

                        }, mTimeCheck);

                    } else if (mCount == 3) {
                        mot = on;
                        // ループ設定
                        mDrodro .setLooping(true);
                        mDrodro.seekTo(0);
                        mDrodro.start();
                        findViewById(R.id.kugi_center).setVisibility(View.VISIBLE);
                        mRosokuOffCenter.setVisibility(View.INVISIBLE);
                        kugiuchi.start();
                        mRosokuOnCenter.setVideoPath("android.resource://skin.example.com.rasberrypiled/" + R.raw.rosoku_on);
                        mRosokuOnCenter.start();
                        //動画が停止したら、シークバーを最初に戻して再度スタート
                        mRosokuOnLeft.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // 先頭に戻す
                                mRosokuOnLeft.seekTo(0);
                                mRosokuOnRight.seekTo(0);
                                mRosokuOnCenter.seekTo(0);
                                // 再生開始
                                mRosokuOnLeft.start();
                                mRosokuOnRight.start();
                                mRosokuOnCenter.start();

                            }
                        });
                        //録音開始
                        startRecord();
                    }
                    //一度釘を3回刺して息で消したら、次に藁人形をタップしたときにタイトルに戻る。
                    if(mComplete==1){
                        Intent intent = new Intent(NoroiActivity.this, MainActivity.class);
                        startActivity(intent);
                        mSuzumushi.stop();
                        mComplete=off;
                    }
                    break;
                }
        }

        HttpGetTask task = new HttpGetTask(NoroiActivity.this);
        task.execute(pel,mot);
    }

    //録音がスタートしたら呼び出し
    private void startRecord() {

        mLoudNess = new LoudNess();
        mLoudNess.setOnReachedVolumeListener(
                new	LoudNess.OnReachedVolumeListener() {
                    public void onReachedVolume(final short volume) {
                        mHandler.post(new Runnable() {
                            public void run() {
                                appendTextView(volume);
                            }
                        });
                    }
                });
        new Thread(mLoudNess).start();
    }

    //息が検出できたら、素子の動作を停止する
    private void appendTextView(short volume) {
        pel=off;
        mot=off;
        mCount=off;
        mComplete=on;
        //ヒュードロドロのBGMをストップさせる
        mDrodro.stop();
        //消えた蝋燭の画像を表示する
        mRosokuOffLeft.setVisibility(View.VISIBLE);
        mRosokuOffRight.setVisibility(View.VISIBLE);
        mRosokuOffCenter.setVisibility(View.VISIBLE);
        HttpGetTask task = new HttpGetTask(this);
        task.execute(pel,mot);
        //録音を停止する
        mLoudNess.stop();
    }

    //uriからBitMap化する
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
