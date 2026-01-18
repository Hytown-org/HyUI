package au.ellie.hyui.builders;

import au.ellie.hyui.events.UIContext;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class HyUIHud extends CustomUIHud implements UIContext {
    protected final HyUInterface delegate;

    private boolean isHidden;
    
    public HyUIHud(PlayerRef playerRef, String uiFile, 
                   List<UIElementBuilder<?>> elements, 
                   List<Consumer<UICommandBuilder>> editCallbacks) {
        super(playerRef);
        this.delegate = new HyUInterface(uiFile, elements, editCallbacks) {};
    }

    @Override
    public void build(UICommandBuilder uiCommandBuilder) {
        delegate.buildFromCommandBuilder(uiCommandBuilder);
    }

    public void update(HudBuilder updatedHudBuilder) {
        UICommandBuilder builder = configureFrom(updatedHudBuilder);
        this.update(true, builder);
    }

    public void hide() {
        setVisibilityOnRoot(false);
    }

    public void show() {
        setVisibilityOnRoot(true);
    }
    
    @Override
    public Optional<Object> getValue(String id) {
        return delegate.getValue(id);
    }

    private void setVisibilityOnRoot(boolean value) {
        for (UIElementBuilder<?> element : delegate.getElements()) {
            if ("HyUIRoot".equals(element.getId())) {
                element.withVisible(value);
            }
        }
        UICommandBuilder builder = new UICommandBuilder();
        delegate.buildFromCommandBuilder(builder);
        this.update(true, builder);
        isHidden = !isHidden;
    }

    @NonNullDecl
    private UICommandBuilder configureFrom(HudBuilder updatedHudBuilder) {
        UICommandBuilder builder = new UICommandBuilder();
        delegate.setEditCallbacks(updatedHudBuilder.editCallbacks);
        delegate.setElements(updatedHudBuilder.getTopLevelElements());
        delegate.setUiFile(updatedHudBuilder.uiFile);
        return builder;
    }
}
