package net.caspervg.llm.executor;

import net.caspervg.llm.LlmCommand;

public interface CommandExecutor {
    int execute(LlmCommand command);
}
