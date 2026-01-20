package au.ellie.hyui.html;

import au.ellie.hyui.builders.HyUIAnchor;
import au.ellie.hyui.builders.HyUIStyle;
import au.ellie.hyui.builders.UIElementBuilder;
import com.hypixel.hytale.server.core.Message;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for handling a specific HTML tag and converting it to a HyUI builder.
 */
public interface TagHandler {
    /**
     * Checks if this handler can handle the given element.
     *
     * @param element The Jsoup element to check.
     * @return true if this handler can process the element, false otherwise.
     */
    boolean canHandle(Element element);

    /**
     * Handles the conversion of the HTML element to a UIElementBuilder.
     *
     * @param element The Jsoup element to convert.
     * @param parser  The parser instance for recursive calls if needed.
     * @return A UIElementBuilder representing the element, or null if it should be ignored.
     */
    UIElementBuilder<?> handle(Element element, HtmlParser parser);

    /**
     * Applies common attributes like id, style, data-*, etc. to the builder.
     *
     * @param builder The builder to apply attributes to.
     * @param element The HTML element containing the attributes.
     */
    default void applyCommonAttributes(UIElementBuilder<?> builder, Element element) {
        if (element.hasAttr("id")) {
            builder.withId(element.attr("id"));
        }

        if (element.hasAttr("data-hyui-tooltiptext")) {
            builder.withTooltipTextSpan(Message.raw(element.attr("data-hyui-tooltiptext")));
        }

        if (element.hasAttr("data-hyui-flexweight")) {
            try {
                builder.withFlexWeight(Integer.parseInt(element.attr("data-hyui-flexweight")));
            } catch (NumberFormatException ignored) {}
        }

        if (element.hasAttr("style")) {
            Map<String, String> styles = parseStyleAttribute(element.attr("style"));
            applyStyles(builder, styles);
        }
    }

    private Map<String, String> parseStyleAttribute(String styleAttr) {
        Map<String, String> styles = new HashMap<>();
        String[] declarations = styleAttr.split(";");
        for (String declaration : declarations) {
            String[] parts = declaration.split(":", 2);
            if (parts.length == 2) {
                styles.put(parts[0].trim().toLowerCase(), parts[1].trim());
            }
        }
        return styles;
    }

