package skin.example.com.rasberrypiled;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity  {
    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private Button button;
    //ボタンを押した時用の効果音
    private MediaPlayer opendoor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //全画面表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        opendoor = MediaPlayer.create(this, R.raw.door);

        button = (Button)findViewById(R.id.button_setpicture);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                opendoor.start();
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
    }

    //取得した画像UriをNoroiActivityに渡す
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            Intent intent = new Intent(MainActivity.this, NoroiActivity.class);

            if (resultData != null) {
                uri = resultData.getData();
                intent.putExtra("FaceData",uri.toString());
                startActivity(intent);

            }
        }
    }


}

