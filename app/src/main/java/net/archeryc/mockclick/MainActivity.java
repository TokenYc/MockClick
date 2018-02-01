package net.archeryc.mockclick;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 参考 http://blog.csdn.net/mad1989/article/details/38109689/
 * @author yc
 */
public class MainActivity extends AppCompatActivity {

    private EditText etX;
    private EditText etY;
    private Button btnSet;
    private Button btnTarget;
    private Button btnSingleClick;
    private Button btnLoopClick;

    private String mTargetX;
    private String mTargetY;

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etX = findViewById(R.id.et_x);
        etY = findViewById(R.id.et_y);
        btnSet = findViewById(R.id.btn_set);
        btnTarget = findViewById(R.id.btn_target);
        btnSingleClick = findViewById(R.id.btn_single_click);
        btnLoopClick = findViewById(R.id.btn_loop_click);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTargetX = etX.getText().toString();
                mTargetY = etY.getText().toString();
            }
        });

        btnSingleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClick(mTargetX,mTargetY);
            }
        });

        btnLoopClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimer==null){
                    mTimer=new Timer();
                    mTimer.schedule(new MyTask(),0,5000);
                }
            }
        });


        btnTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "不要点啦", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void doClick(String x, String y) {
        execShellCmd("input tap" + "\t" + x + "\t" + y);
    }

    /**
     * 执行shell命令
     *
     * @param cmd
     */
    private void execShellCmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec("su");

            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class MyTask extends TimerTask{

        @Override
        public void run() {
            doClick(mTargetX,mTargetY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer!=null){
            mTimer.cancel();
            mTimer=null;
        }
    }
}
