package eternal.walnut.reimu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eternal.walnut.reimu.config.ReimuConfig;

import java.io.File;

public class GlobalVars {
    public static final Gson BUILDER_GSON = new GsonBuilder().setPrettyPrinting().create();
    public static ReimuConfig reimuConfig = new ReimuConfig();

    public static void read() {
        try {
            reimuConfig = BUILDER_GSON.fromJson(FileUtils.read(new File("config.cfg")), ReimuConfig.class);
            if (reimuConfig == null)
                reimuConfig = new ReimuConfig();
        } catch (Throwable ignored) {
            reimuConfig = new ReimuConfig();
        }
    }

    public static void save() {
        FileUtils.save(new File("config.cfg"), BUILDER_GSON.toJson(reimuConfig), false);
    }
}
