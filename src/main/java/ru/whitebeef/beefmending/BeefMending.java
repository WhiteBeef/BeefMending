package ru.whitebeef.beefmending;


import ru.whitebeef.beeflibrary.BeefLibrary;
import ru.whitebeef.beeflibrary.plugin.BeefPlugin;
import ru.whitebeef.beefmending.handlers.ExpSpawnHandler;

public final class BeefMending extends BeefPlugin {

    private static BeefMending instance;

    public static BeefMending getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        instance = this;

        BeefLibrary.registerListeners(this, new ExpSpawnHandler());
    }
}
