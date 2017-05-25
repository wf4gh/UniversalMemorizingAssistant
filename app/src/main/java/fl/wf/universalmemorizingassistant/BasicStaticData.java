package fl.wf.universalmemorizingassistant;

import java.io.File;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by WF on 2017/5/16.
 * Used to store static data
 */

final class BasicStaticData {
    static final String appFolder = "/U_Memorizing";
    static final String appBookDataFileName = "/BookData.xml";
    // TODO: 2017/5/25  use this two static file to replace many things......
    static final File appFolderFile = new File(getExternalStorageDirectory() + appFolder);
    static final File appBookDataFile = new File(getExternalStorageDirectory() + appFolder + appBookDataFileName);
}
