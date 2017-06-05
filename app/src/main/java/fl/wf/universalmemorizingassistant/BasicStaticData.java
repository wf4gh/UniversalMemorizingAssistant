package fl.wf.universalmemorizingassistant;

import android.content.Context;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by WF on 2017/5/16.
 * Used to store static data
 */

final class BasicStaticData {
    private static final String appFolder = "/U_Memorizing";
    private static final String appBookDataFileName = "/BookData.xml";
    static final String absAppFolderPath = getExternalStorageDirectory() + appFolder;
    static final File appFolderFile = new File(getExternalStorageDirectory() + appFolder);
    static final File appBookDataFile = new File(getExternalStorageDirectory() + appFolder + appBookDataFileName);
}
