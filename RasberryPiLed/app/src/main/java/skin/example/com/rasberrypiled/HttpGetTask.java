package skin.example.com.rasberrypiled;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpGetTask extends AsyncTask<Integer, Void, Void>{
    private  final String DEFAULTURL = "http://192.168.11.20/~pi/noroi.php?";

    private Activity mParentActivity;
    private ProgressDialog mDialog = null;
    private String mUri = null;

    public HttpGetTask(Activity parentActivity){
        this.mParentActivity = parentActivity;
    }

    //タスク開始
    @Override
    protected void onPreExecute(){
    }

    //メイン処理
    @Override
    protected Void doInBackground(Integer... argO){
        mUri = DEFAULTURL + "pel=" + argO[0].toString()+"&mot=" + argO[1].toString();
        exec_get();
        return null;
    }
    //タスク終了時
    @Override
    protected  void onPostExecute(Void result){
    }

    private String exec_get(){
        HttpURLConnection http=null;
        InputStream in = null;
        String src = "";
        try {
            URL url = new URL(mUri);
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.connect();

            in = http.getInputStream();

            byte[] line = new byte[1024];
            int size;
            while (true) {
                size = in.read(line);
                if (size <= 0) {
                    break;
                }
                src += new String(line);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if(http != null){
                    http.disconnect();
                }
                if(in != null){
                    in.close();
                }
            }catch (Exception ignored){
            }
        }
        return src;
    }

}
