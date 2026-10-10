package dev.imb11.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

final class GlassBlockShapes {
    static final VoxelShape[] PROJECTOR = projector();
    static final VoxelShape[] TERMINAL = terminal();

    private GlassBlockShapes() {
    }

    private static VoxelShape[] projector() {
        VoxelShape shape = Shapes.or(
                Block.box(0, 0, 0, 16, 1, 16),
                Block.box(0, 0, 0, 1, 9, 16),
                Block.box(15, 0, 0, 16, 9, 16),
                Block.box(0, 0, 0, 16, 9, 1),
                Block.box(0, 0, 15, 16, 9, 16),
                Block.box(4, 0, 4, 12, 8, 12),
                Block.box(5, 8, 5, 11, 16, 11));
        VoxelShape[] shapes = new VoxelShape[6];
        shapes[Direction.UP.ordinal()] = shape;
        shapes[Direction.DOWN.ordinal()] = rotate(shape, 2, 0);
        shapes[Direction.SOUTH.ordinal()] = rotate(shape, 1, 0);
        shapes[Direction.WEST.ordinal()] = rotate(shape, 1, 1);
        shapes[Direction.NORTH.ordinal()] = rotate(shape, 1, 2);
        shapes[Direction.EAST.ordinal()] = rotate(shape, 1, 3);
        return shapes;
    }

    private static VoxelShape[] terminal() {
        double eyeRadius = 3.0D * Math.sqrt(3.0D);
        double eyeZ = 6.5D + Math.sqrt(2.0D);
        VoxelShape shape = Shapes.or(
                Block.box(0, 0, 0, 4, 4, 4),
                Block.box(0, 0, 12, 4, 4, 16),
                Block.box(12, 0, 12, 16, 4, 16),
                Block.box(12, 4, 12, 15, 12, 15),
                Block.box(1, 4, 12, 4, 12, 15),
                Block.box(1, 4, 1, 4, 12, 4),
                Block.box(12, 4, 1, 15, 12, 4),
                Block.box(4, 1, 1, 12, 4, 4),
                Block.box(12, 0, 0, 16, 4, 4),
                Block.box(4, 12, 1, 12, 15, 4),
                Block.box(4, 12, 12, 12, 15, 15),
                Block.box(1, 12, 4, 4, 15, 12),
                Block.box(12, 12, 4, 15, 15, 12),
                Block.box(12, 1, 4, 15, 4, 12),
                Block.box(1, 1, 4, 4, 4, 12),
                Block.box(4, 1, 12, 12, 4, 15),
                Block.box(0, 12, 12, 4, 16, 16),
                Block.box(0, 12, 0, 4, 16, 4),
                Block.box(12, 12, 12, 16, 16, 16),
                Block.box(12, 12, 0, 16, 16, 4),
                Block.box(8 - eyeRadius, 8 - eyeRadius, eyeZ - eyeRadius, 8 + eyeRadius, 8 + eyeRadius, eyeZ + eyeRadius));
        VoxelShape[] shapes = new VoxelShape[6];
        shapes[Direction.SOUTH.ordinal()] = shape;
        shapes[Direction.NORTH.ordinal()] = rotate(shape, 0, 2);
        shapes[Direction.WEST.ordinal()] = rotate(shape, 0, 1);
        shapes[Direction.EAST.ordinal()] = rotate(shape, 0, 3);
        shapes[Direction.UP.ordinal()] = rotate(shape, 1, 0);
        shapes[Direction.DOWN.ordinal()] = rotate(shape, 3, 2);
        return shapes;
    }

    private static VoxelShape rotate(VoxelShape shape, int xTurns, int yTurns) {
        for (int turn = 0; turn < xTurns; turn++) {
            VoxelShape[] rotated = {Shapes.empty()};
            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    rotated[0] = Shapes.or(rotated[0], Shapes.box(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY)));
            shape = rotated[0];
        }
        for (int turn = 0; turn < yTurns; turn++) {
            VoxelShape[] rotated = {Shapes.empty()};
            shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    rotated[0] = Shapes.or(rotated[0], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            shape = rotated[0];
        }
        return shape.optimize();
    }
}
