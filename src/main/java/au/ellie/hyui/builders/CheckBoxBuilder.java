package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.events.UIEventActions;
import au.ellie.hyui.elements.UIElements;
import au.ellie.hyui.theme.Theme;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.function.Consumer;

/**
 * Builder for creating checkbox UI elements with label.
 */
public class CheckBoxBuilder extends UIElementBuilder<CheckBoxBuilder> {
    private Boolean value;
    private String text;

    /**
     * Constructs a new instance of {@code CheckBoxBuilder}.
     * The builder is pre-configured to create a checkbox UI element with a label, 
     * wrapped within a grouping element.
     */
    public CheckBoxBuilder() {
        super(UIElements.CHECK_BOX_WITH_LABEL);
        withWrappingGroup(true);
        withUiFile("Pages/Elements/CheckBox.ui");
    }

    /**
     * Sets the value state for the checkbox being constructed.
     *
     * @param value the value to set for the checkbox; {@code true} indicates checked, and {@code false} indicates unchecked
     * @return the {@code CheckBoxBuilder} for method chaining
     */
    public CheckBoxBuilder withValue(boolean value) {
        this.value = value;
        return this;
    }

    /**
     * Sets the text to be displayed as the label for the checkbox being constructed.
     *
     * @param text the text to set as the label (to the right) of the checkbox
     * @return the {@code CheckBoxBuilder} instance for method chaining
     */
    public CheckBoxBuilder withText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Registers an event listener for the checkbox being constructed. The listener is triggered 
     * based on the specified event type and executes the provided callback when the event occurs.
     *
     * @param type     the type of UI event to bind the listener to, such as value changes or activation
     * @param callback the callback function to execute when the event is triggered, receiving the event value as a parameter
     * @return the {@code CheckBoxBuilder} instance for method chaining, allowing further configuration
     */
    public CheckBoxBuilder addEventListener(CustomUIEventBindingType type, Consumer<Boolean> callback) {
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
            // For CheckBoxWithLabel, the actual CheckBox is a child
            HyUIPlugin.getInstance().logInfo("Setting Value: " + value + " for " + selector + " #CheckBox");
            commands.set(selector + " #CheckBox.Value", value);
        }

        if (text != null) {
            HyUIPlugin.getInstance().logInfo("Setting Text: " + text + " for " + selector);
            commands.set(selector + ".@Text", text);
        }

        if (hyUIStyle == null && style != null) {
            HyUIPlugin.getInstance().logInfo("Setting Style: " + style + " for " + selector);
            commands.set(selector + ".Style", style);
        }

        listeners.forEach(listener -> {
            if (listener.type() == CustomUIEventBindingType.ValueChanged) {
                String eventId = getEffectiveId();
                HyUIPlugin.getInstance().logInfo("Adding ValueChanged event binding for " + selector + " #CheckBox with eventId: " + eventId);
                events.addEventBinding(CustomUIEventBindingType.ValueChanged, selector + " #CheckBox", 
                        EventData.of("@ValueBool", selector + " #CheckBox.Value")
                            .append("Target", eventId)
                            .append("Action", UIEventActions.VALUE_CHANGED),
                        false);
            }
        });
    }
}
