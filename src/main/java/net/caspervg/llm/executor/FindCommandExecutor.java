package net.caspervg.llm.executor;

import net.caspervg.lex4j.bean.Lot;
import net.caspervg.lex4j.route.ExtraLotInfo;
import net.caspervg.lex4j.route.Filter;
import net.caspervg.lex4j.route.SearchRoute;
import net.caspervg.llm.LlmCommand;
import net.caspervg.llm.LlmUtils;

import java.util.List;

public class FindCommandExecutor implements CommandExecutor {

    @Override
    public int execute(LlmCommand command) {
        SearchRoute searchRoute = new SearchRoute(command.getAuth());

        List<String> names = command.getFindCommand().getNames();

        for (String name : names) {
            searchRoute.addFilter(Filter.TITLE, name);
            List<Lot> results = searchRoute.doSearch(new ExtraLotInfo.AllExtraInfo());
            listResults(name, results);
        }

        return 0;
    }

    private void listResults(String name, List<Lot> results) {
        System.out.println(String.format("Results for query '%s': ", name));
        for (Lot lot : results) {
            System.out.println(
                String.format(
                    "<%s> %s by %s (last downloaded: %s)",
                    lot.getId(),
                    lot.getName(),
                    lot.getAuthor(),
                    LlmUtils.orElse(lot.getLastDownloaded(), "never")
                )
            );
        }
    }
}
