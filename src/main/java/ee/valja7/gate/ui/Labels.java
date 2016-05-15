package ee.valja7.gate.ui;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newTreeSet;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Labels {

    private static final Logger LOG = Logger.getLogger(Labels.class);

    private static final Gson GSON = new Gson();
    private static WatchService watchService;
    private static URL labelsYaml;
    private static ThreadLocal<String> language = new ThreadLocal<>();
    private static ThreadLocal<String> context = new ThreadLocal<>();
    private static Map<String, Map<String, String>> labels;
    private static Set<String> allowedLanguages = newTreeSet();

    static void load() throws IOException {
        InputStream stream = labelsYaml.openStream();
        setLabels(stream);
    }

    static void setLabels(InputStream yamlStream) throws IOException {
        //noinspection unchecked
        labels = (Map<String, Map<String, String>>) new Yaml().load(yamlStream);
        yamlStream.close();
        LOG.info("Loaded " + labels.size() + " labels");
        for (Map<String, String> label : labels.values()) {
            for (String language : label.keySet()) allowedLanguages.add(language);
        }
        LOG.info("Available languages: " + allowedLanguages);
    }

    public static String get(String key) {
        reloadIfChanged();
        String text = null;
        if (context.get() != null)
            text = _get(context.get() + '.' + key);
        if (text == null)
            text = _get(key);
        if (text == null) LOG.warn("Missing label: " + key);
        return text == null ? key : text;
    }

    private static String _get(String key) {
        Map<String, String> label = labels.get(key);
        return label != null ? label.get(getLanguage()) : null;
    }

    public static Set<String> getAllowedLanguages() {
        return allowedLanguages;
    }

    public static String getLanguage() {
        String retval = language.get();
        return retval;
    }

    public static void setLanguage(String language) {
        Labels.language.set(language);
    }

    public static String getLanguage3() {
        return ImmutableMap.of("et", "EST", "en", "ENG", "ru", "RUS").get(getLanguage());
    }

    public static void setContext(String context) {
        Labels.context.set(context);
    }

    public static String toJSON() {
        String language = getLanguage();
        Map<String, String> result = newHashMap();
        String prefix = context.get() + ".";
        for (String key : labels.keySet()) {
            if (!key.startsWith(prefix) && (key.startsWith("admin.") || key.startsWith("provider.") || key.startsWith("menu.")))
                continue;
            String k = key.startsWith(prefix) ? key.substring(prefix.length()) : key;
            if (!key.startsWith(prefix) && result.containsKey(k)) continue;
            result.put(k, labels.get(key).get(language));
        }
        return GSON.toJson(result);
    }

    static void reloadIfChanged() {
        try {
            WatchKey key = watchService.poll();
            if (key == null) return;
            for (WatchEvent<?> event : key.pollEvents()) {
                if (!event.context().toString().equals("labels.yaml")) continue;
                load();
                break;
            }
            key.reset();
        } catch (Exception e) {
            LOG.error("Failed to reload labels: " + e);
        }
    }

    public static String nonBreaking(String string) {
        return string == null ? null : string.replace(' ', '\u00A0').replace('-', '\u2011');
    }

    public static void init(URL labelsYaml) throws IOException, URISyntaxException {
        Labels.labelsYaml = labelsYaml;
        load();
        Path path = Paths.get(Labels.labelsYaml.toURI()).getParent();
        watchService = path.getFileSystem().newWatchService();
        path.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);
        LOG.info("Labels Loaded");
    }
}