package au.ellie.hyui.builders;

import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.html.HtmlParser;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class InterfaceBuilder<T extends InterfaceBuilder<T>> {
    protected final Map<String, UIElementBuilder<?>> elementRegistry = new LinkedHashMap<>();
    protected final List<Consumer<UICommandBuilder>> editCallbacks = new ArrayList<>();
    protected String uiFile;

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public T fromFile(String uiFile) {
        this.uiFile = uiFile;
        return self();
    }

    public T fromHtml(String html) {
        new HtmlParser().parseToInterface(this, html);
        return self();
    }

    public T addElement(UIElementBuilder<?> element) {
        element.inside("#HyUIRoot");
        registerElement(element);
        return self();
    }

    protected void registerElement(UIElementBuilder<?> element) {
        if (element.getId() != null) {
            this.elementRegistry.put(element.getId(), element);
        }
        for (UIElementBuilder<?> child : element.children) {
            registerElement(child);
        }
    }

    public <E extends UIElementBuilder<E>> Optional<E> getById(String id, Class<E> clazz) {
        UIElementBuilder<?> builder = elementRegistry.get(id);
        if (builder != null && clazz.isInstance(builder)) {
            return Optional.of(clazz.cast(builder));
        }
        return Optional.empty();
    }

    public <V> T addEventListener(String id, CustomUIEventBindingType type, Class<V> valueClass, Consumer<V> callback) {
        UIElementBuilder<?> element = elementRegistry.get(id);
        if (element == null) {
            throw new IllegalArgumentException("No element found with ID '" + id + "'.");
        }
        element.addEventListener(type, valueClass, callback);
        return self();
    }

    public T addEventListener(String id, CustomUIEventBindingType type, Consumer<Object> callback) {
        return addEventListener(id, type, Object.class, callback);
    }

    public <V> T addEventListener(String id, CustomUIEventBindingType type, Class<V> valueClass, BiConsumer<V, UIContext> callback) {
        UIElementBuilder<?> element = elementRegistry.get(id);
        if (element == null) {
            throw new IllegalArgumentException("No element found with ID '" + id + "'.");
        }
        element.addEventListenerWithContext(type, valueClass, callback);
        return self();
    }

    public T addEventListener(String id, CustomUIEventBindingType type, BiConsumer<Object, UIContext> callback) {
        return addEventListener(id, type, Object.class, callback);
    }

    public T editElement(Consumer<UICommandBuilder> callback) {
        this.editCallbacks.add(callback);
        return self();
    }

    protected List<UIElementBuilder<?>> getTopLevelElements() {
        List<UIElementBuilder<?>> topLevel = new ArrayList<>();
        for (UIElementBuilder<?> element : elementRegistry.values()) {
            if ("#HyUIRoot".equals(element.parentSelector)) {
                topLevel.add(element);
            }
        }
        return topLevel;
    }
}
