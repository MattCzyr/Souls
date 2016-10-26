package com.chaosthedude.souls.client;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SoulsSounds {

	public static void register() {
		registerSound("identifier");
		registerSound("pickpocket");
	}

	public static SoundEvent getSoundEvent(String name) {
		return SoundEvent.REGISTRY.getObject(new ResourceLocation("souls", name));
	}

	private static void registerSound(String soundName) {
		final ResourceLocation soundID = new ResourceLocation("souls", soundName);
		final SoundEvent sound = new SoundEvent(soundID);
		GameRegistry.register(sound, soundID);
	}

}
