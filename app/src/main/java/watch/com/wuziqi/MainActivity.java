package watch.com.wuziqi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import watch.com.wuziqi.view.GomokuPanel;

public class MainActivity extends AppCompatActivity {
    private GomokuPanel mFIRPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mFIRPanel = (GomokuPanel) findViewById(R.id.id_fir_panel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_restart:
                mFIRPanel.restart();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
