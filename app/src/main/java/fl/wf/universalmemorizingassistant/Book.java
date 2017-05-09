package fl.wf.universalmemorizingassistant;

import android.util.Log;

import static android.content.ContentValues.TAG;


/**
 * Created by WF on 2017/5/8.
 * Represents the book created by user
 */

class Book {
    private int id;
    private String name;
    private String path;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "\nBookID: " + id + "\nBookName: " + name + "\nBookPath: " + path;
    }
}
