package cc.cassian.mru;

//? fabric {
import cc.cassian.mru.fabric.FabricPlatformImpl;
//?}
import java.nio.file.Path;
import java.util.ArrayList;
//? neoforge {
/*import cc.cassian.mru.neoforge.NeoforgePlatformImpl;
*///?}

public interface Platform {

    //? fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? neoforge {
    /*Platform INSTANCE = new NeoforgePlatformImpl();
    *///?}

	default boolean isModLoaded(String modid) {
		return isLoaded(modid);
	}

	boolean isLoaded(String modid);

    boolean isLoadingLoaded(String mod);

    String loader();

    Path configPath();

	default Path getConfigDir() {
		return configPath();
	}

	ArrayList<String> getMods();

	boolean isDeveloperEnvironment();

	default boolean isDevEnvironment() {
		return isDeveloperEnvironment();
	}
}
