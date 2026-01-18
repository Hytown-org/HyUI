package au.ellie.hyui.builders;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.HudManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class HudBuilder extends InterfaceBuilder<HudBuilder> {
    private final PlayerRef playerRef;

    public HudBuilder(PlayerRef playerRef) {
        this.playerRef = playerRef;
        fromFile("Pages/EllieAU_HyUI_Placeholder.ui");
    }

    public HudBuilder() {
        this.playerRef = null;
        fromFile("Pages/EllieAU_HyUI_Placeholder.ui");
    }

    public static HudBuilder detachedHud() {
        return new HudBuilder();
    }

    public static HudBuilder hudForPlayer(PlayerRef ref) {
        return new HudBuilder(ref);
    }

    public HyUIHud show(Store<EntityStore> store) {
        assert playerRef != null : "Player reference cannot be null.";
        return show(playerRef, store);
    }

    public HyUIHud show(@Nonnull PlayerRef playerRefParam, Store<EntityStore> store) {
        Player playerComponent = store.getComponent(playerRefParam.getReference(), Player.getComponentType());
        HudManager hudManager = playerComponent.getHudManager();
        var hyUIHud = new HyUIHud(playerRefParam, uiFile, getTopLevelElements(), editCallbacks);
        hudManager.setCustomHud(playerRefParam, hyUIHud);
        return hyUIHud;
    }
    
    public void updateExisting(@Nonnull HyUIHud hudRef) {
        hudRef.update(this);
    }
}
