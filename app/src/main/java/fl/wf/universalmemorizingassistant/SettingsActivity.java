package fl.wf.universalmemorizingassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "FLWFSettingsActivity";
    private String[] bookNames;
    ListView booksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bookNames = getIntent().getStringArrayExtra("bookNames");
        booksListView = (ListView) findViewById(R.id.lv_settings_books);
        booksListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, bookNames));
    }

    public void onSetThisClicked(View view) {
        int position = booksListView.getCheckedItemPosition();
        Log.d(TAG, "onSetThisClicked: " + position);
//        Toast.makeText(this, position, Toast.LENGTH_SHORT).show();
    }
}
