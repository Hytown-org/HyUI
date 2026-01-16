package au.ellie.hyui.events;

import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import java.util.function.BiConsumer;

public record UIEventListener<V>(CustomUIEventBindingType type, BiConsumer<V, UIContext> callback) {
}
