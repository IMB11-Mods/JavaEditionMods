package dev.imb11.mixins;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Set;

@Mixin(LevelRenderer.class)
public interface LevelRendererBufferAccessor {
    @Accessor("viewArea")
    ViewArea glass$getViewArea();

    @Accessor("viewArea")
    void glass$setViewArea(ViewArea viewArea);

    @Accessor("level")
    void glass$setLevel(ClientLevel level);

    @Accessor("visibleSections")
    ObjectArrayList<SectionRenderDispatcher.RenderSection> glass$getVisibleSections();

    @Accessor("sectionRenderDispatcher")
    void glass$setSectionRenderDispatcher(SectionRenderDispatcher dispatcher);
}
