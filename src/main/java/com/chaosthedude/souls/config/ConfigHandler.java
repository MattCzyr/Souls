package com.chaosthedude.souls.config;

import java.io.File;

import com.chaosthedude.souls.Souls;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ConfigHandler {

	public static Configuration config;

	public static boolean allSoulsAggro = false;
	public static boolean blockDamage = false;
	public static boolean equipItems = true;
	public static boolean lockSouls = false;
	public static boolean pickpocketGauntletAggros = true;
	public static boolean pickpocketGauntletLoot = true;
	public static boolean requireSoulIdentifier = true;
	public static boolean soulsAggro = true;
	public static boolean useBestEquipment = false;
	public static double pickpocketSuccessRate = 20.0D;
	public static double soulsExpiration = -1.0D;

	public static void loadConfig(File configFile) {
		config = new Configuration(configFile);

		config.load();
		init();

		FMLCommonHandler.instance().bus().register(new ChangeListener());
	}

	public static void init() {
		String comment;

		comment = "Set this to true to make all Souls in a 16 block radius aggro when you attack one. 'souls.aggro' must be true.";
		allSoulsAggro = loadBool("souls.aggroAll", comment, allSoulsAggro);

		comment = "Set this to true to prevent all damage to Souls except for damage dealt by players.";
		blockDamage = loadBool("souls.preventDamage", comment, blockDamage);

		comment = "Set this to false to disable Souls equipping armor and weapons from the player's inventory.";
		equipItems = loadBool("souls.equipItems", comment, equipItems);

		comment = "Set this to true to lock Souls so that they cannot be interacted with by anyone except their owner.";
		lockSouls = loadBool("souls.lock", comment, lockSouls);

		comment = "Set this to false to disable failed Pickpocket's Gauntlet uses from aggroing Souls. If this is set to true, 'souls.aggro' must also be true for it to take effect.";
		pickpocketGauntletAggros = loadBool("souls.pickpocket.aggros", comment, pickpocketGauntletAggros);

		comment = "Set this to false to disable the Pickpocket's Gauntlet from being found as blacksmiths' loot in villages. This will add a recipe for it.";
		pickpocketGauntletLoot = loadBool("souls.pickpocketGauntlet.loot", comment, pickpocketGauntletLoot);

		comment = "Set this to false to disable the need for a Soul Identifier to view information about a Soul; with this set to false you'll be able to use an empty hand to view the information.";
		requireSoulIdentifier = loadBool("souls.requireSoulIdentifier", comment, requireSoulIdentifier);

		comment = "Set this to false to disable Souls from aggroing when attacked. In other words, set this to false to make Souls completely passive.";
		soulsAggro = loadBool("souls.aggro", comment, soulsAggro);

		comment = "Set this to true to enable Souls equipping the highest damage weapon and highest protection armor in their loot rather than the equipment the player was using, if any. 'souls.equipItems' must be true.";
		useBestEquipment = loadBool("souls.useBestEquipment", comment, useBestEquipment);

		comment = "The success rate for the Pickpocket's Gauntlet. Ex: 20.0 equates to a 20% success rate.";
		pickpocketSuccessRate = loadDouble("souls.pickpocket.successRate", comment, pickpocketSuccessRate);

		comment = "The amount of time, in hours, after which Souls will automatically die and drop their items. Set this to a negative value to make Souls never automatically die.";
		soulsExpiration = loadDouble("souls.expireAfter", comment, soulsExpiration);

		if (config.hasChanged()) {
			config.save();
		}
	}

	public static boolean loadBool(String name, String comment, boolean def) {
		Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		prop.comment = comment;
		return prop.getBoolean(def);
	}

	public static double loadDouble(String name, String comment, double def) {
		Property prop = config.get(Configuration.CATEGORY_GENERAL, name, def);
		prop.comment = comment;
		double val = prop.getDouble(def);
		if (val == 0) {
			val = def;
			prop.set(def);
		}

		return val;
	}

	public static class ChangeListener {
		@SubscribeEvent
		public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if (eventArgs.modID.equals(Souls.MODID)) {
				init();
			}
		}
	}

}
