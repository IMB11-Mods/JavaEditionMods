package dev.imb11.snow.mixin.snowlogging;

import com.llamalad7.mixinextras.sugar.Local;
import dev.imb11.snow.snowlogging.SnowloggedChunkData;
import dev.imb11.snow.snowlogging.SnowloggingStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SerializableChunkData.class)
public class SerializableChunkDataMixin implements SnowloggedChunkData {
    @Unique
    private List<SnowloggingStorage.Layers> snow$layers = List.of();

    @Override
    public List<SnowloggingStorage.Layers> snow$getLayers() {
        return this.snow$layers;
    }

    @Override
    public void snow$setLayers(List<SnowloggingStorage.Layers> layers) {
        this.snow$layers = layers;
    }

    @Inject(method = "copyOf", at = @At("RETURN"))
    private static void snow$storePlants(CallbackInfoReturnable<SerializableChunkData> cir, @Local(argsOnly = true) ChunkAccess chunk) {
        SerializableChunkData data = cir.getReturnValue();
        ((SnowloggedChunkData) (Object) data).snow$setLayers(SnowloggingStorage.store(chunk, data));
    }

    @Inject(method = "write", at = @At("RETURN"))
    private void snow$writeLayers(CallbackInfoReturnable<CompoundTag> cir) {
        SnowloggingStorage.write(cir.getReturnValue(), this.snow$layers);
    }

    @Inject(method = "parse", at = @At("RETURN"))
    private static void snow$readLayers(CallbackInfoReturnable<SerializableChunkData> cir, @Local(argsOnly = true) CompoundTag tag) {
        SerializableChunkData data = cir.getReturnValue();
        if (data != null) {
            ((SnowloggedChunkData) (Object) data).snow$setLayers(SnowloggingStorage.read(tag));
        }
    }

    @Inject(method = "read", at = @At("HEAD"))
    private void snow$restorePlants(CallbackInfoReturnable<ProtoChunk> cir) {
        SnowloggingStorage.restore((SerializableChunkData) (Object) this, this.snow$layers);
    }
}
