package net.caspervg.llm.executor;

import net.caspervg.lex4j.bean.Dependency;
import net.caspervg.lex4j.bean.DependencyList;
import net.caspervg.lex4j.bean.Lot;
import net.caspervg.lex4j.route.ExtraLotInfo;
import net.caspervg.lex4j.route.Filter;
import net.caspervg.lex4j.route.LotRoute;
import net.caspervg.lex4j.route.SearchRoute;
import net.caspervg.llm.LlmCommand;
import net.caspervg.llm.LlmLog;
import net.caspervg.llm.LlmUtils;
import net.caspervg.llm.Main;
import net.caspervg.llm.bean.DownloadEntry;
import net.caspervg.llm.bean.LexNotTrackableEntry;
import net.caspervg.llm.bean.LexTrackableEntry;
import net.caspervg.llm.bean.NotLexEntry;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadCommandExecutor implements CommandExecutor {
    @Override
    public int execute(LlmCommand command) {
        Main.CommandDownload downloadCommand = command.getDownloadCommand();
        List<String> names = downloadCommand.getNames();
        LotRoute lotRoute = new LotRoute(command.getAuth());
        Set<DownloadEntry> totalEntries = new HashSet<>();

        for (String name : names) {
            if (NumberUtils.isDigits(name)) {
                int id = Integer.parseUnsignedInt(name);
                Lot lot = lotRoute.getLot(id, new ExtraLotInfo.AllExtraInfo());

                totalEntries.addAll(buildEntryList(lot));
            } else {
                SearchRoute searchRoute = new SearchRoute(command.getAuth());
                searchRoute.addFilter(Filter.TITLE, name);
                List<Lot> results = searchRoute.doSearch(new ExtraLotInfo.NoExtraInfo());

                if (results.size() < 1) {
                    System.out.println(String.format("Found no results for your query '%s'", name));
                } else if (results.size() > 1) {
                    System.out.println(String.format("Found multiple results for your query '%s'", name));
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
                } else {
                    Lot result = lotRoute.getLot(results.get(0).getId());
                    totalEntries.addAll(buildEntryList(result));
                }
            }
        }

        // We now have the total collection of files (and dependencies to download)
        System.out.println("/---------------------");
        System.out.println("/- Files to Download -");
        List<DownloadEntry> entryList = new ArrayList<>(totalEntries);
        entryList.sort((o1, o2) -> Integer.compare(o1.priority(), o2.priority()));
        entryList.forEach(entry -> System.out.println(entry.overviewLine()));
        System.out.println("\\---------------------");
        System.out.println(String.format("Are you sure you want to download %s LEX files? [y/N]", entryList.size()));
        Scanner inputScanner = new Scanner(System.in);
        String resp = inputScanner.nextLine();
        inputScanner.close();
        if (StringUtils.equalsIgnoreCase(resp, "y")) {
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            for (DownloadEntry entry : entryList) {
                if (entry.canDownload()) {
                    executorService.execute(() -> {
                        System.out.println(String.format("Downloading file with id %s", entry.getId()));
                        lotRoute.getLotDownload(entry.getId(), new File("/tmp"));
                    });
                }
            }
        }
        System.out.println("Done downloading. Enjoy the files!");
        return 1;
    }

    private List<DownloadEntry> buildEntryList(Lot lot) {
        List<DownloadEntry> ret = new ArrayList<>();
        Set<Dependency> dependencies = findDependencies(lot.getDependencyList());
        for (Dependency dep : dependencies) {
            if (dep.getId() > 0 && dep.isInternal()) {
                if (dep.getStatus().isDeleted()) {
                    LlmLog.LOGGER.severe("Dependency <%s> %s has been deleted! It is not available for download.");
                } else if (dep.getStatus().isLocked()) {
                    LlmLog.LOGGER.severe("Dependency <%s> %s has been locked! It is not available for download.");
                } else if (dep.getStatus().isSuperseded()) {
                    LlmLog.LOGGER.warning("Dependency <%s> %s has been superseded by <%s>. Selecting it's successor instead.");
                    ret.add(new LexNotTrackableEntry(dep.getStatus().getSupersededBy(), "supersedes " + dep.getName()));
                } else if (! dep.getStatus().isOk()) {
                    ret.add(new LexNotTrackableEntry(dep.getId(), dep.getName()));
                } else {
                    // Hopefully it's a perfectly fine dependency now
                    ret.add(new LexTrackableEntry(dep.getId(), dep.getName()));
                }
            } else {
                ret.add(new NotLexEntry(dep.getName(), dep.getLink()));
            }
        }

        ret.add(new LexTrackableEntry(lot.getId(), lot.getName(), lot.getAuthor()));
        return ret;
    }

    private Set<Dependency> findDependencies(DependencyList list) {
        Set<Dependency> dependencySet = new HashSet<>();
        List<Dependency> dependencyList = list.asList(false);

        dependencySet.addAll(dependencyList);
        dependencyList.stream().filter(Dependency::isInternal).forEach(dependency -> dependencySet.addAll(dependency.getDependencies().asSet()));

        return dependencySet;
    }
}
