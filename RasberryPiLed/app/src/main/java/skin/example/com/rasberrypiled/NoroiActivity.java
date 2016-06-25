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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;
import android.os.Bundle;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class NoroiActivity extends Activity implements View.OnClickListener {
    private int mCount=0;
    final int on=1;
    final int off=0;
    private int mot=off;
    private int pel =off;
    private Timer mTimer = null;
    static int mTimeCheck=1000;
    private MediaPlayer kugiuchi;
    private LoudNess mLoudNess;
    private ImageView mRosokuOffLeft;
    private VideoView mRosokuOnLeft;
    private ImageView mRosokuOffRight;
    private VideoView mRosokuOnRight;
    private ImageView mRosokuOffCenter;
    private VideoView mRosokuOnCenter;
    private Handler mHandler = new Handler();
    private ImageView mFace;
    private String mFaceUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noroi);

        kugiuchi = MediaPlayer.create(this, R.raw.kugiuchi2);
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

        View mSwitch =findViewById(R.id.wara);
        mSwitch.setOnClickListener(this);
        mRosokuOnLeft = (VideoView) findViewById(R.id.rosoku_on);
        mRosokuOffLeft=(ImageView)findViewById(R.id.rosoku_off);
        mRosokuOnRight = (VideoView) findViewById(R.id.rosoku_on_left);
        mRosokuOffRight=(ImageView)findViewById(R.id.rosoku_off_left);
        mRosokuOnCenter = (VideoView) findViewById(R.id.rosoku_on_center);
        mRosokuOffCenter=(ImageView)findViewById(R.id.rosoku_off_center);




    }
    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.wara:
                if (mCount == 3) {
                    break;
                } else {
                    mCount++;



                    if (mCount == 1) {
                        mot = on;
                        findViewById(R.id.kugi_left).setVisibility(View.VISIBLE);
                        mRosokuOffLeft.setVisibility(View.INVISIBLE);
                        kugiuchi.start();
                        mRosokuOnLeft.setVideoPath("android.resource://skin.example.com.rasberrypiled/" + R.raw.rosoku_on);
                        mRosokuOnLeft.start();



                        mRosokuOnLeft.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // 先頭に戻す
                                mRosokuOnLeft.seekTo(0);
                                // 再生開始
                                mRosokuOnLeft.start();
                            }
                        });
                        //mRosokuOffLeft.setVisibility(View.INVISIBLE);

                       /* mTimer = new Timer(true);
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mot = off;
                                //Toast.makeText(MainActivity.this, "mot=off", Toast.LENGTH_SHORT).show();
                            }

                        }, mTimeCheck);*/

                    } else if (mCount == 2) {
                        pel = on;
                        findViewById(R.id.kugi_right).setVisibility(View.VISIBLE);
                        mRosokuOffRight.setVisibility(View.INVISIBLE);
                        kugiuchi.start();
                        mRosokuOnRight.setVideoPath("android.resource://skin.example.com.rasberrypiled/" + R.raw.rosoku_on);
                        mRosokuOnRight.start();
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

                    } else if (mCount == 3) {
                        findViewById(R.id.kugi_center).setVisibility(View.VISIBLE);
                        mRosokuOffCenter.setVisibility(View.INVISIBLE);
                        kugiuchi.start();
                        mRosokuOnCenter.setVideoPath("android.resource://skin.example.com.rasberrypiled/" + R.raw.rosoku_on);
                        mRosokuOnCenter.start();
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
                        startRecord();
                    }
                    break;
                }
        }

        HttpGetTask task = new HttpGetTask(this);
        task.execute(pel,mot);
    }
    private void startRecord() {

        mLoudNess = new LoudNess();
        mLoudNess.setOnReachedVolumeListener(
                new	LoudNess.OnReachedVolumeListener() {
                    // public void onReachedVolume(final short volume,final int size) {
                    public void onReachedVolume(final short volume) {
                        // TODO 自動生成されたメソッド・スタブ
                        mHandler.post(new Runnable() {
                            public void run() {
                                //sVolume=volume;
                                appendTextView(volume);
                            }
                        });
                    }
                });
        new Thread(mLoudNess).start();
    }

    private void appendTextView(short volume) {
        pel=off;
        mot=off;
        mCount=off;
        mRosokuOffLeft.setVisibility(View.VISIBLE);
        mRosokuOffRight.setVisibility(View.VISIBLE);
        mRosokuOffCenter.setVisibility(View.VISIBLE);
        //Toast.makeText(MainActivity.this, "息検出", Toast.LENGTH_SHORT).show();
        HttpGetTask task = new HttpGetTask(this);
        task.execute(pel,mot);
        mLoudNess.stop();
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


}
