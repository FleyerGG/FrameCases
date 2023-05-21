package ru.fleyer.framecases.Events;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.fleyer.framecases.CaseSettings;
import ru.fleyer.framecases.FrameCases;
import ru.fleyer.framecases.Utils;
import ru.fleyer.framecases.database.DatabaseConstructor;
import ru.fleyer.framecases.guis.MainGui;

import java.util.Arrays;

public class CaseEvents implements Listener {
    FrameCases instance = FrameCases.getInstance();
    Utils utils = Utils.INSTANCE;
    CaseSettings settings = CaseSettings.INSTANCE;
    FileConfiguration config = instance.config().yaml();

    @EventHandler
    public void clicks(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        if (settings.openMenu.containsKey(p.getName())){
            e.setCancelled(true);
            if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.CHEST){
                String loc = settings.openMenu.get(p.getName());
                if (e.getSlot() == 22){
                    p.closeInventory();
                    Location location = new Location(Bukkit.getWorld(loc.split(",")[0]),
                            Integer.parseInt(loc.split(",")[1]),
                            Integer.parseInt(loc.split(",")[2]),
                            Integer.parseInt(loc.split(",")[3]));
                    if (settings.count.containsKey(loc)){
                        p.sendMessage(utils.getMsg("case.openning"));
                        return;
                    }
                    String roulette = settings.rouletteLoc.get(loc);
                    int[] cases = DatabaseConstructor.INSTANCE.getCase(p.getName(),roulette);
                    if (cases[0] <= 0){
                        p.sendMessage(utils.getMsg("case.zerokeys"));
                        return;
                    }
                    settings.count.put(loc,0);
                    location.getBlock().setType(Material.AIR);
//                  FallingBlock fb = location.getWorld().spawnFallingBlock(location.clone().add(0.5, 0.0, 0.5), Material.OBSIDIAN, (byte)0);
//                  fb.setGravity(true);
                    Hologram hologram = settings.holograms.get(loc);
                    hologram.clearLines();
                    settings.holograms.get(loc).clearLines();
                    hologram.appendTextLine("§fОткрывает: §e" + p.getName());
                    hologram.appendTextLine("§7§lV");
                    hologram.appendTextLine("");
                    hologram.appendTextLine("");
                    hologram.appendTextLine("");
                    hologram.appendTextLine("");
                    hologram.appendTextLine("");

                    hologram.appendTextLine(cfg("cases." + roulette + ".name"));
                    hologram.appendItemLine(Utils.INSTANCE.createItem(config.getInt("cases." + roulette + ".block")));

                    int i = (int) (Math.random() * 6.0);
                    settings.win.put(loc,i);
                    settings.wins.get(loc).clear();
                    settings.wins.get(loc).addAll(Arrays.asList(null,null,null,null,null));
                    settings.wins.get(loc).add(i, settings.randomChancePrize(settings.rouletteLoc.get(loc)));
                    settings.opener.put(loc,p.getName());
                }
            }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        settings.openMenu.remove(p.getName());
    }

    @EventHandler
    public void openCase(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location location = e.getClickedBlock().getLocation().clone();
            String line = location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();

            if (settings.rouletteLoc.containsKey(line)){

                e.setCancelled(true);
                MainGui.INSTANCE.menu(e.getPlayer(),line,settings.rouletteLoc.get(line));
            }
        }

    }


    public String cfg (String path){
        return ChatColor.translateAlternateColorCodes('&',FrameCases.getInstance().config().yaml().getString(path));
    }
}
