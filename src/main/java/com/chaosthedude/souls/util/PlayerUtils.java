package com.chaosthedude.souls.util;

import java.util.UUID;

import com.chaosthedude.souls.Souls;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class PlayerUtils {

	public static String getPlayerNameFromUUID(UUID id) {
		final EntityPlayer player = getPlayerFromUUID(id);
		String name = "";
		if (player != null) {
			name = player.getCommandSenderName();
		}

		return name;
	}

	public static EntityPlayer getPlayerFromUUID(UUID id) {
		for (Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			if (o instanceof EntityPlayer) {
				final EntityPlayer player = (EntityPlayer) o;
				if (player.getUniqueID() == id) {
					return player;
				}
			}
		}

		return null;
	}

	public static void playSoundAtPlayer(EntityPlayer player, String soundSuffix) {
		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, Souls.MODID + ":" + soundSuffix, 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
	}

}
