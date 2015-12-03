package net.caspervg.llm.bean;

public abstract class LexEntry extends DownloadEntry {

    private int id;
    private String name;
    private String author;

    public LexEntry(int id, String name) {
        this(id, name, null);
    }

    public LexEntry(int id, String name, String author) {
        this.id = id;
        this.name = name;
        this.author = author;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public boolean canDownload() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LexEntry)) return false;

        LexEntry lexEntry = (LexEntry) o;

        if (id != lexEntry.id) return false;
        if (name != null ? !name.equals(lexEntry.name) : lexEntry.name != null) return false;
        return !(author != null ? !author.equals(lexEntry.author) : lexEntry.author != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        return result;
    }
}
