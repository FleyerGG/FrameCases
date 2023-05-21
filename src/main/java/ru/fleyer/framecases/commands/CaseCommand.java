package ru.fleyer.framecases.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.fleyer.framecases.database.DatabaseConstructor;

public class CaseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        Player p = (Player) sender;
        DatabaseConstructor database = DatabaseConstructor.INSTANCE;
        if (!p.hasPermission("framecases.admin")){
            p.sendMessage("Net prav");
            return false;
        }

        switch (args[0]){
            case "help":{
                p.sendMessage("help ebat");
                return true;
            }

            case "give":{
                int[] cases = database.getCase(args[1], args[2]);
                // /case give Fleyer001 donate 100

                database.giveCase(args[1],args[2],Integer.parseInt(args[3]));
                p.sendMessage("§7[§6§l!§7] §fИгроку §6" + args[1] + "§f выдано §e" + args[3] + "§7 (" + cases[0] + ")§f ключей от кейса §d" + args[2]);
                return true;
            }
            case "set":{
                int[] cases = database.getCase(args[1], args[2]);
                int add = Integer.parseInt(args[3]) - cases[0];
                database.giveCase(args[1], args[2], add);
                p.sendMessage("§7[§6§l!§7] §fИгроку §6" + args[1] + "§f установлено §e" + args[3] + "§7 (" + cases[0] + ")§f ключей от кейса §d" + args[2]);
                return true;
            }
            default: p.sendMessage("help ebat");

        }

        return false;
    }
}
