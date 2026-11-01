package dev.imb11.snow.generation;

import dev.imb11.snow.Snow;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
//? if >=26.3 {
/*import net.minecraft.world.level.levelgen.synth.Noise;
*///?}

public class SnowNoise {
    public static final SnowNoise DEPTH = new SnowNoise("snow_depth", -7, 1.0, 1.0, 0.5);
    public static final SnowNoise DETAIL = new SnowNoise("snow_detail", -4, 1.0, 1.0);
    public static final SnowNoise DRIFT = new SnowNoise("snow_drift", -6, 1.0, 0.5);

    private final String name;
    private final int firstOctave;
    private final DoubleList amplitudes;
    private volatile Sampler sampler;

    private SnowNoise(String name, int firstOctave, double... amplitudes) {
        this.name = Snow.id(name).toString();
        this.firstOctave = firstOctave;
        this.amplitudes = DoubleList.of(amplitudes);
    }

    public float sample(long seed, int x, int z) {
        Sampler current = this.sampler;
        if (current == null || current.seed != seed) {
            current = new Sampler(seed);
            this.sampler = current;
        }

        return current.value(x, z);
    }

    private final class Sampler {
        private final long seed;
        //? if >=26.3 {
        /*private final Noise noise;
        *///?} else {
        private final NormalNoise noise;
        //?}

        private Sampler(long seed) {
            this.seed = seed;
            RandomSource random = new XoroshiroRandomSource(seed).forkPositional().fromHashOf(SnowNoise.this.name);
            //? if >=26.3 {
            /*this.noise = NormalNoise.createParity(SnowNoise.this.firstOctave, SnowNoise.this.amplitudes).create(random);
            *///?} else {
            this.noise = NormalNoise.create(random, new NormalNoise.NoiseParameters(SnowNoise.this.firstOctave, SnowNoise.this.amplitudes));
            //?}
        }

        private float value(int x, int z) {
            //? if >=26.3 {
            /*return this.noise.get(x, 0, z);
            *///?} else {
            return (float) this.noise.getValue(x, 0, z);
            //?}
        }
    }
}
