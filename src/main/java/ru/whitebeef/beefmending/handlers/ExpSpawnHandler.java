package ru.whitebeef.beefmending.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import ru.whitebeef.beeflibrary.utils.ScheduleUtils;
import ru.whitebeef.beefmending.BeefMending;
import ru.whitebeef.beefmending.utils.StreamUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

public class ExpSpawnHandler implements Listener {

    public ExpSpawnHandler() {
        ScheduleUtils.scheduleSyncRepeatingTask(BeefMending.getInstance(), () ->
                Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(ExperienceOrb.class)
                        .forEach(this::collectExperience)), 20L, 20L);
    }

    @EventHandler
    public void onExpSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof ExperienceOrb experienceOrb)) {
            return;
        }
        ScheduleUtils.runTask(() -> collectExperience(experienceOrb));
    }

    private void collectExperience(ExperienceOrb experienceOrb) {
        Location location = experienceOrb.getLocation();
        Stream<? extends Entity> entitiesStream = StreamUtils.contact(
                        location.getNearbyEntitiesByType(ArmorStand.class, 3).stream(),
                        location.getNearbyEntitiesByType(Item.class, 3).stream(),
                        location.getNearbyEntitiesByType(ItemFrame.class, 3).stream(),
                        location.getNearbyEntitiesByType(Fox.class, 3).stream())
                .sorted(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));

        entitiesStream.forEach(entity -> {
            if (experienceOrb.isDead()) {
                return;
            }
            if (experienceOrb.getExperience() == 0) {
                experienceOrb.remove();
                return;
            }
            if (entity instanceof ArmorStand armorStand) {
                Arrays.stream(EquipmentSlot.values()).forEach(equipmentSlot -> {
                    ItemStack itemStack = armorStand.getEquipment().getItem(equipmentSlot);
                    if (mendingItemNaturally(itemStack, experienceOrb)) {
                        armorStand.setItem(equipmentSlot, itemStack);
                    }
                });
                return;
            }
            if (entity instanceof ItemFrame itemFrame) {
                ItemStack itemStack = itemFrame.getItem();
                if (mendingItemNaturally(itemStack, experienceOrb)) {
                    itemFrame.setItem(itemStack);
                }
                return;
            }
            if (entity instanceof Item item) {
                ItemStack itemStack = item.getItemStack();
                mendingItemNaturally(itemStack, experienceOrb);
                item.setItemStack(itemStack);
                return;
            }
            if (entity instanceof Fox fox) {
                ItemStack itemStack = fox.getEquipment().getItemInMainHand();
                if (mendingItemNaturally(itemStack, experienceOrb)) {
                    fox.getEquipment().setItemInMainHand(itemStack);
                }
                return;
            }
        });

    }

    private boolean mendingItemNaturally(ItemStack itemStack, ExperienceOrb experienceOrb) {
        if (itemStack.getItemMeta() instanceof Damageable damageable) {
            if (!itemStack.getItemMeta().hasEnchant(Enchantment.MENDING)) {
                return false;
            }
            int toAdd = Math.min(damageable.getDamage(), experienceOrb.getExperience());
            if (toAdd == 0) {
                return false;
            }
            damageable.setDamage(Math.max(damageable.getDamage() - toAdd * 2, 0));
            experienceOrb.setExperience(experienceOrb.getExperience() - toAdd);
            itemStack.setItemMeta(damageable);
            return true;
        }
        return false;
    }


}
