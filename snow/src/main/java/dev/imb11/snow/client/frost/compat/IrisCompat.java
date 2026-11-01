package dev.imb11.snow.client.frost.compat;

import cc.cassian.mru.Platform;

public final class IrisCompat {
    private static final boolean LOADED = Platform.INSTANCE.isLoaded("iris");

    public static boolean shaderPackInUse() {
        return LOADED && Access.shaderPackInUse();
    }

    private static final class Access {
        private static boolean shaderPackInUse() {
            return net.irisshaders.iris.api.v0.IrisApi.getInstance().isShaderPackInUse();
        }
    }
}