    private void applyStyles(UIElementBuilder<?> builder, Map<String, String> styles) {
        // Special handling for buttons - collect all pseudo-states
        if (builder instanceof au.ellie.hyui.builders.ButtonBuilder) {
            applyButtonStyles(builder, styles);
            return;
        }
        
        // For non-buttons, apply styles normally
        HyUIStyle hyStyle = new HyUIStyle();
        HyUIAnchor anchor = new HyUIAnchor();
        boolean hasStyle = false;
        boolean hasAnchor = false;

        for (Map.Entry<String, String> entry : styles.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            // Skip pseudo-state properties for non-buttons
            if (key.startsWith("--hover-") || key.startsWith("--pressed-") || 
                key.startsWith("--active-") || key.startsWith("--disabled-")) {
                continue;
            }

            switch (key) {
                case "color":
                    hyStyle.setTextColor(value);
                    hasStyle = true;
                    break;
                case "background":
                case "background-color":
                    // Groups/Containers can have Background set directly
                    builder.editElementAfter((commandBuilder, selector) -> {
                        commandBuilder.set(selector + ".Background", value);
                    });
                    break;
                case "font-size":
                    // Font size doesn't work on buttons, only on labels
                    if (!(builder instanceof au.ellie.hyui.builders.ButtonBuilder)) {
                        hyStyle.setFontSize(value);
                        hasStyle = true;
                    }
                    break;
                case "font-weight":
                    // Bold doesn't work on buttons, only on labels
                    if (value.equalsIgnoreCase("bold") && !(builder instanceof au.ellie.hyui.builders.ButtonBuilder)) {
                        hyStyle.setRenderBold(true);
                        hasStyle = true;
                    }
                    break;
                case "text-transform":
                    // Uppercase doesn't work on buttons, only on labels
                    if (value.equalsIgnoreCase("uppercase") && !(builder instanceof au.ellie.hyui.builders.ButtonBuilder)) {
                        hyStyle.setRenderUppercase(true);
                        hasStyle = true;
                    }
                    break;
                case "text-align":
                case "layout-mode":
                case "layout":
                    // This maps to LayoutMode in some builders
                    if (builder instanceof au.ellie.hyui.builders.GroupBuilder) {
                        ((au.ellie.hyui.builders.GroupBuilder) builder).withLayoutMode(capitalize(value));
                    }
                    break;
                case "vertical-align":
                    hyStyle.setVerticalAlignment(capitalize(value));
                    hasStyle = true;
                    break;
                case "horizontal-align":
                    hyStyle.setHorizontalAlignment(capitalize(value));
                    hasStyle = true;
                    break;
                case "align":
                    hyStyle.setAlignment(capitalize(value));
                    hasStyle = true;
                    break;
                case "visibility":
                    if (value.equalsIgnoreCase("hidden")) {
                        builder.withVisible(false);
                    } else if (value.equalsIgnoreCase("shown")) {
                        builder.withVisible(true);
                    }
                    break;
                case "display":
                    if (value.equalsIgnoreCase("none")) {
                        builder.withVisible(false);
                    } else if (value.equalsIgnoreCase("block")) {
                        builder.withVisible(true);
                    }
                    break;
                case "flex-weight":
                    try {
                        builder.withFlexWeight(Integer.parseInt(value));
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-left":
                    try {
                        anchor.setLeft(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-right":
                    try {
                        anchor.setRight(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-top":
                    try {
                        anchor.setTop(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-bottom":
                    try {
                        anchor.setBottom(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-width":
                    try {
                        anchor.setWidth(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor-height":
                    try {
                        anchor.setHeight(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "anchor":
                    try {
                        anchor.setFull(Integer.parseInt(value));
                        hasAnchor = true;
                    } catch (NumberFormatException ignored) {}
                    break;
                case "padding":
                    // Padding only works on containers (Groups), not buttons or labels
                    // Only apply if this is a GroupBuilder
                    if (builder instanceof au.ellie.hyui.builders.GroupBuilder) {
                        builder.editElementAfter((commandBuilder, selector) -> {
                            String[] parts = value.trim().split("\\s+");
                            if (parts.length == 1) {
                                // All sides
                                commandBuilder.set(selector + ".Padding", "(Left: " + parts[0] + ", Top: " + parts[0] + ", Right: " + parts[0] + ", Bottom: " + parts[0] + ")");
                            } else if (parts.length == 2) {
                                // top/bottom left/right
                                commandBuilder.set(selector + ".Padding", "(Left: " + parts[1] + ", Top: " + parts[0] + ", Right: " + parts[1] + ", Bottom: " + parts[0] + ")");
                            } else if (parts.length == 4) {
                                // top right bottom left
                                commandBuilder.set(selector + ".Padding", "(Left: " + parts[3] + ", Top: " + parts[0] + ", Right: " + parts[1] + ", Bottom: " + parts[2] + ")");
                            }
                        });
                    }
                    break;
            }
        }

        if (hasStyle) {
            builder.withStyle(hyStyle);
        }
        if (hasAnchor) {
            builder.withAnchor(anchor);
        }
    }

    /**
     * Applies button-specific styles including pseudo-states.
     * Supports CSS custom properties for states:
     * - Default: background, color, font-size, font-weight, text-transform
     * - Hover: --hover-background, --hover-color, etc.
     * - Pressed: --pressed-background, --pressed-color, etc.
     * - Disabled: --disabled-background, --disabled-color, etc.
     */
    private void applyButtonStyles(UIElementBuilder<?> builder, Map<String, String> styles) {
        // Separate styles by state
        Map<String, String> defaultStyles = new HashMap<>();
        Map<String, String> hoverStyles = new HashMap<>();
        Map<String, String> pressedStyles = new HashMap<>();
        Map<String, String> disabledStyles = new HashMap<>();
        
        // Other non-button-style properties (padding, anchor, etc.)
        HyUIAnchor anchor = new HyUIAnchor();
        boolean hasAnchor = false;
        
        for (Map.Entry<String, String> entry : styles.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (key.startsWith("--hover-")) {
                // --hover-background -> background
                String prop = key.substring(8); // Remove "--hover-"
                hoverStyles.put(prop, value);
            } else if (key.startsWith("--pressed-") || key.startsWith("--active-")) {
                // --pressed-background or --active-background -> background
                String prop = key.startsWith("--pressed-") ? key.substring(10) : key.substring(9);
                pressedStyles.put(prop, value);
            } else if (key.startsWith("--disabled-")) {
                // --disabled-background -> background
                String prop = key.substring(11); // Remove "--disabled-"
                disabledStyles.put(prop, value);
            } else if (key.startsWith("anchor-")) {
                // Handle anchor properties
                String anchorProp = key.substring(7); // Remove "anchor-"
                Integer intValue = parseInt(value);
                if (intValue != null) {
                    switch (anchorProp) {
                        case "top": anchor.setTop(intValue); hasAnchor = true; break;
                        case "left": anchor.setLeft(intValue); hasAnchor = true; break;
                        case "right": anchor.setRight(intValue); hasAnchor = true; break;
                        case "bottom": anchor.setBottom(intValue); hasAnchor = true; break;
                        case "width": anchor.setWidth(intValue); hasAnchor = true; break;
                        case "height": anchor.setHeight(intValue); hasAnchor = true; break;
                    }
                }
            } else if (key.equals("flex-weight")) {
                try {
                    builder.withFlexWeight(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {}
            } else if (key.equals("layout-mode") || key.equals("layout")) {
                // Skip - not applicable to buttons
            } else if (key.equals("padding")) {
                // Skip - buttons don't support padding directly
            } else {
                // Default state properties
                defaultStyles.put(key, value);
            }
        }
        
        // Apply anchor if we have any
        if (hasAnchor) {
            builder.withAnchor(anchor);
        }
        
        // Store the generated style DSL for later use
        // We'll apply it by defining a local style variable and using it
        String styleDsl = ButtonStyleGenerator.generateTextButtonStyleDsl(
            defaultStyles.isEmpty() ? null : defaultStyles,
            hoverStyles.isEmpty() ? null : hoverStyles,
            pressedStyles.isEmpty() ? null : pressedStyles,
            disabledStyles.isEmpty() ? null : disabledStyles
        );
        
        if (styleDsl != null) {
            // We need to inject the style as part of the button definition via appendInline
            // This is a limitation: we can't set Style dynamically, only via .ui files or inline DSL
            // For now, store it in the builder for potential future use
            builder.editElementBefore((commandBuilder, selector) -> {
                // Inject a style definition inline before the button
                // Format: @CustomButtonStyle = TextButtonStyle(...);
                String styleVarName = "CustomStyle" + Math.abs(selector.hashCode());
                commandBuilder.appendInline(selector.substring(0, selector.lastIndexOf(" ")), 
                    "@" + styleVarName + " = " + styleDsl + ";");
            });
            
            // Then set the button to use that style
            builder.editElementAfter((commandBuilder, selector) -> {
                String styleVarName = "CustomStyle" + Math.abs(selector.hashCode());
                commandBuilder.set(selector + ".Style", "$." + styleVarName);
            });
        }
    }
    
    private Integer parseInt(String value) {
        try {
            return Integer.parseInt(value.replaceAll("[^0-9-]", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        // Check for specific multi-word LayoutModes
        if (str.equalsIgnoreCase("topscrolling")) return "TopScrolling";
        if (str.equalsIgnoreCase("bottomscrolling")) return "BottomScrolling";
        if (str.equalsIgnoreCase("middlecenter")) return "MiddleCenter";
        if (str.equalsIgnoreCase("centermiddle")) return "CenterMiddle";
        if (str.equalsIgnoreCase("leftcenterwrap")) return "LeftCenterWrap";
        
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
