package dev.imb11.snow.client.frost;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class FrostShader {
    public static final int WEIGHT_SHIFT = 3;
    public static final int WEIGHT_MAX = 31;
    private static final String NAMESPACE = "sodium";
    private static final String TERRAIN = "blocks/block_layer_opaque";
    private static final String GLOBALS = "globals.glsl";
    private static final String GLOBALS_ANCHOR = "bool u_UseRGSS;";
    private static final String GLOBALS_MEMBER = "\n    float u_SnowFrost;";
    private static final String VERTEX_ANCHOR = "v_Color = _vert_color * texture(u_LightTex, _vert_tex_light_coord);";
    private static final String VERTEX_CODE = """
            uint snowFrostWeight = (_material_params >> %du) & %du;
                if (snowFrostWeight != 0u) {
                    float snowFrost = u_SnowFrost * float(snowFrostWeight) / %d.0;
                    _vert_color = vec4(mix(_vert_color.rgb, vec3(_vert_color.a), snowFrost), 1.0);
                }
                """.formatted(WEIGHT_SHIFT, WEIGHT_MAX, WEIGHT_MAX);
    private static volatile boolean globalsPatched;
    private static volatile boolean vertexPatched;

    private FrostShader() {
    }

    public static boolean ready() {
        return globalsPatched && vertexPatched;
    }

    public static int encodeWeight(float weight) {
        return Mth.clamp(Math.round(weight * WEIGHT_MAX), 1, WEIGHT_MAX) << WEIGHT_SHIFT;
    }

    public static String patchShader(Identifier id, boolean vertex, String source) {
        if (!id.getNamespace().equals(NAMESPACE)) {
            return source;
        }

        String patched = patchGlobals(source);
        if (vertex && id.getPath().equals(TERRAIN)) {
            //? if <26.3 {
            globalsPatched = source.contains(GLOBALS_ANCHOR);
            //?}
            vertexPatched = patched.contains(VERTEX_ANCHOR);
            patched = patched.replace(VERTEX_ANCHOR, VERTEX_CODE + VERTEX_ANCHOR);
        }

        return patched;
    }

    public static String patchInclude(Identifier id, String source) {
        if (!id.getNamespace().equals(NAMESPACE) || !id.getPath().endsWith(GLOBALS)) {
            return source;
        }

        globalsPatched = source.contains(GLOBALS_ANCHOR);
        return patchGlobals(source);
    }

    private static String patchGlobals(String source) {
        return source.replace(GLOBALS_ANCHOR, GLOBALS_ANCHOR + GLOBALS_MEMBER);
    }
}
