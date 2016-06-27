package com.chaosthedude.souls;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoulsSounds {

	// TODO: Fix sounds

	public static SoundEvent identifier;
	public static SoundEvent pickpocket;

	public static void register() {
		registerSound(identifier, "identifier");
		registerSound(pickpocket, "pickpocket");
	}

	private static void registerSound(SoundEvent sound, String soundName) {
		final ResourceLocation soundID = new ResourceLocation(Souls.MODID, soundName);
		sound = new SoundEvent(soundID).setRegistryName(soundID);
		GameRegistry.register(sound);
	}

}
