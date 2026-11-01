package dev.imb11.snow.client.frost.compat;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class SodiumFrostShader {
    public static final int WEIGHT_MAX = 31;
    private static final String NAMESPACE = "sodium";
    private static final String GLOBALS_ANCHOR = "bool u_UseRGSS;";
    private static final String IMPORT_ANCHOR = "#moj_import <sodium:globals.glsl>";
    private static final String VERTEX_ANCHOR = "v_Color = _vert_color * texture(u_LightTex, _vert_tex_light_coord);";
    private static volatile boolean globalsPatched;
    private static volatile boolean vertexPatched;

    public static boolean ready() {
        return globalsPatched && vertexPatched;
    }

    public static int encodeWeight(float weight) {
        return Mth.clamp(Math.round(weight * WEIGHT_MAX), 1, WEIGHT_MAX) << 3;
    }

    public static String patchVertex(Identifier id, boolean vertex, String source) {
        if (!vertex || !id.getNamespace().equals(NAMESPACE) || !id.getPath().equals("blocks/block_layer_opaque")) {
            return source;
        }

        // lord help those who try to understand why i had to patch sodium like this
        // im sorry.
        vertexPatched = source.contains(IMPORT_ANCHOR) && source.contains(VERTEX_ANCHOR);
        return source.replace(IMPORT_ANCHOR, IMPORT_ANCHOR + "\n#moj_import <snow:frost.glsl>").replace(VERTEX_ANCHOR, "_vert_color = snow_applyFrost(_vert_color, _material_params);\n    " + VERTEX_ANCHOR);
    }

    public static String patchGlobals(Identifier id, String source) {
        if (!id.getNamespace().equals(NAMESPACE) || !source.contains(GLOBALS_ANCHOR)) {
            return source;
        }

        globalsPatched = true;
        return source.replace(GLOBALS_ANCHOR, GLOBALS_ANCHOR + "\n    float u_SnowFrost;");
    }
}
