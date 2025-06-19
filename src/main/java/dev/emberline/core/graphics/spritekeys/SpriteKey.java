package dev.emberline.core.graphics.spritekeys;

/**
 * Tag interface for objects that will be used as keys in a caching mechanism
 * based on a {@link java.util.HashMap}.
 *
 * <p><strong>IMPORTANT:</strong> Any class implementing this interface <em>must</em>
 * ensure that the {@code hashCode()} and {@code equals(Object)} methods are properly
 * and consistently implemented. This is crucial to guarantee that no duplicate keys
 * are stored in the underlying {@code HashMap} used for caching.
 *
 * <p>Failure to implement these methods correctly may result in duplicate entries in
 * the cache or inability to retrieve previously cached objects causing a
 * {@link java.lang.OutOfMemoryError}
 *
 * <p>To avoid such issues, it is required to:
 * <ul>
 *   <li>Override {@code equals(Object)} to define logical equality for the key.</li>
 *   <li>Override {@code hashCode()} to be consistent with {@code equals(Object)}.</li>
 * </ul>
 * otherwise use a {@code record} class.
 */
public interface SpriteKey  {}
