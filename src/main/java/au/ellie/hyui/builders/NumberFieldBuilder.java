package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.events.UIEventActions;
import au.ellie.hyui.elements.UIElements;
import au.ellie.hyui.theme.Theme;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A builder class for constructing a number input field UI element. This class extends 
 * the UIElementBuilder to provide functionality specific to creating and customizing 
 * number input fields.
 *
 * The NumberFieldBuilder supports setting the initial numeric value, attaching event 
 * listeners, and integrating with specific themes and styles. It facilitates the seamless 
 * generation of commands and events during the UI build phase.
 */
public class NumberFieldBuilder extends UIElementBuilder<NumberFieldBuilder> {
    private Double value;

    /**
     * Do not use. Instead, use the static .numberInput().
     * @param theme
     */
    public NumberFieldBuilder(Theme theme) {
        super(theme, UIElements.MACRO_NUMBER_FIELD);
        withWrappingGroup(true);
        if (theme == Theme.GAME_THEME) {
            withUiFile("Pages/Elements/NumberInput.ui");
        }
    }

    /**
     * Creates a new instance of {@code NumberFieldBuilder} configured for the game theme.
     *
     * @return a new {@code NumberFieldBuilder} instance for defining and customizing a number input field.
     */
    public static NumberFieldBuilder numberInput() {
        return new NumberFieldBuilder(Theme.GAME_THEME);
    }

    /**
     * Sets the value for the number input field.
     *
     * @param value the numeric value to be set for the number input field
     * @return the current instance of {@code NumberFieldBuilder} for method chaining
     */
    public NumberFieldBuilder withValue(double value) {
        this.value = value;
        this.initialValue = value;
        return this;
    }

    /**
     * Adds an event listener to the number field builder. The only type it accepts will be ValueChanged.
     *
     * @param type     the type of the event to listen for, represented by {@code CustomUIEventBindingType}. 
     *                 This defines the specific event binding, such as {@code ValueChanged}.
     * @param callback the function to execute when the specified event occurs. The callback receives 
     *                 a {@code Double} value, which typically represents the current numeric value 
     *                 associated with the event.
     * @return the current instance of {@code NumberFieldBuilder}, enabling method chaining.
     */
    public NumberFieldBuilder addEventListener(CustomUIEventBindingType type, Consumer<Double> callback) {
        return addEventListenerInternal(type, callback);
    }

    /**
     * Adds an event listener to the number field builder with access to the UI context.
     *
     * @param type     The type of the event to bind the listener to.
     * @param callback The function to be executed when the specified event is triggered, with UI context.
     * @return This NumberFieldBuilder instance for method chaining.
     */
    public NumberFieldBuilder addEventListener(CustomUIEventBindingType type, BiConsumer<Double, UIContext> callback) {
        return addEventListenerInternal(type, callback);
    }

    @Override
    protected boolean usesRefValue() {
        return true;
    }

    @Override
    protected boolean supportsStyling() {
        return true;
    }

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector == null) return;

        if (value != null) {
            HyUIPlugin.getInstance().logInfo("Setting Value: " + value + " for " + selector);
            commands.set(selector + ".Value", value);
        }

        if (hyUIStyle == null && style != null) {
            HyUIPlugin.getInstance().logInfo("Setting Style: " + style + " for " + selector);
            commands.set(selector + ".Style", style);
        }

        listeners.forEach(listener -> {
            if (listener.type() == CustomUIEventBindingType.ValueChanged) {
                String eventId = getEffectiveId();
                HyUIPlugin.getInstance().logInfo("Adding ValueChanged event binding for " + selector + " with eventId: " + eventId);
                events.addEventBinding(CustomUIEventBindingType.ValueChanged, selector, 
                        EventData.of("@ValueDouble", selector + ".Value")
                            .append("Target", eventId)
                            .append("Action", UIEventActions.VALUE_CHANGED), 
                        false);
            }
        });
    }
}
