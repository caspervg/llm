package net.caspervg.llm;

import com.beust.jcommander.Parameter;
import net.caspervg.lex4j.auth.Auth;

public class LlmCommand {
    @Parameter(names="-v")
    private Boolean verbose = false;

    @Parameter(names={"-u", "--username"})
    private String username;

    @Parameter(names={"-p", "--password"})
    private String password;

    @Parameter(names={"-c", "--config"})
    private String configPath;

    private Main.CommandDownload downloadCommand;
    private Main.CommandFind findCommand;

    public LlmCommand(Main.CommandDownload dl, Main.CommandFind find) {
        this.downloadCommand = dl;
        this.findCommand = find;
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfigPath() {
        return configPath;
    }

    public Main.CommandDownload getDownloadCommand() {
        return downloadCommand;
    }

    public Main.CommandFind getFindCommand() {
        return findCommand;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public Auth getAuth() {
        return new Auth(this.username, this.password);
    }
}
