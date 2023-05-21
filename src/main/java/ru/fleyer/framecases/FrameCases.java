package ru.fleyer.framecases;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.zaxxer.hikari.HikariDataSource;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.fleyer.framecases.Events.CaseEvents;
import ru.fleyer.framecases.commands.CaseCommand;
import ru.fleyer.framecases.configs.ConfigurationGeneration;
import ru.fleyer.framecases.database.DatabaseConstructor;

import java.util.ArrayList;
import java.util.List;

public final class FrameCases extends JavaPlugin {
    private static FrameCases instance;
    private HikariDataSource hikari;
    private ConfigurationGeneration config;
    private ConfigurationGeneration lang;
    public Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        config = new ConfigurationGeneration(this,"config.yml");
        lang = new ConfigurationGeneration(this,"lang.yml");
        if (!this.setupEconomy()) {
            Bukkit.getConsoleSender().sendMessage(Utils.INSTANCE.getMsg("vault.existnot"));
        }
        // Database connection
        hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", config.yaml().getString("mysql.ip"));
        hikari.addDataSourceProperty("port", config.yaml().getInt("mysql.port"));
        hikari.addDataSourceProperty("databaseName", config.yaml().getString("mysql.database"));
        hikari.addDataSourceProperty("user", config.yaml().getString("mysql.username"));
        hikari.addDataSourceProperty("password", config.yaml().getString("mysql.password"));
        DatabaseConstructor.INSTANCE.START();
        getCommand("case").setExecutor(new CaseCommand());
        getServer().getPluginManager().registerEvents(new CaseEvents(), this);
        for (String roulette : config.yaml().getConfigurationSection("cases").getKeys(false)) {
            for (String loc : config().yaml().getStringList("cases." + roulette + ".locations")) {
                CaseSettings.INSTANCE.rouletteLoc.put(loc, roulette);
                CaseSettings.INSTANCE.prizes.put(loc, new ArrayList<>());
                CaseSettings.INSTANCE.wins.put(loc, new ArrayList<>());
                List<String> holo = config().yaml().getStringList("cases." + roulette + ".holo");
                Location location = new Location(Bukkit.getWorld(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]), Integer.parseInt(loc.split(",")[2]), Integer.parseInt(loc.split(",")[3]));
                CaseSettings.INSTANCE.rouletteData.put(loc, location.getBlock().getData());
                location.add(0.5D, 1.25D + (double) holo.size() * 0.25D, 0.5D);
                Hologram hologram = HologramsAPI.createHologram(instance, location);

                for (String line : holo) {
                    hologram.appendTextLine(line.replace("&", "§"));
                }

                CaseSettings.INSTANCE.holograms.put(loc, hologram);
            }
        }
        CaseSettings.INSTANCE.spawnHolo();



    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        new CaseSettings().disable();
    }
    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> EconomyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (EconomyProvider != null) {
            economy = EconomyProvider.getProvider();
        }
        return economy != null;
    }
    public static FrameCases getInstance(){
        return instance;
    }

    public HikariDataSource getHikari(){
        return hikari;
    }
    public ConfigurationGeneration config() {
        return config;
    }

    public ConfigurationGeneration lang() {
        return lang;
    }
}
