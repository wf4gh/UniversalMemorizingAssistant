package fl.wf.universalmemorizingassistant;

import android.os.Environment;

/**
 * Created by WF on 2017/5/4.
 */

public final class DataChecker{

    /* Checks if external storage is available for read and write */
    public static final boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
