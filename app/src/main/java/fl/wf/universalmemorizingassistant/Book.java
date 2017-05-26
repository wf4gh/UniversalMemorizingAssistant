package fl.wf.universalmemorizingassistant;


/**
 * Created by WF on 2017/5/8.
 * Represents the book created by user
 */

class Book {
    //    private int id;
    private String name;
    private int maxTimes = 5;
    private int index = 1;
    private int recitedTimes = 0;

    boolean tag = false;

    int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    int getRecitedTimes() {
        return recitedTimes;
    }

    void setRecitedTimes(int recitedTimes) {
        this.recitedTimes = recitedTimes;
    }

    int getMaxTimes() {
        return maxTimes;
    }

    void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "\nBookName: " + name + "\nmaxTimes: " + maxTimes + "\nIndex: " + index + "\nRecitedTimes: " + recitedTimes;
    }
}
