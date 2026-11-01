package dev.imb11.snow.client.frost;

import cc.cassian.mru.Platform;
import net.minecraft.resources.Identifier;

public final class FrostShader {
    private static final boolean SODIUM = Platform.INSTANCE.isLoaded("sodium");
    //? if >=26.3 {
    /*private static final String IMPORT = "#include ";
    *///?} else {
    private static final String IMPORT = "#moj_import ";
    //?}
    private static final Identifier VANILLA_TERRAIN = Identifier.withDefaultNamespace("core/terrain");
    private static final Identifier SODIUM_TERRAIN = Identifier.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque");
    private static final String VANILLA_GLOBALS_ANCHOR = "int UseRgss;";
    private static final String SODIUM_GLOBALS_ANCHOR = "bool u_UseRGSS;";
    private static volatile boolean vanillaGlobals;
    private static volatile boolean vanillaVertex;
    private static volatile boolean sodiumGlobals;
    private static volatile boolean sodiumVertex;

    public static boolean ready() {
        return SODIUM ? sodiumGlobals && sodiumVertex : vanillaGlobals && vanillaVertex;
    }

    public static String patchVertex(Identifier id, boolean vertex, String source) {
        if (!vertex) {
            return source;
        }

        if (id.equals(VANILLA_TERRAIN)) {
            String anchor = "vertexColor = Color * sample_lightmap(Sampler2, UV2);";
            vanillaVertex = source.contains(anchor);
            return patch(source, "<minecraft:globals.glsl>", anchor, "vertexColor = snow_applyFrost(Color, SnowFrost) * sample_lightmap(Sampler2, UV2);");
        }

        if (id.equals(SODIUM_TERRAIN)) {
            String anchor = "_vert_init();";
            sodiumVertex = source.contains(anchor);
            return patch(source, "<sodium:globals.glsl>", anchor, anchor + "\n    _vert_color = snow_applyFrost(_vert_color, u_SnowFrost);");
        }

        return source;
    }

    public static String patchGlobals(String source) {
        if (source.contains(VANILLA_GLOBALS_ANCHOR)) {
            vanillaGlobals = true;
            return source.replace(VANILLA_GLOBALS_ANCHOR, VANILLA_GLOBALS_ANCHOR + "\n    float SnowFrost;");
        }

        if (source.contains(SODIUM_GLOBALS_ANCHOR)) {
            sodiumGlobals = true;
            return source.replace(SODIUM_GLOBALS_ANCHOR, SODIUM_GLOBALS_ANCHOR + "\n    float u_SnowFrost;");
        }

        return source;
    }

    private static String patch(String source, String globals, String anchor, String replacement) {
        return source.replace(IMPORT + globals, IMPORT + globals + "\n" + IMPORT + "<snow:frost.glsl>").replace(anchor, replacement);
    }
}
