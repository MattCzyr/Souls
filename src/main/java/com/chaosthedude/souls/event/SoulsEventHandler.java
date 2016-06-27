package com.chaosthedude.souls.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chaosthedude.souls.Souls;
import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.util.Equipment;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
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

	private Map<EntityPlayer, Equipment> equipmentMap = new HashMap<EntityPlayer, Equipment>();

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onDropEvent(PlayerDropsEvent event) {
		try {
			final List<ItemStack> items = new ArrayList<ItemStack>();
			for (EntityItem i : event.getDrops()) {
				items.add(i.getEntityItem());
			}

			if (!items.isEmpty()) {
				final Equipment equipment = equipmentMap.get(event.getEntityPlayer());
				final EntitySoul soul = new EntitySoul(event.getEntityPlayer(), items, equipment);
				soul.spawnInWorld(event.getEntityPlayer().worldObj);

				event.getDrops().clear();
			}
		} catch (Exception e) {
			Souls.logger.error("Caught an exception while trying to spawn a Soul. This is a bug! Report the following stack trace to the mod author:");
			e.printStackTrace();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingDeath(LivingDeathEvent event) {
		if (!(event.getEntityLiving() instanceof EntityPlayer) || event.getEntityLiving().worldObj.isRemote) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		final Equipment equipment = new Equipment();

		equipment.set(Equipment.MAINHAND, player.getHeldItemMainhand());
		for (int i = 0; i <= 3; i++) {
			final ItemStack stack = player.inventory.armorInventory[i];
			equipment.set(Equipment.getEquipmentIndexFromPlayerArmorIndex(i), stack);
		}

		equipment.set(Equipment.OFFHAND, player.getHeldItem(EnumHand.OFF_HAND));

		equipmentMap.put(player, equipment);
	}

	@SubscribeEvent
	public void onLootTableLoad(LootTableLoadEvent event) {
		if (ConfigHandler.pickpocketGauntletLoot && event.getName() == LootTableList.CHESTS_VILLAGE_BLACKSMITH) {
			LootPool main = event.getTable().getPool("main");
			if (main != null) {
				LootCondition[] chance = { new RandomChance(0.10F) };
				LootFunction[] count = { new SetCount(chance, new RandomValueRange(1.0F, 1.0F)) };
				main.addEntry(new LootEntryItem(SoulsItems.pickpocketGauntlet, 50, 1, count, chance, Souls.MODID + ":pickpocketGauntlet"));
			}
		}
	}

}
