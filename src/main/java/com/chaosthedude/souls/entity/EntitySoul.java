package com.chaosthedude.souls.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.items.ItemPickpocketGauntlet;
import com.chaosthedude.souls.util.Equipment;
import com.chaosthedude.souls.util.ItemUtils;
import com.chaosthedude.souls.util.PlayerUtils;
import com.chaosthedude.souls.util.StringUtils;
import com.chaosthedude.souls.util.Strings;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class EntitySoul extends EntityMob implements IBossDisplayData {

	public static final double MAX_HEALTH = 20.0D;
	public static final double MOVEMENT_SPEED = 0.35D;

	public List<ItemStack> items = new ArrayList<ItemStack>();

	private UUID playerID;
	private String playerName;
	private long dateCreated;

	public EntitySoul(World world) {
		super(world);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
		tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));

		if (ConfigHandler.soulsAggro) {
			targetTasks.addTask(1, new EntityAIHurtByTarget(this, ConfigHandler.allSoulsAggro));
		}

		experienceValue = 0;

		setCanPickUpLoot(false);
		setSize(0.6F, 1.8F);
	}

	public EntitySoul(EntityPlayer player, List<ItemStack> items) {
		this(player.worldObj);

		this.items = items;
		playerID = player.getGameProfile().getId();
		playerName = player.getCommandSenderName();
		dateCreated = System.currentTimeMillis();

		setupEquipment(null);
	}

	public EntitySoul(EntityPlayer player, List<ItemStack> items, Equipment equipment) {
		this(player.worldObj);

		this.items = items;
		playerID = player.getGameProfile().getId();
		playerName = player.getCommandSenderName();
		dateCreated = System.currentTimeMillis();

		setupEquipment(equipment);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(MAX_HEALTH);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MOVEMENT_SPEED);
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected void despawnEntity() {
		entityAge = 0;
	}

	@Override
	protected String getLivingSound() {
		return "mob.skeleton.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.skeleton.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.skeleton.death";
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (soulShouldDie()) {
			attackEntityFrom(DamageSource.generic, getHealth());
		}
	}

	@Override
	protected void dropEquipment(boolean par1, int par2) {};

	@Override
	protected void damageEntity(DamageSource source, float par1) {
		if (source.getSourceOfDamage() != null && source.getSourceOfDamage() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) source.getSourceOfDamage();
			if (canInteract(player)) {
				super.damageEntity(source, par1);
			}
		} else if (!ConfigHandler.blockDamage || soulShouldDie()) {
			super.damageEntity(source, par1);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		if (source.getSourceOfDamage() instanceof EntityPlayer || soulShouldDie()) {
			super.onDeath(source);

			if (!worldObj.isRemote) {
				dropItems();
			}
		}
	}

	@Override
	protected boolean interact(EntityPlayer player) {
		if (player.getHeldItem() != null) {
			if (player.getHeldItem().getItem() instanceof ItemPickpocketGauntlet) {
				final ItemPickpocketGauntlet pickpocketGauntlet = (ItemPickpocketGauntlet) player.getHeldItem().getItem();
				pickpocketGauntlet.pickpocket(player, this);

				return true;
			} else if (!player.worldObj.isRemote && player.getHeldItem().getItem() == SoulsItems.soulIdentifier) {
				PlayerUtils.playSoundAtPlayer(player, "soulIdentifier.use");
				player.addChatMessage(getSoulInfoChatComponent());

				return true;
			}
		} else if (!player.worldObj.isRemote && !ConfigHandler.requireSoulIdentifier) {
			player.addChatMessage(getSoulInfoChatComponent());

			return true;
		}

		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);

		NBTTagList inventoryList = new NBTTagList();
		NBTTagCompound tag1;

		for (ItemStack stack : items) {
			tag1 = new NBTTagCompound();

			if (stack != null) {
				stack.writeToNBT(tag1);
			}

			inventoryList.appendTag(tag1);
		}

		tag.setTag("Inventory", inventoryList);

		tag.setLong("DateCreated", dateCreated);

		tag.setString("PlayerID", playerID.toString());
		tag.setString("PlayerName", playerName);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		if (tag.hasKey("Inventory", 9)) {
			NBTTagList inventoryTagList = tag.getTagList("Inventory", 10);

			for (int i = 0; i < inventoryTagList.tagCount(); i++) {
				items.add(ItemStack.loadItemStackFromNBT(inventoryTagList.getCompoundTagAt(i)));
			}
		}

		if (tag.hasKey("DateCreated")) {
			dateCreated = tag.getLong("DateCreated");
		}

		if (tag.hasKey("PlayerID")) {
			playerID = UUID.fromString(tag.getString("PlayerID"));
		}

		if (tag.hasKey("PlayerName")) {
			playerName = tag.getString("PlayerName");
		}
	}

	public void spawnInWorld() {
		final EntityPlayer player = PlayerUtils.getPlayerFromUUID(playerID);
		if (player != null) {
			setLocationAndAngles(player.posX, player.posY, player.posZ, 0.0F, 0.0F);
			player.worldObj.spawnEntityInWorld(this);
		}
	}

	public int getNumItemsHeld() {
		int amount = items.size();
		for (int i = 0; i <= 4; i++) {
			if (getEquipmentInSlot(i) != null) {
				amount++;
			}
		}

		return amount;
	}

	public boolean hasNoItems() {
		return getNumItemsHeld() == 0;
	}

	public void clearEquipment() {
		for (int i = 0; i <= 4; i++) {
			setCurrentItemOrArmor(i, null);
		}
	}

	public boolean canInteract(EntityPlayer player) {
		if (!ConfigHandler.lockSouls || playerIsSoulOwner(player)) {
			return true;
		}

		return false;
	}

	public void removeItemInSlot(int slot) {
		final ItemStack stack = items.get(slot);
		items.remove(slot);
		for (int i = 0; i <= 4; i++) {
			final ItemStack equipment = getEquipmentInSlot(i);
			if (equipment != null && equipment.isItemEqual(stack)) {
				setCurrentItemOrArmor(i, null);
				return;
			}
		}
	}

	public long getMillisAlive() {
		return Calendar.getInstance().getTime().getTime() - dateCreated;
	}

	public boolean soulShouldExpire() {
		if (ConfigHandler.soulsExpiration >= 0.0D && ((double) getMillisAlive() / 3600000) > ConfigHandler.soulsExpiration) {
			return true;
		}

		return false;
	}

	public boolean soulShouldDie() {
		return soulShouldExpire() || hasNoItems();
	}

	protected void dropItems() {
		for (ItemStack stack : items) {
			if (stack != null) {
				entityDropItem(stack, 0.0F);
			}
		}

		clearEquipment();
	}

	protected ChatComponentText getSoulInfoChatComponent() {
		return (ChatComponentText) new ChatComponentText(parseSoulInfo()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true));
	}

	protected String parseSoulInfo() {
		return String.format(StringUtils.localize(Strings.SOUL_INFO), playerName, StringUtils.parseDate(dateCreated), getNumItemsHeld());
	}

	protected boolean playerIsSoulOwner(EntityPlayer player) {
		return PlayerUtils.getPlayerFromUUID(playerID) == player;
	}

	protected void setupArmor(Equipment equipment) {
		ItemStack armor = null;
		for (int i = 0; i <= 3; i++) {
			if (ConfigHandler.useBestEquipment) {
				armor = ItemUtils.getHighestProtectionArmor(i, items);
				setCurrentItemOrArmor(4 - i, armor);
			} else if (equipment != null) {
				armor = equipment.getEquipmentFromIndex(Equipment.getEquipmentIndexFromPlayerArmorIndex(i));
				setCurrentItemOrArmor(Equipment.getEquipmentIndexFromPlayerArmorIndex(i), armor);
			}
		}
	}

	protected void setupWeapon(Equipment equipment) {
		ItemStack weapon = null;
		if (ConfigHandler.useBestEquipment) {
			weapon = ItemUtils.getHighestDamageItem(items);
		} else if (equipment != null) {
			weapon = equipment.getEquipmentFromIndex(Equipment.WEAPON);
		}

		setCurrentItemOrArmor(Equipment.WEAPON, weapon);
	}

	private void setupEquipment(Equipment equipment) {
		if (ConfigHandler.equipItems) {
			setupArmor(equipment);
			setupWeapon(equipment);

			for (int i = 0; i <= 4; i++) {
				setEquipmentDropChance(i, 0.0F);
			}
		}
	}

}
