package skin.example.com.rasberrypiled;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements View.OnClickListener {
    private int mCount=0;
    final int on=1;
    final int off=0;
    private int mot=off;
    private int pel =off;
    private Timer mTimer = null;
    static int mTimeCheck=10000;
    private MediaPlayer kugiuchi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kugiuchi = MediaPlayer.create(this, R.raw.kugiuchi);

        View mSwitch =findViewById(R.id.wara);
        mSwitch.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.wara:
                mCount++;
                findViewById(R.id.kugi1).setVisibility(View.VISIBLE);

                if(mCount==1){
                    mot=on;
                    //Toast.makeText(MainActivity.this, "mot=on", Toast.LENGTH_SHORT).show();
                    kugiuchi.start();
                    mTimer=new Timer(true);
                    mTimer.schedule(new TimerTask(){
                        @Override
                        public void run(){
                            mot=off;
                            //Toast.makeText(MainActivity.this, "mot=off", Toast.LENGTH_SHORT).show();
                        }

                    },mTimeCheck);

                }else if(mCount==2){
                    pel=on;
                    kugiuchi.start();
                }else{
                    pel=off;
                    mot=off;
                    mCount=off;
                }

                //Toast.makeText(MainActivity.this, String.format("count:%d", mCount), Toast.LENGTH_SHORT).show();
                break;
        }

        HttpGetTask task = new HttpGetTask(this);
        task.execute(pel,mot);
    }

}

