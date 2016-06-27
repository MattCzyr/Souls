package com.chaosthedude.souls.util;

import java.util.UUID;

import com.chaosthedude.souls.Souls;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayerUtils {

	public static String getPlayerNameFromUUID(World world, UUID id) {
		final EntityPlayer player = world.getPlayerEntityByUUID(id);
		String name = "";
		if (player != null) {
			name = player.getName();
		}

		return name;
	}

	public static void playSoundAtPlayer(EntityPlayer player, String soundSuffix) {
		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, Souls.MODID + ":" + soundSuffix, 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
	}

}
