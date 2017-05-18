package fl.wf.universalmemorizingassistant;

/**
 * Created by WF on 2017/5/8.
 * Represents the book created by user
 */

class Book {
    private int id;
    private String name;
    private int maxTimes;
    private int index;
    private int recitedTimes;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRecitedTimes() {
        return recitedTimes;
    }

    public void setRecitedTimes(int recitedTimes) {
        this.recitedTimes = recitedTimes;
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

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

    @Override
    public String toString() {
        return "\nBookID: " + id + "\nBookName: " + name + "\nBookRank: " + maxTimes;
    }
}
