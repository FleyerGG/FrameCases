package ru.fleyer.framecases.guis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.fleyer.framecases.CaseSettings;
import ru.fleyer.framecases.FrameCases;
import ru.fleyer.framecases.Utils;
import ru.fleyer.framecases.database.DatabaseConstructor;
import ru.fleyer.framecases.logs.LogData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainGui {
    public static MainGui INSTANCE = new MainGui();
    FrameCases instance = FrameCases.getInstance();
    Utils utils = Utils.INSTANCE;
    DatabaseConstructor database = DatabaseConstructor.INSTANCE;
    CaseSettings settings = CaseSettings.INSTANCE;
    FileConfiguration config = instance.config().yaml();


    public String cfg (String path){
        return ChatColor.translateAlternateColorCodes('&',FrameCases.getInstance().config().yaml().getString(path));
    }
    public void menu(Player p, String location, String roulette){
        Inventory inv = Bukkit.createInventory(null,54, cfg("cases." + roulette + ".title"));
        // заполнитель инва
        utils.setInvFrame(inv);
        int[] cases = database.getCase(p.getName(),roulette);
        String prize = "";
        for (String s : instance.config().yaml().getConfigurationSection("cases." + roulette + ".prizes").getKeys(false)){
            prize = prize + "&7 - &f" + settings.nameprize(s) + "\n";
        }
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(utils.getMsg("case.menu." + roulette + ".list", new String[]{"%list%", prize}).split("\n")));
        ArrayList<String> arrayList2 = new ArrayList<>(Arrays.asList(utils.getMsg("case.menu." + roulette + ".open", new String[]{"%keys%", String.valueOf(cases[0]), "%open%", String.valueOf(cases[1])}).split("\n")));
        String name = arrayList.get(0);
        arrayList.remove(0);
        inv.setItem(20,utils.createItem(381,0,name,arrayList));

        String name2 = arrayList2.get(0);
        arrayList2.remove(0);
        inv.setItem(22,utils.createItem(config.getInt("cases." + roulette + ".block"),0, name2,arrayList2));
        arrayList2 = new ArrayList<>(Arrays.asList(utils.getMsg("case.menu." + roulette + ".help").split("\n")));
        name2 = arrayList2.get(0);
        inv.setItem(24,utils.createItem(386,0,name2,arrayList2));
        List<LogData> history = database.getCaseHistory(roulette);
        for (int i = 0; i < 5; i++) {
            if (i < history.size()) {
                String[] arrString;
                LogData logData = history.get(i);
                ArrayList<String> arrayList3 = new ArrayList<>(Arrays.asList(utils.getMsg("case.history.exist", new String[]{"%prize%", settings.nameprize(logData.getPrize()), "%date%", utils.getDate(logData.getDate()), "%player%", logData.getPlayerName()}).split("\n")));
                String name3 = arrayList3.get(0);
                arrayList3.remove(0);
                if (config.contains("prizes." + logData.getPrize())) {
                    arrString = config.getString("prizes." + logData.getPrize()).split(",")[2].split(":");
                } else {
                    String[] arrString2 = new String[2];
                    arrString2[0] = "251";
                    arrString = arrString2;
                    arrString2[1] = "7";
                }
                String[] itemData = arrString;
                inv.setItem(38 + i, utils.createItem(Integer.parseInt(itemData[0]), itemData.length == 2 ? Integer.parseInt(itemData[1]) : 0, name3, arrayList3));
                continue;
            }
            ArrayList<String> arrayList4 = new ArrayList<>(Arrays.asList(utils.getMsg("case.history.foundnot").split("\n")));
            String name4 = arrayList4.get(0);
            arrayList4.remove(0);
            inv.setItem(38 + i, utils.createItem(389, 0, name4, arrayList4));
        }

        ItemStack o = utils.createItem(160,7,"");
        inv.setItem(37,o);
        inv.setItem(43,o);

        p.openInventory(inv);
        settings.openMenu.put(p.getName(),location);


    }

}