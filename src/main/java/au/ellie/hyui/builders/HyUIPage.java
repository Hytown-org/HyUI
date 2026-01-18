package au.ellie.hyui.builders;

import au.ellie.hyui.HyUIPlugin;
import au.ellie.hyui.events.DynamicPageData;
import au.ellie.hyui.events.UIContext;
import au.ellie.hyui.events.UIEventActions;
import au.ellie.hyui.events.UIEventListener;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class HyUIPage extends InteractiveCustomUIPage<DynamicPageData> implements UIContext {
    private final HyUInterface delegate;

    public HyUIPage(PlayerRef playerRef, CustomPageLifetime lifetime, String uiFile, List<UIElementBuilder<?>> elements, List<Consumer<UICommandBuilder>> editCallbacks) {
        super(playerRef, lifetime, DynamicPageData.CODEC);
        this.delegate = new HyUInterface(uiFile, elements, editCallbacks) {};
    }

    @Override
    public Optional<Object> getValue(String id) {
        return delegate.getValue(id);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        delegate.build(ref, uiCommandBuilder, uiEventBuilder, store);
    }


    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull DynamicPageData data) {
        super.handleDataEvent(ref, store, data);
        delegate.handleDataEventInternal(data);
    }
}
