package net.archeryc.mockclick;

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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 参考 http://blog.csdn.net/mad1989/article/details/38109689/
 *
 * @author yc
 */
public class MainActivity extends AppCompatActivity {

    private EditText etX;
    private EditText etY;
    private EditText etTime;
    private Button btnTarget;
    private Button btnSingleClick;
    private Button btnLoopClick;


    private ScheduledExecutorService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etX = findViewById(R.id.et_x);
        etY = findViewById(R.id.et_y);
        btnTarget = findViewById(R.id.btn_target);
        btnSingleClick = findViewById(R.id.btn_single_click);
        btnLoopClick = findViewById(R.id.btn_loop_click);
        etTime = findViewById(R.id.et_time);


        btnSingleClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetX = etX.getText().toString();
                String targetY = etY.getText().toString();
                doClick(targetX, targetY);
            }
        });

        btnLoopClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int period;
                try {
                    period = Integer.parseInt(etTime.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "请输入数字", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (service != null) {
                    service.shutdown();
                }
                service = new ScheduledThreadPoolExecutor(1);
                service.scheduleAtFixedRate(new MyTask(), 0, period, TimeUnit.MILLISECONDS);
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


    private class MyTask implements Runnable {

        @Override
        public void run() {
            String targetX = etX.getText().toString();
            String targetY = etY.getText().toString();
            doClick(targetX, targetY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (service != null) {
            service.shutdown();
        }
    }
}
