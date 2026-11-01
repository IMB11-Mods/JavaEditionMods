package dev.imb11.snow.snowlogging;

import java.util.List;

public interface SnowloggedChunkData {
    List<SnowloggingStorage.Layers> snow$getLayers();

    void snow$setLayers(List<SnowloggingStorage.Layers> layers);
}
