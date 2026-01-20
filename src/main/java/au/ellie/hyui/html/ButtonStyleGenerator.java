package au.ellie.hyui.html;

import java.util.Map;

/**
 * Generates TextButtonStyle DSL syntax from CSS properties.
 * Converts CSS like "background: #D2B48C" into Hytale .ui DSL format.
 */
public class ButtonStyleGenerator {
    
    /**
     * Generates a complete TextButtonStyle DSL string from CSS properties for different states.
     * 
     * @param defaultStyle CSS properties for default state (background, color, etc.)
     * @param hoverStyle CSS properties for :hover state
     * @param pressedStyle CSS properties for :pressed/:active state
     * @param disabledStyle CSS properties for :disabled state
     * @return Complete TextButtonStyle(...) DSL string
     */
    public static String generateTextButtonStyleDsl(
            Map<String, String> defaultStyle,
            Map<String, String> hoverStyle,
            Map<String, String> pressedStyle,
            Map<String, String> disabledStyle) {
        
        if (defaultStyle == null || defaultStyle.isEmpty()) {
            return null; // No styling to apply
        }
        
        StringBuilder dsl = new StringBuilder("TextButtonStyle(");
        boolean hasContent = false;
        
        // Default state
        if (defaultStyle != null && !defaultStyle.isEmpty()) {
            dsl.append("Default: (");
            appendButtonStateProperties(dsl, defaultStyle);
            dsl.append(")");
            hasContent = true;
        }
        
        // Hovered state
        if (hoverStyle != null && !hoverStyle.isEmpty()) {
            if (hasContent) dsl.append(", ");
            dsl.append("Hovered: (");
            appendButtonStateProperties(dsl, hoverStyle);
            dsl.append(")");
            hasContent = true;
        }
        
        // Pressed state
        if (pressedStyle != null && !pressedStyle.isEmpty()) {
            if (hasContent) dsl.append(", ");
            dsl.append("Pressed: (");
            appendButtonStateProperties(dsl, pressedStyle);
            dsl.append(")");
            hasContent = true;
        }
        
        // Disabled state
        if (disabledStyle != null && !disabledStyle.isEmpty()) {
            if (hasContent) dsl.append(", ");
            dsl.append("Disabled: (");
            appendButtonStateProperties(dsl, disabledStyle);
            dsl.append(")");
            hasContent = true;
        }
        
        dsl.append(")");
        return hasContent ? dsl.toString() : null;
    }
    
    /**
     * Appends properties for a single button state (Default, Hovered, etc.)
     * Format: Background: #color, LabelStyle: (TextColor: #color, FontSize: N, RenderBold: true)
     */
    private static void appendButtonStateProperties(StringBuilder dsl, Map<String, String> cssProps) {
        boolean hasProps = false;
        
        // Background (directly on button state)
        String background = getCssValue(cssProps, "background", "background-color");
        if (background != null) {
            dsl.append("Background: ").append(background);
            hasProps = true;
        }
        
        // Text styling goes in LabelStyle
        boolean hasLabelStyle = false;
        StringBuilder labelStyle = new StringBuilder();
        
        String color = getCssValue(cssProps, "color");
        if (color != null) {
            labelStyle.append("TextColor: ").append(color);
            hasLabelStyle = true;
        }
        
        String fontSize = getCssValue(cssProps, "font-size");
        if (fontSize != null) {
            if (hasLabelStyle) labelStyle.append(", ");
            // Convert "16px" to just "16"
            String size = fontSize.replaceAll("[^0-9.]", "");
            labelStyle.append("FontSize: ").append(size);
            hasLabelStyle = true;
        }
        
        String fontWeight = getCssValue(cssProps, "font-weight");
        if (fontWeight != null && fontWeight.equalsIgnoreCase("bold")) {
            if (hasLabelStyle) labelStyle.append(", ");
            labelStyle.append("RenderBold: true");
            hasLabelStyle = true;
        }
        
        String textTransform = getCssValue(cssProps, "text-transform");
        if (textTransform != null && textTransform.equalsIgnoreCase("uppercase")) {
            if (hasLabelStyle) labelStyle.append(", ");
            labelStyle.append("RenderUppercase: true");
            hasLabelStyle = true;
        }
        
        // Add LabelStyle if we have any text properties
        if (hasLabelStyle) {
            if (hasProps) dsl.append(", ");
            dsl.append("LabelStyle: (").append(labelStyle).append(")");
            hasProps = true;
        }
    }
    
    /**
     * Gets a CSS value, checking multiple possible property names.
     */
    private static String getCssValue(Map<String, String> cssProps, String... keys) {
        for (String key : keys) {
            String value = cssProps.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }
}
