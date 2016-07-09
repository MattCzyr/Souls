package com.chaosthedude.souls.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.SoulsSounds;
import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.items.ItemPickpocketGauntlet;
import com.chaosthedude.souls.util.Equipment;
import com.chaosthedude.souls.util.ItemUtils;
import com.chaosthedude.souls.util.PlayerUtils;
import com.chaosthedude.souls.util.StringUtils;
import com.chaosthedude.souls.util.Strings;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class EntitySoul extends EntityMob {

	public static final double MAX_HEALTH = 20.0D;
	public static final double MOVEMENT_SPEED = 0.35D;

	public List<ItemStack> items = new ArrayList<ItemStack>();

	private UUID playerID;
	private String playerName;
	private long dateCreated;

	public EntitySoul(World world) {
		super(world);

		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, true));
		tasks.addTask(2, new EntityAIMoveTowardsRestriction(this, 1.0D));
		tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));

		if (ConfigHandler.soulsAggro) {
			targetTasks.addTask(1, new EntityAIHurtByTarget(this, ConfigHandler.allSoulsAggro));
		}

		experienceValue = 0;

		setCanPickUpLoot(false);
		setSize(0.6F, 2.0F);
	}

	public EntitySoul(EntityPlayer player, List<ItemStack> items) {
		this(player.worldObj);

		this.items = items;
		playerID = player.getGameProfile().getId();
		playerName = player.getName();
		dateCreated = System.currentTimeMillis();

		setupEquipment(null);
	}

	public EntitySoul(EntityPlayer player, List<ItemStack> items, Equipment equipment) {
		this(player.worldObj);

		this.items = items;
		playerID = player.getGameProfile().getId();
		playerName = player.getName();
		dateCreated = System.currentTimeMillis();

		setupEquipment(equipment);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
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
	protected SoundEvent getHurtSound() {
		return SoundEvents.ENTITY_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SKELETON_DEATH;
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
		super.onDeath(source);

		if (!worldObj.isRemote) {
			dropItems();
		}
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (stack != null) {
			if (stack.getItem() instanceof ItemPickpocketGauntlet) {
				final ItemPickpocketGauntlet pickpocketGauntlet = (ItemPickpocketGauntlet) stack.getItem();
				pickpocketGauntlet.pickpocket(player, stack, this);

				return true;
			} else if (!player.worldObj.isRemote && stack.getItem() == SoulsItems.soulIdentifier) {
				PlayerUtils.playSoundAtPlayer(player, SoulsSounds.identifier);
				player.addChatMessage(getSoulInfoTextComponent());

				return true;
			}
		} else if (!player.worldObj.isRemote && !ConfigHandler.requireSoulIdentifier) {
			player.addChatMessage(getSoulInfoTextComponent());

			return true;
		}

		return false;
	}

	@Override
	public boolean isNonBoss() {
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

	public void spawnInWorld(World world) {
		final EntityPlayer player = world.getPlayerEntityByUUID(playerID);
		if (player != null) {
			setLocationAndAngles(player.posX, player.posY, player.posZ, 0.0F, 0.0F);
			player.worldObj.spawnEntityInWorld(this);
		}
	}

	public int getNumItemsHeld() {
		int amount = items.size();
		for (int i = 0; i <= 4; i++) {
			if (getItemStackFromSlot(Equipment.getSlotFromEquipmentIndex(i)) != null) {
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
			setItemStackToSlot(Equipment.getSlotFromEquipmentIndex(i), null);
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
			final ItemStack equipment = getItemStackFromSlot(Equipment.getSlotFromEquipmentIndex(i));
			if (equipment != null && equipment.isItemEqual(stack)) {
				setItemStackToSlot(Equipment.getSlotFromEquipmentIndex(i), null);
				return;
			}
		}
	}

	public long getMillisAlive() {
		return Calendar.getInstance().getTime().getTime() - dateCreated;
	}

	public boolean soulShouldExpire() {
		if (ConfigHandler.soulsExpiration >= 0.0D
				&& ((double) getMillisAlive() / 3600000) > ConfigHandler.soulsExpiration) {
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

	protected TextComponentString getSoulInfoTextComponent() {
		final TextComponentString soulInfo = new TextComponentString(parseSoulInfo());
		soulInfo.setStyle(new Style().setItalic(true).setColor(TextFormatting.GRAY));
		return soulInfo;
	}

	protected String parseSoulInfo() {
		return StringUtils.localize(Strings.SOUL_INFO, playerName, StringUtils.parseDate(dateCreated),
				getNumItemsHeld());
	}

	protected boolean playerIsSoulOwner(EntityPlayer player) {
		return worldObj.getPlayerEntityByUUID(playerID) == player;
	}

	protected void setupArmor(Equipment equipment) {
		ItemStack armor = null;
		for (int i = 0; i <= 3; i++) {
			if (ConfigHandler.useBestEquipment) {
				armor = ItemUtils.getHighestProtectionArmor(Equipment.getSlotFromEquipmentIndex(i), items);
				setItemStackToSlot(Equipment.getSlotFromEquipmentIndex(4 - i), armor);
			} else if (equipment != null) {
				armor = equipment.getEquipmentFromIndex(Equipment.getEquipmentIndexFromPlayerArmorIndex(i));
				setItemStackToSlot(
						Equipment.getSlotFromEquipmentIndex(Equipment.getEquipmentIndexFromPlayerArmorIndex(i)), armor);
			}
		}
	}

	protected void setupWeapon(Equipment equipment) {
		ItemStack mainhand = null;
		ItemStack offhand = null;
		if (ConfigHandler.useBestEquipment) {
			mainhand = ItemUtils.getHighestDamageItemForHand(EntityEquipmentSlot.MAINHAND, items);
		} else if (equipment != null) {
			mainhand = equipment.getEquipmentFromIndex(Equipment.MAINHAND);
			offhand = equipment.getEquipmentFromIndex(Equipment.OFFHAND);
		}

		setItemStackToSlot(EntityEquipmentSlot.MAINHAND, mainhand);
		setItemStackToSlot(EntityEquipmentSlot.OFFHAND, offhand);
	}

	private void setupEquipment(Equipment equipment) {
		if (ConfigHandler.equipItems) {
			setupArmor(equipment);
			setupWeapon(equipment);

			for (int i = 0; i <= 4; i++) {
				setDropChance(Equipment.getSlotFromEquipmentIndex(i), 0.0F);
			}
		}
	}

}
