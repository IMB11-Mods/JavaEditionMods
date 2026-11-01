//? fabric {
package dev.imb11.snow.datagen;

import com.google.common.hash.Hashing;
import dev.imb11.snow.gametest.support.SnowTestHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SnowTestStructureProvider implements DataProvider {
    private final PackOutput.PathProvider paths;

    public SnowTestStructureProvider(FabricPackOutput output) {
        this.paths = output.createPathProvider(PackOutput.Target.DATA_PACK, "structure");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        try {
            write(output, SnowTestHelper.PLATFORM, 9, 5, 9);
            write(output, SnowTestHelper.CHUNK_PLATFORM, 16, 3, 16);
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private void write(CachedOutput output, Identifier id, int width, int height, int depth) throws IOException {
        ListTag palette = new ListTag();
        palette.add(NbtUtils.writeBlockState(Blocks.STONE.defaultBlockState()));

        ListTag blocks = new ListTag();
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                CompoundTag block = new CompoundTag();
                block.put("pos", ints(x, 0, z));
                block.putInt("state", 0);
                blocks.add(block);
            }
        }

        CompoundTag structure = new CompoundTag();
        structure.put("size", ints(width, height, depth));
        structure.put("palette", palette);
        structure.put("blocks", blocks);
        structure.put("entities", new ListTag());
        NbtUtils.addCurrentDataVersion(structure);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        NbtIo.writeCompressed(structure, bytes);
        byte[] data = bytes.toByteArray();
        output.writeIfNeeded(this.paths.file(id, "nbt"), data, Hashing.sha1().hashBytes(data));
    }

    private static ListTag ints(int... values) {
        ListTag list = new ListTag();
        for (int value : values) {
            list.add(IntTag.valueOf(value));
        }

        return list;
    }

    @Override
    public String getName() {
        return "Snow Test Structures";
    }
}
//?}
