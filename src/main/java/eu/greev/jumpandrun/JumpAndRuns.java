package eu.greev.jumpandrun;

import eu.greev.jumpandrun.listeners.InteractListener;
import eu.greev.jumpandrun.listeners.MoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class JumpAndRuns extends JavaPlugin {

    @Override
    public void onEnable() {
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new MoveListener(), this);
    }
}
