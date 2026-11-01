//? fabric {
package dev.imb11.snow.snowlogging;

import dev.imb11.snow.Snow;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.level.chunk.ChunkAccess;

public class SnowloggingAttachment {
    public static final AttachmentType<SnowloggedPlants> TYPE = AttachmentRegistry.create(Snow.id("snowlogged_plants"), builder -> builder
            .persistent(SnowloggedPlants.CODEC)
            .syncWith(SnowloggedPlants.STREAM_CODEC, AttachmentSyncPredicate.all()));

    public static void init() {
    }

    public static SnowloggedPlants get(ChunkAccess chunk) {
        return chunk.getAttachedOrElse(TYPE, SnowloggedPlants.EMPTY);
    }

    public static void set(ChunkAccess chunk, SnowloggedPlants plants) {
        chunk.setAttached(TYPE, plants);
    }
}
//?} else {
/*package dev.imb11.snow.snowlogging;

import dev.imb11.snow.Snow;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class SnowloggingAttachment {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Snow.MOD_ID);
    public static final Supplier<AttachmentType<SnowloggedPlants>> TYPE = ATTACHMENT_TYPES.register("snowlogged_plants", () -> AttachmentType.builder(() -> SnowloggedPlants.EMPTY)
            .serialize(SnowloggedPlants.MAP_CODEC, plants -> !plants.isEmpty())
            .sync(new SyncHandler())
            .build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }

    public static SnowloggedPlants get(ChunkAccess chunk) {
        SnowloggedPlants plants = chunk.getExistingDataOrNull(TYPE);
        return plants == null ? SnowloggedPlants.EMPTY : plants;
    }

    public static void set(ChunkAccess chunk, SnowloggedPlants plants) {
        chunk.setData(TYPE, plants);
        if (chunk instanceof LevelChunk levelChunk && !levelChunk.getLevel().isClientSide()) {
            levelChunk.syncData(TYPE);
        }
    }

    private static class SyncHandler implements AttachmentSyncHandler<SnowloggedPlants> {
        @Override
        public void write(RegistryFriendlyByteBuf buf, SnowloggedPlants attachment, boolean initialSync) {
            SnowloggedPlants.STREAM_CODEC.encode(buf, attachment);
        }

        @Override
        public SnowloggedPlants read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable SnowloggedPlants previousValue) {
            SnowloggedPlants plants = SnowloggedPlants.STREAM_CODEC.decode(buf);
            if (holder instanceof LevelChunk chunk) {
                Snowlogging.listener().plantsChanged(chunk.getLevel(), previousValue, plants);
            }

            return plants;
        }
    }
}
*///?}
