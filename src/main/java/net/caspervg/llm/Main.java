package net.caspervg.llm;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.moandjiezana.toml.Toml;
import net.caspervg.llm.config.LlmConfig;
import net.caspervg.llm.executor.CommandExecutor;
import net.caspervg.llm.executor.DownloadCommandExecutor;
import net.caspervg.llm.executor.FindCommandExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String LLM_VERSION = "0.0.1";
    private static final Map<String, CommandExecutor> EXECUTOR_MAP;
    static {
        Map<String, CommandExecutor> temp = new HashMap<>();
        temp.put("find", new FindCommandExecutor());
        temp.put("download", new DownloadCommandExecutor());
        EXECUTOR_MAP = Collections.unmodifiableMap(temp);
    }

    public static void main(String[] args) throws Exception {
        CommandFind find = new CommandFind();
        CommandDownload dl = new CommandDownload();

        LlmCommand llm = new LlmCommand(dl, find);
        JCommander jc = new JCommander(llm);

        jc.addCommand("find", find);
        jc.addCommand("download", dl);

        jc.parse(args);

        Path configPath = configLocation(llm);
        LlmConfig llmConfig = parseConfig(configPath);
        if (! llmConfig.getVersion().equals(LLM_VERSION)) {
            LlmLog.LOGGER.severe("LLM Config version does not equal the version of the application. Aborting.");
            return;
        }

        llm.setConfigPath(configPath.toString());
        if (StringUtils.isBlank(llm.getUsername())) {
            if (StringUtils.isBlank(llmConfig.getUsername())) {
                LlmLog.LOGGER.warning("LLM Config username is blank. Some features (like downloading) may not work.");
            } else {
                llm.setUsername(llmConfig.getUsername());
            }
        }

        if (StringUtils.isBlank(llm.getPassword())) {
            if (StringUtils.isBlank(llmConfig.getPassword())) {
                LlmLog.LOGGER.warning("LLM Config password is blank. Some features (like downloading) may not work.");
            } else {
                llm.setPassword(llmConfig.getPassword());
            }
        }

        EXECUTOR_MAP.get(jc.getParsedCommand()).execute(llm);
    }

    private static LlmConfig parseConfig(Path configLocation) {
        return new Toml().parse(configLocation.toFile()).to(LlmConfig.class);
    }

    private static Path configLocation(LlmCommand command) throws IOException {
        Path configPath;
        if (command.getConfigPath() != null) {
            configPath = Paths.get(command.getConfigPath());
        } else {
            configPath = Paths.get(System.getProperty("user.home"), ".llm", "config.toml");
        }

        File configDir = configPath.getParent().toFile();
        File configFile = configPath.toFile();
        if (! configDir.exists()) {
            FileUtils.forceMkdir(configDir);
        }
        if (! configFile.exists()) {
            FileUtils.touch(configFile);
            FileUtils.copyInputStreamToFile(Main.class.getResourceAsStream("/default_config.toml"), configFile);
        }

        return configPath;
    }

    @Parameters(separators = "=", commandDescription = "Download a LEX file by id or name")
    public static class CommandDownload {
        @Parameter(description = "List of names/ids to download")
        private List<String> names;

        @Parameter(names = "--no-dependencies")
        private Boolean noDependencies = false;

        public List<String> getNames() {
            return names;
        }

        public Boolean noDependencies() {
            return noDependencies;
        }
    }

    @Parameters(separators = "=", commandDescription = "Find a LEX file by name")
    public static class CommandFind {
        @Parameter(description = "Partial names of the files to search")
        private List<String> names;

        public List<String> getNames() {
            return names;
        }
    }
}


