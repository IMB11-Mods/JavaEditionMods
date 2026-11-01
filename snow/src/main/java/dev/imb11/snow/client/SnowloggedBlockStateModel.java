//? fabric {
package dev.imb11.snow.client;

import net.fabricmc.fabric.api.client.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class SnowloggedBlockStateModel implements BlockStateModel {
    private final BlockStateModel snow;

    public SnowloggedBlockStateModel(BlockStateModel snow) {
        this.snow = snow;
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
        this.snow.collectParts(random, output);
    }

    @Override
    public Material.Baked particleMaterial() {
        return this.snow.particleMaterial();
    }

    @Override
    public int materialFlags() {
        return this.snow.materialFlags();
    }

    @Override
    public void emitQuads(QuadEmitter emitter, BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, Predicate<@Nullable Direction> cullTest) {
        this.snow.emitQuads(emitter, level, pos, state, random, cullTest);
        BlockState plant = plantAt(level, pos);
        if (plant == null) {
            return;
        }

        Vec3 offset = plant.getOffset(pos);
        emitter.pushTransform(quad -> {
            for (int i = 0; i < 4; i++) {
                quad.pos(i, quad.x(i) + (float) offset.x, quad.y(i) + (float) offset.y, quad.z(i) + (float) offset.z);
            }

            return true;
        });
        model(plant).emitQuads(emitter, level, pos, plant, random, cullTest);
        emitter.popTransform();
    }

    @Override
    public @Nullable Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        return plantAt(level, pos) == null ? this.snow.createGeometryKey(level, pos, state, random) : null;
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
        BlockState plant = plantAt(level, pos);
        int flags = this.snow.materialFlags(level, pos, state, random);
        return plant == null ? flags : flags | model(plant).materialFlags(level, pos, plant, random);
    }

    private static @Nullable BlockState plantAt(BlockAndTintGetter level, BlockPos pos) {
        return level == BlockAndTintGetter.EMPTY ? null : SnowloggingClient.INSTANCE.plantAt(pos);
    }

    private static BlockStateModel model(BlockState plant) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(plant);
    }
}
//?} else {
/*package dev.imb11.snow.client;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.DelegateBlockStateModel;
import net.neoforged.neoforge.client.model.quad.QuadTransforms;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SnowloggedBlockStateModel extends DelegateBlockStateModel {
    public SnowloggedBlockStateModel(BlockStateModel snow) {
        super(snow);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        super.collectParts(level, pos, state, random, parts);
        BlockState plant = plantAt(level, pos);
        if (plant == null) {
            return;
        }

        Vec3 offset = plant.getOffset(pos);
        Transformation transformation = new Transformation(new Matrix4f().translation((float) offset.x, (float) offset.y, (float) offset.z));
        List<BlockStateModelPart> plantParts = new ArrayList<>();
        model(plant).collectParts(level, pos, plant, random, plantParts);
        plantParts.forEach(part -> parts.add(new OffsetPart(part, transformation)));
    }

    @Override
    public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
        BlockState plant = plantAt(level, pos);
        int flags = super.materialFlags(level, pos, state);
        return plant == null ? flags : flags | model(plant).materialFlags(level, pos, plant);
    }

    private static @Nullable BlockState plantAt(BlockAndTintGetter level, BlockPos pos) {
        return level == BlockAndTintGetter.EMPTY ? null : SnowloggingClient.INSTANCE.plantAt(pos);
    }

    private static BlockStateModel model(BlockState plant) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(plant);
    }

    private record OffsetPart(BlockStateModelPart part, Transformation transformation) implements BlockStateModelPart {
        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            return this.part.getQuads(direction).stream().map(quad -> QuadTransforms.applyTransformation(quad, this.transformation)).toList();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.part.useAmbientOcclusion();
        }

        @Override
        public TriState ambientOcclusion() {
            return this.part.ambientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.part.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return this.part.materialFlags();
        }
    }
}
*///?}
