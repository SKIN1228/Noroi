package skin.example.com.rasberrypiled;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


public class LoudNess implements Runnable {
    private  OnReachedVolumeListener mListener;
    private boolean isRecording =true;
    private static final int SAMPLE_RATE=44100;
    private short mBoarderVolume=15000;//しきい値

    public void stop(){
        if (isRecording == true){
            isRecording =false;
        }
    }

    public void run(){
        android.os.Process.setThreadPriority(
                android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        int bufferSize = AudioRecord.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        short[] buffer = new short[bufferSize];
        audioRecord.startRecording();
        while (isRecording) {
            audioRecord.read(buffer, 0, bufferSize);
            short max = 0;
            for (int i = 0; i < bufferSize; i++)
            {
                max = (short) Math.max(max, buffer[i]);
            }
            if (max > mBoarderVolume) {
                if (mListener != null){
                    mListener.onReachedVolume(max);

                }
            }

            try{
                Thread.sleep(100);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        audioRecord.stop();
        audioRecord.release();
    }

    public interface OnReachedVolumeListener{
        void onReachedVolume(short volume);
    }

    public void setOnReachedVolumeListener(OnReachedVolumeListener listener){
        mListener = listener;
    }
}
