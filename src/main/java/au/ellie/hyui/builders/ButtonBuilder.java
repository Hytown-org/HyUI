package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.events.UIEventActions;
import au.ellie.hyui.elements.UIElements;
import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.theme.Theme;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builder for creating button UI elements. 
 * Buttons are interactive elements that can trigger actions when clicked.
 */
public class ButtonBuilder extends UIElementBuilder<ButtonBuilder> {
    private String text;

    /**
     * You do not need to call this.
     */
    public ButtonBuilder() {
        super(UIElements.BUTTON);
        withWrappingGroup(true);
    }
    
    /**
     * You do not need to call this.
     */
    public ButtonBuilder(Theme theme) {
        super(theme, UIElements.BUTTON);
        withWrappingGroup(true);
        if (theme == Theme.GAME_THEME) {
            withUiFile("Pages/Elements/TextButton.ui");
        }
    }

    /**
     * You do not need to call this.
     * 
     * @param theme
     * @param elementPath
     */
    public ButtonBuilder(Theme theme, String elementPath) {
        super(theme, elementPath);
        withWrappingGroup(true);
        if (UIElements.TEXT_BUTTON.equals(elementPath)) {
            withUiFile("Pages/Elements/TextButton.ui");
        } else if (UIElements.CANCEL_TEXT_BUTTON.equals(elementPath)) {
            withUiFile("Pages/Elements/CancelTextButton.ui");
        } else if (UIElements.BACK_BUTTON.equals(elementPath)) {
            withUiFile("Pages/Elements/BackButton.ui");
        }
    }

    /**
     * Creates a ButtonBuilder instance for a text-based button styled with the GAME_THEME and the TEXT_BUTTON element.
     *
     * @return a ButtonBuilder configured for creating a text button with predefined theme and style.
     */
    public static ButtonBuilder textButton() {
        return new ButtonBuilder(Theme.GAME_THEME, UIElements.TEXT_BUTTON);
    }

    /**
     * Creates a ButtonBuilder instance for a cancel text button styled with the GAME_THEME and 
     * the CANCEL_TEXT_BUTTON element.
     *
     * @return a ButtonBuilder configured for creating a cancel text button with predefined theme and style.
     */
    public static ButtonBuilder cancelTextButton() {
        return new ButtonBuilder(Theme.GAME_THEME, UIElements.CANCEL_TEXT_BUTTON);
    }

    /**
     * Sets the text for the button being built. Replaces any other text.
     *
     * @param text the text to be displayed on the button
     * @return the current instance of ButtonBuilder for method chaining
     */
    public ButtonBuilder withText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Adds an event listener to this button. This allows the button to respond to specific UI events
     * that are triggered during interaction.
     *
     * @param type the type of UI event to listen for, specified as a {@link CustomUIEventBindingType}
     * @param callback a callback function to handle the event, expressed as a {@link Consumer<Void>}
     * @return the current instance of {@code ButtonBuilder} for method chaining
     */
    public ButtonBuilder addEventListener(CustomUIEventBindingType type, Consumer<Void> callback) {
        return addEventListenerInternal(type, callback);
    }

    /**
     * Adds an event listener to this button with access to the UI context.
     *
     * @param type the type of UI event to listen for, specified as a {@link CustomUIEventBindingType}
     * @param callback a callback function to handle the event, expressed as a {@link BiConsumer<Void, UIContext>}
     * @return the current instance of {@code ButtonBuilder} for method chaining
     */
    public ButtonBuilder addEventListener(CustomUIEventBindingType type, BiConsumer<Void, UIContext> callback) {
        return addEventListenerInternal(type, callback);
    }

    @Override
    protected boolean supportsStyling() {
        return true;
    }

    @Override
    protected Set<String> getUnsupportedStyleProperties() {
        if (this.theme == Theme.GAME_THEME) {
            return Set.of("FontSize", "TextColor");
        }
        return Collections.emptySet();
    }

    @Override
    protected void onBuild(UICommandBuilder commands, UIEventBuilder events) {
        String selector = getSelector();
        if (selector == null) return;

        if (text != null) {
            HyUIPlugin.getInstance().logInfo("Setting Text: " + text + " for " + selector);
            commands.set(selector + ".Text", text);
        }

        if (hyUIStyle == null && style != null) {
            HyUIPlugin.getInstance().logInfo("Setting Style: " + style + " for " + selector);
            commands.set(selector + ".Style", style);
        }

        listeners.forEach(listener -> {
            if (listener.type() == CustomUIEventBindingType.Activating) {
                String eventId = getEffectiveId();
                HyUIPlugin.getInstance().logInfo("Adding Activating event binding: " + eventId + " for " + selector);
                events.addEventBinding(CustomUIEventBindingType.Activating, selector, 
                        EventData.of("Action", UIEventActions.BUTTON_CLICKED)
                            .append("Target", eventId), 
                        false);
            }
        });
    }
}
