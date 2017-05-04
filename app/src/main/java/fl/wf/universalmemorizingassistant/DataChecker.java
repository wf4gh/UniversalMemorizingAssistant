package fl.wf.universalmemorizingassistant;

import android.os.Environment;

/**
 * Created by WF on 2017/5/4.
 * For now,just used to check if external storage is available for read and write.
 */

final class DataChecker {

    //Checks if external storage is available for read and write
    static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
}
