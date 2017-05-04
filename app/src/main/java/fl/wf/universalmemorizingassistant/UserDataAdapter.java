package fl.wf.universalmemorizingassistant;

import java.io.File;

/**
 * Created by WF on 2017/5/4.
 * Used to read and write user data
 */

public class UserDataAdapter {
// TODO: 2017/5/4 Check if the permission is really needed to manipulate these files.The docs said this is only needed in low SDK versions.
    // TODO: 2017/5/4  so may need to add this       android:maxSdkVersion="18"      to the permission in manifest

    void createNewFile(){
        File fileToCreate=new File(File.pathSeparator);
    }
}
