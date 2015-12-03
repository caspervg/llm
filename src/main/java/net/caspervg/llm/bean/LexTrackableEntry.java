package net.caspervg.llm.bean;

public class LexTrackableEntry extends LexEntry {

    public LexTrackableEntry(int id, String name) {
        super(id, name);
    }

    public LexTrackableEntry(int id, String name, String author) {
        super(id, name, author);
    }

    @Override
    public String overviewLine() {
        if (this.getAuthor() != null) {
            return String.format("|- <%s> %s by %s", this.getId(), this.getName(), this.getAuthor());
        } else {
            return String.format("|- <%s> %s", this.getId(), this.getName());
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
