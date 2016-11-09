package com.chaosthedude.souls.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chaosthedude.souls.Souls;
import com.chaosthedude.souls.entity.EntitySoul;
import com.chaosthedude.souls.util.Equipment;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class SoulsEventHandler {

	private Map<EntityPlayer, Equipment> equipmentMap = new HashMap<EntityPlayer, Equipment>();

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onDropEvent(PlayerDropsEvent event) {
		try {
			final List<ItemStack> items = new ArrayList<ItemStack>();
			for (EntityItem i : event.drops) {
				items.add(i.getEntityItem());
			}

			if (!items.isEmpty()) {
				final Equipment equipment = equipmentMap.get(event.entityPlayer);
				final EntitySoul soul = new EntitySoul(event.entityPlayer, items, equipment);
				soul.spawnInWorld();

				event.drops.clear();
			}
		} catch (Exception e) {
			Souls.logger.error("Caught an exception while trying to spawn a Soul. This is a bug! Report the following stack trace to the mod author:");
			e.printStackTrace();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingDeath(LivingDeathEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer) || event.entityLiving.worldObj.isRemote) {
			return;
		}

		final EntityPlayer player = (EntityPlayer) event.entityLiving;
		final Equipment equipment = new Equipment();

		equipment.set(Equipment.WEAPON, player.getHeldItem());
		for (int i = 0; i <= 3; i++) {
			final ItemStack stack = player.getCurrentArmor(i);
			equipment.set(Equipment.getEquipmentIndexFromPlayerArmorIndex(i), stack);
		}

		equipmentMap.put(player, equipment);
	}

}
