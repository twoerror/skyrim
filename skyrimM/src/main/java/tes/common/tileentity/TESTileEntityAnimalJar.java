package tes.common.tileentity;

import tes.common.block.other.TESBlockAnimalJar;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.animal.TESEntityBird;
import tes.common.entity.animal.TESEntityButterfly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Random;

public class TESTileEntityAnimalJar extends TileEntity {
	private NBTTagCompound jarEntityData;
	private Entity jarEntity;
	private int ticksExisted = -1;
	private float targetYaw;
	private boolean hasTargetYaw;

	@Override
	public Packet getDescriptionPacket() {
		return getJarPacket(0);
	}

	public float getEntityBobbing(float f) {
		return MathHelper.sin((ticksExisted + f) * 0.2f) * 0.05f;
	}

	public NBTTagCompound getEntityData() {
		return jarEntityData;
	}

	public void setEntityData(NBTTagCompound nbt) {
		jarEntityData = nbt;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	private float getEntityHeight() {
		Block block = getBlockType();
		if (block instanceof TESBlockAnimalJar) {
			return ((TESBlockAnimalJar) block).getJarEntityHeight();
		}
		return 0.5f;
	}

	private float[] getInitialEntityCoords(Entity entity) {
		return new float[]{xCoord + 0.5f, yCoord + getEntityHeight() - entity.height / 2.0f, zCoord + 0.5f};
	}

	private Packet getJarPacket(int type) {
		getOrCreateJarEntity();
		NBTTagCompound data = new NBTTagCompound();
		data.setByte("JarPacketType", (byte) type);
		if (type == 0) {
			writeToNBT(data);
		} else if (type == 1) {
			data.setFloat("TargetYaw", targetYaw);
		}
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
	}

	public int getLightValue() {
		getOrCreateJarEntity();
		if (jarEntity instanceof TESEntityButterfly && ((TESEntityButterfly) jarEntity).getButterflyType() == TESEntityButterfly.ButterflyType.QOHOR) {
			return 7;
		}
		return -1;
	}

	public Entity getOrCreateJarEntity() {
		if (jarEntityData == null || jarEntityData.hasNoTags()) {
			return null;
		}
		if (jarEntity == null) {
			jarEntity = EntityList.createEntityFromNBT(jarEntityData, worldObj);
			jarEntity.ticksExisted = ticksExisted;
			float[] coords = getInitialEntityCoords(jarEntity);
			jarEntity.setLocationAndAngles(coords[0], coords[1], coords[2], jarEntity.rotationYaw, jarEntity.rotationPitch);
		}
		return jarEntity;
	}

	public boolean isEntityWatching() {
		getOrCreateJarEntity();
		return jarEntity instanceof TESEntityButterfly;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		getOrCreateJarEntity();
		NBTTagCompound data = packet.func_148857_g();
		byte type = 0;
		if (data.hasKey("JarPacketType")) {
			type = data.getByte("JarPacketType");
		}
		if (type == 0) {
			readFromNBT(packet.func_148857_g());
		} else if (type == 1) {
			targetYaw = data.getFloat("TargetYaw");
			hasTargetYaw = true;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("JarEntityData")) {
			jarEntityData = nbt.getCompoundTag("JarEntityData");
		} else if (nbt.hasKey("ButterflyData")) {
			jarEntityData = nbt.getCompoundTag("ButterflyData");
			jarEntityData.setString("id", TESEntityRegistry.getStringFromClass(TESEntityButterfly.class));
		}
		if (jarEntity != null) {
			jarEntity.readFromNBT(jarEntityData);
		}
	}

	private void sendJarPacket(int type) {
		Packet packet = getJarPacket(type);
		int i = MathHelper.floor_double(xCoord) >> 4;
		int k = MathHelper.floor_double(zCoord) >> 4;
		PlayerManager playermanager = ((WorldServer) worldObj).getPlayerManager();
		List<EntityPlayer> players = worldObj.playerEntities;
		for (EntityPlayer entityplayer : players) {
			if (!playermanager.isPlayerWatchingChunk((EntityPlayerMP) entityplayer, i, k)) {
				continue;
			}
			((EntityPlayerMP) entityplayer).playerNetServerHandler.sendPacket(packet);
		}
	}

	@Override
	public void setWorldObj(World world) {
		super.setWorldObj(world);
		if (jarEntity != null) {
			jarEntity = null;
		}
	}

	@Override
	public void updateEntity() {
		Random rand = worldObj.rand;
		super.updateEntity();
		if (ticksExisted < 0) {
			ticksExisted = rand.nextInt(100);
		}
		++ticksExisted;
		getOrCreateJarEntity();
		if (jarEntity != null) {
			jarEntity.ticksExisted = ticksExisted;
			jarEntity.lastTickPosX = jarEntity.prevPosX = jarEntity.posX;
			jarEntity.lastTickPosY = jarEntity.prevPosY = jarEntity.posY;
			jarEntity.lastTickPosZ = jarEntity.prevPosZ = jarEntity.posZ;
			jarEntity.prevRotationYaw = jarEntity.rotationYaw;
			if (jarEntity instanceof TESEntityBird) {
				((TESEntityBird) jarEntity).updateInAnimalJar();
			}
			if (!worldObj.isRemote) {
				if (jarEntity instanceof EntityLiving) {
					EntityLiving jarLiving = (EntityLiving) jarEntity;
					++jarLiving.livingSoundTime;
					if (rand.nextInt(1000) < jarLiving.livingSoundTime) {
						jarLiving.livingSoundTime = -jarLiving.getTalkInterval();
						jarLiving.playLivingSound();
					}
					if (rand.nextInt(200) == 0) {
						targetYaw = rand.nextFloat() * 360.0f;
						sendJarPacket(1);
						jarEntity.rotationYaw = targetYaw;
					}
				}
			} else if (hasTargetYaw) {
				float delta = targetYaw - jarEntity.rotationYaw;
				delta = MathHelper.wrapAngleTo180_float(delta);
				jarEntity.rotationYaw += delta * 0.1f;
				if (Math.abs(jarEntity.rotationYaw - targetYaw) <= 0.01f) {
					hasTargetYaw = false;
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		getOrCreateJarEntity();
		if (jarEntity != null && jarEntityData != null) {
			jarEntity.writeToNBTOptional(jarEntityData);
		}
		if (jarEntityData != null) {
			nbt.setTag("JarEntityData", jarEntityData);
		}
	}
}