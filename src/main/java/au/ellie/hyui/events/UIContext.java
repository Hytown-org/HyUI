package au.ellie.hyui.events;

import java.util.Optional;

/**
 * Provides access to the current state of UI elements on the page.
 */
public interface UIContext {
    /**
     * Retrieves the current value of an element by its ID.
     *
     * @param id The ID of the element (set via .withId).
     * @return An Optional containing the value, or empty if the ID is not found or has no value.
     */
    Optional<Object> getValue(String id);

    /**
     * Retrieves the current value of an element by its ID, cast to the specified type.
     *
     * @param id   The ID of the element.
     * @param type The class of the type to cast to.
     * @param <T>  The expected type of the value.
     * @return An Optional containing the cast value, or empty if not found or if casting fails.
     */
    default <T> Optional<T> getValue(String id, Class<T> type) {
        return getValue(id).filter(type::isInstance).map(type::cast);
    }
}
