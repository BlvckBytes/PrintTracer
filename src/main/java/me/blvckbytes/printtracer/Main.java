package me.blvckbytes.printtracer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Plugin(name = "PrintTracer", version = "0.1")
@Author("BlvckBytes")
@ApiVersion(ApiVersion.Target.v1_13)
public class Main extends JavaPlugin {

  private final Map<String, org.bukkit.plugin.Plugin> loadedPlugins;

  public Main() {
    this.loadedPlugins = new HashMap<>();
  }

  @Override
  public void onLoad() {
    this.scanLoadedPlugins();

    PrintStream vanillaOut = System.out;
    System.setOut(new InstrumentedPrintStream(System.out, frames -> {
      org.bukkit.plugin.Plugin invoker = getPlugin(frames);
      vanillaOut.println("The following print is sent by: " + (invoker == null ? "?" : invoker.getName()));
    }));
  }

  @Override
  public void onEnable() {
    // After the first tick elapsed, all plugins should be enabled
    Bukkit.getScheduler().runTask(this, this::scanLoadedPlugins);
  }

  private @Nullable org.bukkit.plugin.Plugin getPlugin(List<StackWalker.StackFrame> frames) {
    for (StackWalker.StackFrame frame : frames) {
      String declaringClassPackage = frame.getDeclaringClass().getPackageName();
      for (String knownPackage : loadedPlugins.keySet()) {
        if (declaringClassPackage.startsWith(knownPackage))
          return loadedPlugins.get(knownPackage);
      }
    }

    return null;
  }

  private void scanLoadedPlugins() {
    for (org.bukkit.plugin.Plugin plugin : Bukkit.getPluginManager().getPlugins()) {

      // Ignore self (always in the stack-trace)
      if (plugin == this)
        continue;

      loadedPlugins.put(plugin.getClass().getPackageName(), plugin);
    }
  }
}
