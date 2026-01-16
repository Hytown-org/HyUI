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

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builder for creating text field UI elements. Also known as Text Input elements.
 */
public class TextFieldBuilder extends UIElementBuilder<TextFieldBuilder> {
    private String value;

    /**
     * DO NOT USE UNLESS YOU KNOW WHAT YOU ARE DOING.
     * 
     * Not normally used, only used when creating a text field element from scratch.
     */
    public TextFieldBuilder() {
        super(UIElements.TEXT_FIELD);
        withWrappingGroup(true);
    }

    /**
     * DO NOT USE UNLESS YOU KNOW WHAT YOU ARE DOING.
     * 
     * Constructor for creating a text field element with a specified theme.
     * @param theme The theme to use for the text field element.
     */
    public TextFieldBuilder(Theme theme) {
        super(theme, UIElements.TEXT_FIELD);
        withWrappingGroup(true);
        if (theme == Theme.GAME_THEME) {
            withUiFile("Pages/Elements/TextInput.ui");
        }
    }

    /**
     * DO NOT USE UNLESS YOU KNOW WHAT YOU ARE DOING.
     * 
     * Constructor for creating a text field element with a specified theme and element path.
     * @param theme The theme to use for the text field element.
     * @param elementPath The path to the UI element definition file.
     */
    public TextFieldBuilder(Theme theme, String elementPath) {
        super(theme, elementPath);
        withWrappingGroup(true);
        if (UIElements.MACRO_TEXT_FIELD.equals(elementPath)) {
            withUiFile("Pages/Elements/TextInput.ui");
        }
    }

    /**
     * Creates a text input field with the game theme.
     * @return A new TextFieldBuilder instance configured for text input.
     */
    public static TextFieldBuilder textInput() {
        return new TextFieldBuilder(Theme.GAME_THEME, UIElements.MACRO_TEXT_FIELD);
    }

    /**
     * Sets the initial value of the text field.
     * @param value The initial value to set for the text field.
     * @return This TextFieldBuilder instance for method chaining.
     */
    public TextFieldBuilder withValue(String value) {
        this.value = value;
        this.initialValue = value;
        return this;
    }

    /**
     * Adds an event listener to the text field builder for handling a specific type of UI event.
     *
     * @param type The type of the event to bind the listener to. This specifies what kind of UI event 
     *             should trigger the provided callback.
     * @param callback The function to be executed when the specified event is triggered. The callback 
     *                 processes a string argument associated with the event.
     * @return This TextFieldBuilder instance for method chaining.
     */
    public TextFieldBuilder addEventListener(CustomUIEventBindingType type, Consumer<String> callback) {
        return addEventListenerInternal(type, callback);
    }

    /**
     * Adds an event listener to the text field builder with access to the UI context.
     *
     * @param type     The type of the event to bind the listener to.
     * @param callback The function to be executed when the specified event is triggered, with UI context.
     * @return This TextFieldBuilder instance for method chaining.
     */
    public TextFieldBuilder addEventListener(CustomUIEventBindingType type, BiConsumer<String, UIContext> callback) {
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
                        EventData.of("@Value", selector + ".Value")
                            .append("Target", eventId)
                            .append("Action", UIEventActions.VALUE_CHANGED), 
                        false);
            }
        });
    }
}
