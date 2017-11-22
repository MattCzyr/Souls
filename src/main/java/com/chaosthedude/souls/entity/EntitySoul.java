package com.chaosthedude.souls.entity;

import com.chaosthedude.souls.SoulsItems;
import com.chaosthedude.souls.config.ConfigHandler;
import com.chaosthedude.souls.items.ItemPickpocketGauntlet;
import com.chaosthedude.souls.items.ItemSoulIdentifier;
import com.chaosthedude.souls.util.Equipment;
import com.chaosthedude.souls.util.ItemUtils;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EntitySoul extends EntityMob {

	public static final double MAX_HEALTH = 20.0D;
	public static final double MOVEMENT_SPEED = 0.35D;

	public List<ItemStack> items = new ArrayList<ItemStack>();

	private UUID playerID;
	private String playerName;
	private long dateCreated;

	protected int identifierCooldown = 20;

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
		this(player.world);

		this.items = items;
		playerID = player.getGameProfile() != null ? player.getGameProfile().getId() : null;
		playerName = player.getName();
		dateCreated = System.currentTimeMillis();

		setupEquipment(null);
	}

	public EntitySoul(EntityPlayer player, List<ItemStack> items, Equipment equipment) {
		this(player.world);

		this.items = items;
		playerID = player.getGameProfile() != null ? player.getGameProfile().getId() : null;
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
		// Think this is the correct replacement for `entityAge`
		idleTime = 0;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
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
			attackEntityFrom(DamageSource.GENERIC, getHealth());
		}

		if (identifierCooldown > 0) {
			identifierCooldown--;
		}
	}

	@Override
	protected void dropEquipment(boolean par1, int par2) {
	};

	@Override
	protected void damageEntity(DamageSource source, float amount) {
		if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer) source.getTrueSource();
			if (canInteract(player)) {
				super.damageEntity(source, amount);
			}
		} else if (!ConfigHandler.blockDamage || soulShouldDie()) {
			super.damageEntity(source, amount);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);

		if (!world.isRemote) {
			dropItems();
		}
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemPickpocketGauntlet) {
			final ItemPickpocketGauntlet pickpocketGauntlet = (ItemPickpocketGauntlet) stack.getItem();
			pickpocketGauntlet.pickpocket(player, stack, this);

			return true;
		}

		if (!player.world.isRemote && identifierCooldown <= 0) {
			ItemSoulIdentifier soulIdentifier = null;
			if (!stack.isEmpty() && stack.getItem() == SoulsItems.SOUL_IDENTIFIER) {
				soulIdentifier = (ItemSoulIdentifier) stack.getItem();
			}

			if ((ConfigHandler.requireSoulIdentifier && soulIdentifier != null)
					|| !ConfigHandler.requireSoulIdentifier) {
				ItemSoulIdentifier.displaySoulInfo(player, this, soulIdentifier);
			}

			identifierCooldown = 20;

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

			if (!stack.isEmpty()) {
				stack.writeToNBT(tag1);
			}

			inventoryList.appendTag(tag1);
		}

		tag.setTag("Inventory", inventoryList);

		tag.setLong("DateCreated", dateCreated);

		if (playerID != null) {
			tag.setString("PlayerID", playerID.toString());
		}

		if (playerName != null) {
			tag.setString("PlayerName", playerName);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);

		if (tag.hasKey("Inventory", 9)) {
			NBTTagList inventoryTagList = tag.getTagList("Inventory", 10);

			for (int i = 0; i < inventoryTagList.tagCount(); i++) {
				items.add(new ItemStack(inventoryTagList.getCompoundTagAt(i)));
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
		final EntityPlayer player = playerID != null ? world.getPlayerEntityByUUID(playerID)
				: world.getPlayerEntityByName(playerName);
		if (player != null) {
			setLocationAndAngles(player.posX, player.posY, player.posZ, 0.0F, 0.0F);
			player.world.spawnEntity(this);
		}
	}

	public int getNumItemsHeld() {
		int amount = items.size();
		for (int i = 0; i <= 4; i++) {
			if (!getItemStackFromSlot(Equipment.getSlotFromEquipmentIndex(i)).isEmpty()) {
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
			setItemStackToSlot(Equipment.getSlotFromEquipmentIndex(i), ItemStack.EMPTY);
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
			if (!equipment.isEmpty() && equipment.isItemEqual(stack)) {
				setItemStackToSlot(Equipment.getSlotFromEquipmentIndex(i), ItemStack.EMPTY);
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

	public UUID getPlayerID() {
		return playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public long getDateCreatedMillis() {
		return dateCreated;
	}

	protected void dropItems() {
		for (ItemStack stack : items) {
			if (!stack.isEmpty()) {
				entityDropItem(stack, 0.0F);
			}
		}

		clearEquipment();
	}

	protected boolean playerIsSoulOwner(EntityPlayer player) {
		if (playerID != null) {
			return world.getPlayerEntityByUUID(playerID) == player;
		}

		return world.getPlayerEntityByName(playerName) == player;
	}

	protected void setupArmor(Equipment equipment) {
		ItemStack armor = ItemStack.EMPTY;
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
		ItemStack mainhand = ItemStack.EMPTY;
		ItemStack offhand = ItemStack.EMPTY;
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
