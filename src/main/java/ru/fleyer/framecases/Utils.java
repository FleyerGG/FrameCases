package ru.fleyer.framecases;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
   public static Utils INSTANCE = new Utils();

    public String num(long l) {
        return l < 10L ? "0" + l : "" + l;
    }

    public ItemStack createItem(int id) {
        return createItem(id, 0, 1, null, null, false);
    }

    public ItemStack createItem(int id, int data) {
        return createItem(id, data, 1, null, null, false);
    }

    public ItemStack createItem(int id, int data, String name) {
        return createItem(id, data, 1, name, null, false);
    }

    public ItemStack createItem(int id, int data, String name, List<String> lore) {
        return createItem(id, data, 1, name, lore, false);
    }

    public ItemStack createItem(int id, int data, int amount, String name, List<String> lore, boolean enchant) {
        ItemStack is = new ItemStack(id, amount, (short)0, (byte)data);
        return getItemStack(is, name, lore, enchant);
    }


    private ItemStack getItemStack(ItemStack is, String name, List<String> lore, boolean enchant) {
        ItemMeta im = is.getItemMeta();
        if (name != null) {
            im.setDisplayName(name);
        }

        if (lore != null) {
            im.setLore(lore);
        }

        if (lore != null & name != null) {
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            im.addItemFlags(ItemFlag.HIDE_DESTROYS);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        is.setItemMeta(im);
        if (enchant) {
            is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        }

        return is;
    }

    public String getMsg(String url) {
        return getMsg(url, null);
    }

    public String getMsg(String url, String[] s) {
        StringBuilder ss = new StringBuilder(FrameCases.getInstance().lang().msg().getString(url, ""));
        int i;
        if (ss.toString().equals("")) {
            ss = new StringBuilder("&c&l[!] &cНе найдено сообщение: &e" + url + "&c в файле message.yml");
            if (s != null) {
                for(i = 0; i < s.length; i += 2) {
                    ss.append("\n&c&l[!]&c Placeholder: §f").append(s[i]).append("§c заменил бы на §f").append(s[i + 1]);
                }
            }
        } else if (s != null) {
            for(i = 0; i < s.length; i += 2) {
                if (s[i] != null && s[i + 1] != null) {
                    ss = new StringBuilder(ss.toString().replace(s[i], s[i + 1]));
                }
            }
        }

        return ss.toString().replace("&", "§");
    }

    public void setInvFrame(Inventory inv) {
        ItemStack is = createItem(160, 15, "§f");

        for(int i = 0; i < inv.getSize() / 9; ++i) {
            if (i != 0 && i != inv.getSize() / 9 - 1) {
                inv.setItem(i * 9, is);
                inv.setItem(i * 9 + 8, is);
            } else {
                for(int k = 0; k < 9; ++k) {
                    inv.setItem(i * 9 + k, is);
                }
            }
        }

    }

    public double arithmeticProgress(int a, double d, double t) {
        return ((double)(2 * a) + d * (t - 1.0D)) / 2.0D * t;
    }

    public String getDate(Long time) {
        Date d = new Date(time);
        return num(d.getHours()) + ":" + num(d.getMinutes()) + " " + num(d.getDate()) + "." + num(d.getMonth() + 1) + "." + num(d.getYear() + 1900) + " по мск";
    }
    public List<Double> color(String color){
        ArrayList<Double> i = new ArrayList<>(0);
        i.add(0.0);
        i.add(0.0);
        i.add(0.0);
        switch (color){
            case "0": {
                i.set(0, 1.0E-4);
                i.set(1, 1.0E-4);
                i.set(2, 1.0E-4);
                return i;
            }
            case "1":{
                return i;
            }
            case "2":{
                i.set(0, 1.0E-4);
                i.set(1, 1.0E-4);
                i.set(2, 1.0);
                return i;
            }
            case "3":{
                i.set(0, 0.1255);
                i.set(1, 0.698);
                i.set(2, 0.6666);
                return i;
            }
            case "4":{
                i.set(0, 1.0);
                i.set(1, 1.0E-4);
                i.set(2, 1.0E-4);
                return i;
            }
            case "5":{
                i.set(0, 0.5804);
                i.set(1, 1.0E-4);
                i.set(2, 0.8274);
                return i;
            }
            case "6":{
                i.set(0, 1.0);
                i.set(1, 0.8431);
                i.set(2, 1.0E-4);
                return i;
            }
            case "7":{
                i.set(0, 0.8118);
                i.set(1, 0.8118);
                i.set(2, 0.8118);
                return i;
            }
            case "8":{
                i.set(0, 0.4118);
                i.set(1, 0.4118);
                i.set(2, 0.4118);
                return i;
            }
            case "9":{
                i.set(0, 0.2549);
                i.set(1, 0.4118);
                i.set(2, 1.0);
                return i;
            }
            case "a":{
                i.set(0, 1.0E-4);
                i.set(1, 1.0);
                i.set(2, 1.0E-4);
                return i;
            }
            case "b":{
                i.set(0, 1.0E-4);
                i.set(1, 1.0);
                i.set(2, 1.0);
                return i;
            }
            case "c":{
                i.set(0, 0.8039);
                i.set(1, 0.3608);
                i.set(2, 0.3608);
                return i;
            }
            case "d":{
                i.set(0, 1.0);
                i.set(1, 0.4118);
                i.set(2, 0.7059);
                return i;
            }
            case "e":{
                i.set(0, 1.0);
                i.set(1, 1.0);
                i.set(2, 1.0E-4);
                return i;
            }
            case "f":{
                i.set(0, 1.0);
                i.set(1, 1.0);
                i.set(2, 1.0);
                return i;
            }
            default: return i;
        }
    }

}

