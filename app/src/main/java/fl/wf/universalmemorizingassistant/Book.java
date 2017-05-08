package fl.wf.universalmemorizingassistant;

/**
 * Created by WF on 2017/5/8.
 * Represents the book created by user
 */

class Book {
    private String name;
    private String path;

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
        return "\nBookName:" + name + "\nBookPath:" + path;
    }
}
