package com.chaosthedude.souls.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.chaosthedude.souls.Souls;
import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.util.Equipment;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SoulsEventHandler {

	private Map<UUID, Equipment> equipmentMap = new HashMap<UUID, Equipment>();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onDropEvent(PlayerDropsEvent event) {
		try {
			final List<ItemStack> items = new ArrayList<ItemStack>();
			for (EntityItem i : event.getDrops()) {
				items.add(i.getItem());
			}

			if (!items.isEmpty()) {
				final Equipment equipment = equipmentMap.get(event.getEntityPlayer().getGameProfile().getId());
				for (int i = 0; i <= 5; i++) {
					if (!items.contains(equipment.getEquipmentFromIndex(i))) {
						equipment.set(i, ItemStack.EMPTY);
					}
				}
				final EntitySoul soul = new EntitySoul(event.getEntityPlayer(), items, equipment);
				soul.spawnInWorld(event.getEntityPlayer().world);

				event.getDrops().clear();
			}
		} catch (Exception e) {
			Souls.logger.error(
					"Caught an exception while trying to spawn a Soul. This is a bug! Report the following stack trace to the mod author:");
			e.printStackTrace();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingDeath(LivingDeathEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().world.isRemote) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		final Equipment equipment = new Equipment();

		equipment.set(Equipment.MAINHAND, player.getHeldItemMainhand());

		for (int i = 0; i <= 3; i++) {
			final ItemStack stack = player.inventory.armorInventory.get(i);
			equipment.set(Equipment.getEquipmentIndexFromPlayerArmorIndex(i), stack);
		}

		equipment.set(Equipment.OFFHAND, player.getHeldItemOffhand());

		equipmentMap.put(player.getGameProfile().getId(), equipment);
	}

}
