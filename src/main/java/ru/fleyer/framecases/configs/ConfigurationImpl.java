package ru.fleyer.framecases.configs;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigurationImpl {
    FileConfiguration yaml();
    FileConfiguration msg();

    FileConfiguration yamlLoad();

    void save();

    void reloadConfiguration();
}