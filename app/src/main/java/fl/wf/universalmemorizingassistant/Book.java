package fl.wf.universalmemorizingassistant;

/**
 * Created by WF on 2017/5/8.
 * Represents the book created by user
 */

class Book {
    private int id;
    private String name;
    private int rank;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
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
        return "\nBookID: " + id + "\nBookName: " + name + "\nBookRank: " + rank;
    }
}
