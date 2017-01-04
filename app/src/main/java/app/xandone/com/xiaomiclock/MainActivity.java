package app.xandone.com.xiaomiclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private ClockView mClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClockView = (ClockView) findViewById(R.id.clock);
        mClockView.triangleAnim();
    }
}
