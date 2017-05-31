package fl.wf.universalmemorizingassistant;

import android.content.Context;

/**
 * Created by WF on 2017/5/31.
 * Used to store dynamic data, such as translation
 */

public class BasicDynamicData {
    public String sheetName;
    public String hint;
    public String answer;
    public String noData;
    public String times;

    public BasicDynamicData(Context context) {
        sheetName = context.getString(R.string.sheet_sheet_name);
        hint = context.getString(R.string.sheet_hint);
        answer = context.getString(R.string.sheet_answer);
        noData = context.getString(R.string.sheet_no_data);
        times=context.getString(R.string.sheet_times);
    }
}
