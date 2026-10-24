package dev.imb11.snow.profile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.imb11.snow.util.SnowColumn;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
//? if >=26.3 {
/*import net.minecraft.core.registries.codec.RegistryCodecs;
*///?} else {
import net.minecraft.core.RegistryCodecs;
//?}
import net.minecraft.world.level.biome.Biome;

public record SnowProfile(HolderSet<Biome> biomes, int minLayers, int maxLayers, float accumulationRate, int priority) {
    public static final Codec<SnowProfile> CODEC = RecordCodecBuilder.<SnowProfile>create(instance -> instance.group(
            //? if >=26.3 {
            /*RegistryCodecs.holderSet(Registries.BIOME).fieldOf("biomes").forGetter(SnowProfile::biomes),
            *///?} else {
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(SnowProfile::biomes),
            //?}
            Codec.intRange(1, SnowColumn.MAX_LAYERS).fieldOf("min_layers").forGetter(SnowProfile::minLayers),
            Codec.intRange(1, SnowColumn.MAX_LAYERS).fieldOf("max_layers").forGetter(SnowProfile::maxLayers),
            Codec.floatRange(0, 8).optionalFieldOf("accumulation_rate", 1f).forGetter(SnowProfile::accumulationRate),
            Codec.INT.optionalFieldOf("priority", 0).forGetter(SnowProfile::priority)
    ).apply(instance, SnowProfile::new)).validate(profile -> profile.minLayers > profile.maxLayers
            ? DataResult.error(() -> "min_layers must not exceed max_layers")
            : DataResult.success(profile));
}
