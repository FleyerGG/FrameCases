package ru.fleyer.framecases;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.fleyer.framecases.database.DatabaseConstructor;

import java.sql.SQLException;
import java.util.*;

public class CaseSettings {
    public static CaseSettings INSTANCE = new CaseSettings();
    FrameCases instance = FrameCases.getInstance();
    FileConfiguration config = instance.config().yaml();
    Utils utils = Utils.INSTANCE;
    DatabaseConstructor database = DatabaseConstructor.INSTANCE;

    // maps
    public HashMap<String, String> openMenu = new HashMap<>();
    public HashMap<String, String> rouletteLoc = new HashMap<>();
    public HashMap<String, Byte> rouletteData = new HashMap<>();
    public HashMap<String, Hologram> holograms = new HashMap<>();
    public HashMap<String, List<Hologram>> prizes = new HashMap<>();
    public HashMap<String, List<String>> wins = new HashMap<>();
    public HashMap<String, String> opener = new HashMap<>();
    public HashMap<String, Integer> win = new HashMap<>();
    public HashMap<String, Integer> count = new HashMap<>();
    Color[] colors;
    {
        colors = new Color[]{Color.WHITE, Color.ORANGE, Color.FUCHSIA, Color.AQUA, Color.YELLOW, Color.LIME, Color.OLIVE, Color.SILVER, Color.GRAY, Color.NAVY, Color.PURPLE, Color.BLUE, Color.TEAL, Color.GREEN, Color.RED, Color.BLACK};
    }


