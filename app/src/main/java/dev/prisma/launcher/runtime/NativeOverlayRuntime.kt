package dev.prisma.launcher.runtime

/**
 * JNI access to Prisma's native overlay state model.
 *
 * This bridges the Android app to the C++ runtime. It does not attach to,
 * inject into, or render inside Geometry Dash. The in-game host integration
 * remains a separate, explicitly validated milestone.
 */
class NativeOverlayRuntime : AutoCloseable {
    private var handle: Long = 0L

    val isLoaded: Boolean

    init {
        isLoaded = try {
            System.loadLibrary("prisma_launcher_jni")
            handle = nativeCreate()
            handle != 0L
        } catch (_: UnsatisfiedLinkError) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    fun openPanel() {
        if (handle != 0L) nativeOpenPanel(handle)
    }

    fun closePanel() {
        if (handle != 0L) nativeClosePanel(handle)
    }

    fun togglePanel() {
        if (handle != 0L) nativeTogglePanel(handle)
    }

    fun isPanelOpen(): Boolean =
        handle != 0L && nativeIsPanelOpen(handle)

    fun isOrbVisible(): Boolean =
        handle != 0L && nativeIsOrbVisible(handle)

    /** Selects Mods, Browse, Settings or Logs using indices 0 through 3. */
    fun selectTab(index: Int) {
        require(index in 0..3) { "Tab index must be between 0 and 3." }
        if (handle != 0L) nativeSelectTab(handle, index)
    }

    override fun close() {
        val current = handle
        handle = 0L
        if (current != 0L) nativeDestroy(current)
    }

    private external fun nativeCreate(): Long
    private external fun nativeDestroy(handle: Long)
    private external fun nativeOpenPanel(handle: Long)
    private external fun nativeClosePanel(handle: Long)
    private external fun nativeTogglePanel(handle: Long)
    private external fun nativeIsPanelOpen(handle: Long): Boolean
    private external fun nativeIsOrbVisible(handle: Long): Boolean
    private external fun nativeSelectTab(handle: Long, index: Int)
}
