package com.witch.addon;

import com.witch.addon.hud.WitchHud;
import com.witch.addon.intro.IntroHook;
import com.witch.addon.ui.ThemeTweak;
import com.witch.addon.modules.*;
import meteordevelopment.meteorclient.MeteorClient;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class WitchAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Witch");
    public static final HudGroup HUD_GROUP = new HudGroup("Witch");

    @Override
    public void onInitialize() {
        LOG.info("Witch Addon carregando");
        MeteorClient.EVENT_BUS.subscribe(new IntroHook());
        MeteorClient.EVENT_BUS.subscribe(new ThemeTweak());
        Modules m = Modules.get();
        m.add(new FastFall());
        m.add(new Glide());
        m.add(new LowGravity());
        m.add(new WallJump());
        m.add(new AirBrake());
        m.add(new Pogo());
        m.add(new YawLock());
        m.add(new PitchLock());
        m.add(new LowHealthAlert());
        m.add(new ArmorWarning());
        m.add(new FarmEsp());
        m.add(new ChunkBorders());
        m.add(new AncientFinder());
        m.add(new JumpCircles());
        m.add(new Hover());
        m.add(new Levitate());
        m.add(new Intro());
        m.add(new HeldItemGlow());
        m.add(new ToggleSneak());
        m.add(new Spinner());
        m.add(new HotbarCycle());

        Hud hud = Hud.get();
        hud.register(WitchHud.WATERMARK);
        hud.register(WitchHud.DAY);
        hud.register(WitchHud.DIRECTION);
        hud.register(WitchHud.NETHER);
        hud.register(WitchHud.SPEED);
        hud.register(WitchHud.CLOCK);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.witch.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("henriquenatan288-ui", "Witch-Addon");
    }
}
