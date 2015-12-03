package net.caspervg.llm.bean;

public class LexNotTrackableEntry extends LexEntry {

    public LexNotTrackableEntry(int id, String name) {
        super(id, name);
    }

    public LexNotTrackableEntry(int id, String name, String author) {
        super(id, name, author);
    }

    @Override
    public String overviewLine() {
        if (this.getAuthor() != null) {
            return String.format("|- <%s> %s by %s -- WARNING: does not support the LEX Dependency Tracker!",
                    this.getId(), this.getName(), this.getAuthor());
        } else {
            return String.format("|- <%s> %s -- WARNING: does not support the LEX Dependency Tracker!", this.getId(), this.getName());
        }
    }

    @Override
    public int priority() {
        return 1;
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