    public void spawnHolo(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, () -> {
            for (String loc : rouletteLoc.keySet()){
                double addLoc = 0.5;
                String roulette = rouletteLoc.get(loc);
                Location location = new Location(Bukkit.getWorld(loc.split(",")[0]),
                                                        Integer.parseInt(loc.split(",")[1]) + addLoc ,
                                                        Integer.parseInt(loc.split(",")[2]) + addLoc,
                                                        Integer.parseInt(loc.split(",")[3]) +addLoc);
                String axis = config.getString("cases." + roulette + ".axis");
                if (location.getBlock().getTypeId() == config.getInt("cases." + roulette + ".block")){
                    List<Double> color = utils.color(config.getString("cases." + roulette + ".aura"));
                    for (int i = 0; i <= 1; i++){
                        Vector vector = new Vector(Math.sin((new Date().getTime() + (i * 1570)) / 500.0), Math.cos((new Date().getTime() + (i * 1570)) / 500.0), 0.0);
                        if (axis.equalsIgnoreCase("z")){
                            vector = new Vector(0.0, Math.cos((new Date().getTime() + (i * 1570)) / 500.0), Math.sin((new Date().getTime() + (i * 1570)) / 500.0));
                        }else if (axis.equalsIgnoreCase("y")){
                            final int radius = 5;
                            final int precision = 12;
                            for(int Si = 0; Si < precision; Si++) {
                                double p1 = (Si * Math.PI) / (precision / 2);
                                double p2 = (((Si == 0) ? precision : Si-1) * Math.PI) / (precision / 2);

                                double x1 = Math.cos(p1) * radius;
                                double x2 = Math.cos(p2) * radius;
                                double z1 = Math.sin(p1) * radius;
                                double z2 = Math.sin(p2) * radius;
                                vector = new Vector(x2-x1,0,z2-z1);

                            }
                        }
                        location.getWorld().spawnParticle(Particle.REDSTONE,location.clone().add(vector.multiply(0.7)),0, color.get(0).doubleValue(),color.get(1).doubleValue(),color.get(2).doubleValue(),1.0);
                    }
                    continue;
                }
                if (!count.containsKey(loc)) continue;
                int counts = count.get(loc);
                Hologram hologram = holograms.get(loc);
                HologramLine line = hologram.getLine(hologram.size() - 1);
                if (!(line instanceof ItemLine)) continue;
                if (counts % 2 == 0){
                    location.getWorld().spawnParticle(Particle.SPELL_WITCH,hologram.getLocation().clone().add(0.0,-2.25,0.0),1,0.0,0.0,0.0,0.0);
                }
                if (counts < 60){
                    int i;
                    if (counts % 10 == 0){
                        i = prizes.get(loc).size();
                        Vector vector = new Vector(Math.sin(i / 6.0 * Math.PI * 2.0),Math.cos(i / 6.0 * Math.PI * 2.0),0.0);
                        if (axis.equalsIgnoreCase("z")){
                            vector = new Vector(0.0,Math.cos(i / 6.0 * Math.PI * 2.0),Math.sin(i / 6.0 * Math.PI * 2.0));
                        }else if (axis.equalsIgnoreCase("y")){
                            int radius = 5;
                            int precision = 12;
                            for(int Si = 0; Si < precision; Si++) {
                                double p1 = (Si * Math.PI) / (precision / 2);
                                double p2 = (((Si == 0) ? precision : Si-1) * Math.PI) / (precision / 2);

                                double x1 = Math.cos(p1) * radius;
                                double x2 = Math.cos(p2) * radius;
                                double z1 = Math.sin(p1) * radius;
                                double z2 = Math.sin(p2) * radius;
                                vector = new Vector(x2-x1,0,z2-z1);

                            }
                        }
                        Hologram prize = HologramsAPI.createHologram(instance,hologram.getLocation().clone().add(vector.multiply(1.25)).add(0.0,-1.75,0.0));
                        String id;
                        if (i != win.get(loc)){
                            id = randomPrize(loc,roulette);
                            wins.get(loc).remove(i);
                            wins.get(loc).add(i,id);

                        }else id = wins.get(loc).get(i);
                        String[] lines = line("prizes." + id);
                        //String[] lines = config.getString("prizes." + id).split(",");
                        prize.appendTextLine(lines[0].replace("&","§"));
                        String[] itemData = lines[2].split(":");
                        prize.appendItemLine(utils.createItem(Integer.parseInt(itemData[0]),itemData.length == 2 ? Integer.parseInt(itemData[1]) : 0));
                        prizes.get(loc).add(prize);
                    }
                    i = 0;
                    for (Hologram prize : prizes.get(loc)){
                        Location prizeLoc = prize.getLocation().clone();
                        ArrayList<String> list = new ArrayList<>(wins.get(loc));
                        list.removeIf(Objects::isNull);
                        String type = list.get(i);
                        color(prizeLoc, type);
                        ++i;
                    }

                }else {
                    Location newLocation = hologram.getLocation();
                    int i = 0;
                    double t = counts - 60;
                    int prz = win.get(loc);
                    int step = 60;
                    int step2 = 140 - prz * 4 + 16;
                    int step3 = 320 - prz * 4 + 16;
                    double speed;
                    if (t >= step3) t = step3;
                    speed = t <= step ? utils.arithmeticProgress(0,1.0 / step,t) : (t <= step2 ? utils.arithmeticProgress(0,1.0 / step,step) + (t - step) : utils.arithmeticProgress(0,1.0 / step,step) + (step2 - step) + utils.arithmeticProgress(1,-1.0 / (step3 - step2), t - step2));
                    speed *= 0.25;
                    for (Hologram prize : prizes.get(loc)){



                        Vector vector = new Vector(Math.sin(((double)i + speed) / 6.0 * Math.PI * 2.0), Math.cos(((double)i + speed) / 6.0 * Math.PI * 2.0), 0.0);
                        if (axis.equalsIgnoreCase("z")){
                            vector = new Vector(0.0, Math.cos(((double)i + speed) / 6.0 * Math.PI * 2.0), Math.sin(((double)i + speed) / 6.0 * Math.PI * 2.0));
                        }else if (axis.equalsIgnoreCase("y")){
                            int radius = 5;
                            int precision = 12;
                            for(int Si = 0; Si < precision; Si++) {
                                double p1 = (Si * Math.PI) / (precision / 2);
                                double p2 = (((Si == 0) ? precision : Si-1) * Math.PI) / (precision / 2);

                                double x1 = Math.cos(p1) * radius;
                                double x2 = Math.cos(p2) * radius;
                                double z1 = Math.sin(p1) * radius;
                                double z2 = Math.sin(p2) * radius;
                                vector = new Vector(x2-x1,0,z2-z1);

                            }


                        }
                        Location tp = newLocation.clone().add(vector.multiply(1.25)).add(0.0, -1.75, 0.0);
                        prize.teleport(tp);
                        if (prize.getVisibilityManager().isVisibleByDefault()) {
                            String type = wins.get(loc).get(i);
                            color(tp, type);
                        }
                        ++i;
                    }
                    if (counts - step3 == 70){
                        String name = opener.get(loc);
                        String d = wins.get(loc).get(win.get(loc));
                        String winPrize = nameprize(d);
                        boolean bool = Boolean.parseBoolean(config.getString("prizes." + d).split(",")[4]);
                        Player p = Bukkit.getPlayer(name);
                        givePrize(name, rouletteLoc.get(loc), d, p, winPrize, bool);
                        opener.put(loc,"");
                    }else if (counts - step3 >= 90){
                        List<Hologram> list = prizes.get(loc);
                        if (counts - step3 < 150){
                            int delete = win.get(loc) - (counts - step3 - 80) / 10;
                            if (delete < 0) delete += 6;

                            Hologram hd = list.get(delete);
                            hd.getVisibilityManager().setVisibleByDefault(false);
                            hd.getVisibilityManager().resetVisibilityAll();
                        }else {
                            for (Hologram hd : list){
                                hd.delete();
                            }
                            prizes.put(loc,new ArrayList<>());
                            if (counts - step3 == 150){
                                location.clone().add(0.0,-1.0,0.0).getBlock().setType(Material.AIR);
//                                  final FallingBlock fb = location.getWorld().spawnFallingBlock(location.clone().add(0.0, -1.5, 0.0), Material.OBSIDIAN, (byte)0);
//                                  fb.setVelocity(new Vector(0.0, 0.3, 0.0));
//                                    Bukkit.getScheduler().runTaskLater(instance, () -> {
//                                        fb.remove();
//                                    },4L);

                            }else if (counts - step3 == 154){
                                location.getBlock().setTypeId(config.getInt("cases." + rouletteLoc.get(loc) + ".block"));
                                location.getBlock().setData(rouletteData.get(loc));
                                location.clone().add(0.0,-1.0,0.0).getBlock().setType(Material.AIR);
                                hologram.clearLines();
                                for (String lines : config.getStringList("cases." + rouletteLoc.get(loc) + ".holo")){
                                    hologram.appendTextLine(lines.replace("&", "§"));
                                    System.out.println("suka2");
                                }
                                wins.put(loc, new ArrayList<>());
                                count.remove(loc);
                                continue;
                            }
                        }
                    }
                }
                count.put(loc, counts + 1);
            }
        }, 0L, 1L);
    }




    private void color(Location tp, String type) {
        List<Double> color = utils.color(config.getString("prizes." + type).split(",")[1]);
        tp.getWorld().spawnParticle(Particle.REDSTONE, tp.clone().add(0.0, -0.45, 0.0), 0, color.get(0).doubleValue(), color.get(1).doubleValue(), color.get(2).doubleValue(), 1.0);
    }

    public String[] line(String path){
        String [] test = config.getString(path).split(",");
        for (String s : test){
            ChatColor.translateAlternateColorCodes('&',s);
        }
        return test;
    }

    protected String randomPrize(String loc, String roulette) {
        ArrayList<String> list = new ArrayList<>(wins.get(loc));
        list.removeIf(Objects::isNull);
        Set<String> set = config.getConfigurationSection("cases." + roulette + ".prizes").getKeys(false);
        Object[] arr = set.stream().filter(c -> !list.contains(c)).toArray();
        if (arr.length != 0) {
            int r = (int)((double)arr.length * Math.random());
            return "" + arr[r];
        }
        int r = (int)((double)set.size() * Math.random());
        return "" + set.toArray()[r];
    }

    public String nameprize(String s) {
        String[] f = instance.config().yaml().getString("prizes." + s).split(",");
        String name = f[0].replace("&", "\u00a7");
        String type = f[3];
        return instance.config().yaml().getString("format." + type.split(":")[0], name).replace("%name%", name);
    }

    public String randomChancePrize(String roulette) {
        int max = 0;
        Set<String> set = config.getConfigurationSection("cases." + roulette + ".prizes").getKeys(false);
        for (String p : set) {
            max += config.getInt("cases." + roulette + ".prizes." + p);
        }
        int random = (int)(Math.random() * (double)max);
        max = 0;
        for (String p : set) {
            if (random >= (max += config.getInt("cases." + roulette + ".prizes." + p))) continue;
            return p;
        }
        return null;
    }
    public void givePrize(String player, String roulette, String prize, Player p, String winPrize, boolean notification){
        int[] cases = database.getCase(player, roulette);
        if (cases[0] == 0) return;
        database.decKey(player,roulette);
        String prizeType = config.getString("prizes." + prize).split(",")[3];
        if (notification){
            if (p != null){
                String[] ph = new String[]{"УДАЧА!", " ТЕБЕ ПОВЕЗЛО!", "ДЖЕКПОТ!", "  ВОТ ЭТО ВЕЗЕНИЕ!", "  А ТЫ КРУТОЙ!", " МЕГА-ПРИЗ!", " ФОРТУНА 80LVL"};
                String phString = ph[(int)((double)ph.length * Math.random())];
                String s = utils.getMsg("case.title",new String[]{"%phraze%",phString,"%prize%",winPrize.replace("§l","")});
                p.sendTitle(s.split("%n%")[0],s.split("%n%")[1],5,60,10);
            }
            for (Player p2 : Bukkit.getOnlinePlayers()){
                p2.sendMessage(utils.getMsg("case.chat",new String[]{"%prize%",winPrize,"%player%", player,"%case%", config.getString("cases." + roulette + ".name")}));
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            String url = "command." + prizeType.split(":")[0];
            if (config.isString(url)){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),config.getString(url).replace("%player%",player).replace("%prize%",prizeType.split(":")[1]).replace("%prizef%",winPrize.replace("§l","")));
            }else if (config.isList(url)){
                for (String cmd : config.getStringList(url)){
                    if (cmd.replace("%player%", player).replace("%prize%", prizeType.split(":")[1]).replace("%prizef%", winPrize.replace("§l", "")).startsWith("tell: ")){
                        Objects.requireNonNull(p).sendMessage(ChatColor.translateAlternateColorCodes('&',cmd.substring(6)));
                        continue;
                    }
                    if (cmd.startsWith("title: ")){
                        String[] line = ChatColor.translateAlternateColorCodes('&',cmd.substring(7)).split(";");
                        Objects.requireNonNull(p).sendTitle(line[0], line[1], Integer.parseInt(line[2]),Integer.parseInt(line[3]),Integer.parseInt(line[4]));
                        continue;
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
                }

            }
        },1L);
        database.addCaseHistory(player,roulette,prize);
    }
    public void disable() {


        if (instance.economy != null){
            for (String loc : rouletteLoc.keySet()) {
                String roulette = rouletteLoc.get(loc);
                Location location = new Location(Bukkit.getWorld(loc.split(",")[0]), Integer.parseInt(loc.split(",")[1]), Integer.parseInt(loc.split(",")[2]), Integer.parseInt(loc.split(",")[3]));
                location.getBlock().setTypeId(config.getInt("cases." + roulette + ".block"));
                location.getBlock().setData(rouletteData.get(loc));
                location.add(0.0, -1.0, 0.0).getBlock().setType(Material.AIR);
            }
            for (Hologram hologram : HologramsAPI.getHolograms(instance)) {
                hologram.delete();
            }

        }
        try {
            if (instance.getHikari().getConnection() != null){
                instance.getHikari().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
