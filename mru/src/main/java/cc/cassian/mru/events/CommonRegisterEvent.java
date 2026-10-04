package cc.cassian.mru.events;

/**
 * Fired in Fabric's <code>ModInitializer</code> and NeoForge's <code>RegisterEvent</code> to allow Fabric-style static initialization in common code.
 */
public interface CommonRegisterEvent {
	void onInitialize();
}
