package net.caspervg.llm.bean;

public abstract class DownloadEntry {
    public abstract boolean canDownload();
    public abstract String overviewLine();
    public abstract int priority();
    public abstract int getId();
}
