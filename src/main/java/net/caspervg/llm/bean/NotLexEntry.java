package net.caspervg.llm.bean;

public class NotLexEntry extends DownloadEntry {

    private String name;
    private String link;

    public NotLexEntry(String name, String link) {
        this.name = name;
        this.link = link;
    }

    @Override
    public boolean canDownload() {
        return false;
    }

    @Override
    public String overviewLine() {
        return String.format("|- %s -- WARNING: Not on the LEX, download at %s", this.name, this.link);
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public int getId() {
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotLexEntry)) return false;

        NotLexEntry that = (NotLexEntry) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(link != null ? !link.equals(that.link) : that.link != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (link != null ? link.hashCode() : 0);
        return result;
    }
}
