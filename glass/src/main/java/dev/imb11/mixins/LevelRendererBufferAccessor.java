package dev.imb11.mixins;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelRenderer.class)
public interface LevelRendererBufferAccessor {
    @Accessor("viewArea")
    void glass$setViewArea(ViewArea viewArea);

    @Accessor("sectionRenderDispatcher")
    void glass$setSectionRenderDispatcher(SectionRenderDispatcher dispatcher);
}
