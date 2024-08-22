package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.TESBannerProtection;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESCreativeTabs;
import tes.common.entity.other.inanimate.TESEntityBanner;
import tes.common.entity.other.inanimate.TESEntityBannerWall;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class TESItemBanner extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconBase;

	@SideOnly(Side.CLIENT)
	private IIcon iconOverlay;

	public TESItemBanner() {
		setCreativeTab(TESCreativeTabs.TAB_BANNER);
		setMaxStackSize(64);
		setMaxDamage(0);
		setHasSubtypes(true);
		setFull3D();
	}

	private static BannerType getBannerType(int i) {
		return BannerType.forID(i);
	}

	public static BannerType getBannerType(ItemStack itemstack) {
		if (itemstack.getItem() instanceof TESItemBanner) {
			return getBannerType(itemstack.getItemDamage());
		}
		return null;
	}

	public static NBTTagCompound getProtectionData(ItemStack itemstack) {
		if (itemstack.getTagCompound() != null && itemstack.getTagCompound().hasKey("TESBannerData")) {
			return itemstack.getTagCompound().getCompoundTag("TESBannerData");
		}
		return null;
	}

	public static boolean hasChoiceToKeepOriginalOwner(EntityPlayer entityplayer) {
		return entityplayer.capabilities.isCreativeMode;
	}

	public static boolean isHoldingBannerWithExistingProtection(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.getHeldItem();
		if (itemstack != null && itemstack.getItem() instanceof TESItemBanner) {
			NBTTagCompound protectData = getProtectionData(itemstack);
			return protectData != null && !protectData.hasNoTags();
		}
		return false;
	}

	public static void setProtectionData(ItemStack itemstack, NBTTagCompound data) {
		if (data == null) {
			if (itemstack.getTagCompound() != null) {
				itemstack.getTagCompound().removeTag("TESBannerData");
			}
		} else {
			if (itemstack.getTagCompound() == null) {
				itemstack.setTagCompound(new NBTTagCompound());
			}
			itemstack.getTagCompound().setTag("TESBannerData", data);
		}
	}

	private static boolean shouldKeepOriginalOwnerOnPlacement(EntityPlayer entityplayer) {
		return hasChoiceToKeepOriginalOwner(entityplayer) && entityplayer.isSneaking();
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("rawtypes")
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
		NBTTagCompound protectData = getProtectionData(itemstack);
		if (protectData != null) {
			list.add(StatCollector.translateToLocal("item.tes.banner.protect"));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		EntityPlayer entityplayer = TES.proxy.getClientPlayer();
		if (pass == 0) {
			return 0x816641;
		}
		if (itemstack == entityplayer.getHeldItem()) {
			return 0xffffff;
		}
		return getBannerType(itemstack.getItemDamage()).getFaction().getEggColor();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamageForRenderPass(int i, int pass) {
		if (pass == 0) {
			return iconBase;
		}
		return iconOverlay;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("rawtypes")
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (BannerType type : BannerType.BANNER_TYPES) {
			list.add(new ItemStack(item, 1, type.getBannerID()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return getUnlocalizedName() + '.' + getBannerType(itemstack).getBannerName();
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float f, float f1, float f2) {
		int side1 = side;
		int j1 = j;
		BannerType bannerType = getBannerType(itemstack);
		NBTTagCompound protectData = getProtectionData(itemstack);
		if (world.getBlock(i, j1, k).isReplaceable(world, i, j1, k)) {
			side1 = 1;
		} else if (side1 == 1) {
			++j1;
		}
		if (side1 == 0) {
			return false;
		}
		if (side1 == 1) {
			if (!entityplayer.canPlayerEdit(i, j1, k, 1, itemstack)) {
				return false;
			}
			Block block = world.getBlock(i, j1 - 1, k);
			int meta = world.getBlockMetadata(i, j1 - 1, k);
			if (block.isSideSolid(world, i, j1 - 1, k, ForgeDirection.UP)) {
				int protectRange;
				if (TESConfig.allowBannerProtection && !entityplayer.capabilities.isCreativeMode && (protectRange = TESBannerProtection.getProtectionRange(block, meta)) > 0) {
					TESFaction faction = bannerType.getFaction();
					if (TESLevelData.getData(entityplayer).getAlignment(faction) < 1.0f) {
						if (!world.isRemote) {
							TESAlignmentValues.notifyAlignmentNotHighEnough(entityplayer, 1.0f, faction);
						}
						return false;
					}
					if (!world.isRemote && TESBannerProtection.isProtected(world, i, j1, k, TESBannerProtection.forPlayer(entityplayer, TESBannerProtection.Permission.FULL), false, protectRange)) {
						entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.alreadyProtected"));
						return false;
					}
				}
				if (!world.isRemote) {
					TESEntityBanner banner = new TESEntityBanner(world);
					banner.setLocationAndAngles(i + 0.5, j1, k + 0.5, 90.0f * (MathHelper.floor_double(entityplayer.rotationYaw * 4.0f / 360.0f + 0.5) & 3), 0.0f);
					if (world.checkNoEntityCollision(banner.boundingBox) && world.getCollidingBoundingBoxes(banner, banner.boundingBox).isEmpty() && !world.isAnyLiquid(banner.boundingBox) && world.getEntitiesWithinAABB(TESEntityBanner.class, banner.boundingBox).isEmpty()) {
						banner.setBannerType(bannerType);
						if (protectData != null) {
							banner.readProtectionFromNBT(protectData);
						}
						if (banner.getPlacingPlayer() == null || !shouldKeepOriginalOwnerOnPlacement(entityplayer)) {
							banner.setPlacingPlayer(entityplayer);
						}
						world.spawnEntityInWorld(banner);
						if (banner.isProtectingTerritory()) {
							TESLevelData.getData(entityplayer).addAchievement(TESAchievement.bannerProtect);
						}
						world.playSoundAtEntity(banner, Blocks.planks.stepSound.func_150496_b(), (Blocks.planks.stepSound.getVolume() + 1.0f) / 2.0f, Blocks.planks.stepSound.getPitch() * 0.8f);
						--itemstack.stackSize;
						return true;
					}
					banner.setDead();
				}
			}
		} else {
			if (!entityplayer.canPlayerEdit(i, j1, k, side1, itemstack)) {
				return false;
			}
			if (!world.isRemote) {
				int l = Direction.facingToDirection[side1];
				TESEntityBannerWall banner = new TESEntityBannerWall(world, i, j1, k, l);
				if (banner.onValidSurface()) {
					banner.setBannerType(bannerType);
					if (protectData != null) {
						banner.setProtectData((NBTTagCompound) protectData.copy());
					}
					world.spawnEntityInWorld(banner);
					world.playSoundAtEntity(banner, Blocks.planks.stepSound.func_150496_b(), (Blocks.planks.stepSound.getVolume() + 1.0f) / 2.0f, Blocks.planks.stepSound.getPitch() * 0.8f);
					--itemstack.stackSize;
					return true;
				}
				banner.setDead();
			}
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		iconBase = iconregister.registerIcon(getIconString() + "_base");
		iconOverlay = iconregister.registerIcon(getIconString() + "_overlay");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	public enum BannerType {
		CLEOS(244, "cleos", TESFaction.UNALIGNED), SPARROW(500, "sparrow", TESFaction.UNALIGNED), MAESTERS(501, "maesters", TESFaction.UNALIGNED), SECONDSONS(502, "secondsons", TESFaction.UNALIGNED), GOLDENCOMPANY(503, "goldencompany", TESFaction.UNALIGNED), WINDBLOWN(504, "windblown", TESFaction.UNALIGNED), STORMCROWS(505, "stormcrows", TESFaction.UNALIGNED), BRAVECOMPANIONS(506, "bravecompanions", TESFaction.UNALIGNED), WARRIORSSONS(507, "warriorsSons", TESFaction.UNALIGNED), POORFELLOWS(508, "poorFellows", TESFaction.UNALIGNED), ANDAL(509, "andal", TESFaction.UNALIGNED), SEPT(510, "sept", TESFaction.UNALIGNED), FACELESS(511, "faceless", TESFaction.UNALIGNED), BROTHERHOOD(514, "brotherhood", TESFaction.UNALIGNED), RHLLOR(515, "rhllor", TESFaction.UNALIGNED), WHITE(516, "white", TESFaction.UNALIGNED), TARGARYEN(517, "targaryen", TESFaction.UNALIGNED), TARGARYENBOOK(518, "targaryenBook", TESFaction.UNALIGNED), BLACKFYRE(519, "blackfyre", TESFaction.UNALIGNED), BLACKFYREBOOK(520, "blackfyreBook", TESFaction.UNALIGNED), BLACKS(521, "blacks", TESFaction.UNALIGNED), BLACKSBOOK(522, "blacksBook", TESFaction.UNALIGNED), GREENS(523, "greens", TESFaction.UNALIGNED), GREENSBOOK(524, "greensBook", TESFaction.UNALIGNED), AEMON(525, "aemon", TESFaction.UNALIGNED), AEMONBOOK(526, "aemonBook", TESFaction.UNALIGNED), AERION(527, "aerion", TESFaction.UNALIGNED), AERIONBOOK(528, "aerionBook", TESFaction.UNALIGNED), VALARR(529, "valarr", TESFaction.UNALIGNED), VALARRBOOK(530, "valarrBook", TESFaction.UNALIGNED), BITTERSTEEL(531, "bittersteel", TESFaction.UNALIGNED), BLOODRAVEN(532, "bloodraven", TESFaction.UNALIGNED), BLOODRAVENBOOK(533, "bloodravenBook", TESFaction.UNALIGNED), SEASTAR(534, "seastar", TESFaction.UNALIGNED), MAEKAR(535, "maekar", TESFaction.UNALIGNED), MAEKARBOOK(536, "maekarBook", TESFaction.UNALIGNED), DAEMON(537, "daemon", TESFaction.UNALIGNED), TRUEFYRE(538, "truefyre", TESFaction.UNALIGNED), DAERON(539, "daeron", TESFaction.UNALIGNED), DAERONBOOK(540, "daeronBook", TESFaction.UNALIGNED), RHAEGAR(541, "rhaegar", TESFaction.UNALIGNED), YOUNGGRIFF(542, "youngGriff", TESFaction.UNALIGNED), JONCONNINGTON(543, "jonconnington", TESFaction.UNALIGNED), UNDERLEAF(544, "underleaf", TESFaction.UNALIGNED), ROSSART(545, "rossart", TESFaction.UNALIGNED), BELAERYS(595, "belaerys", TESFaction.UNALIGNED), GALTRY(596, "galtry", TESFaction.UNALIGNED), ILLIFER(597, "illifer", TESFaction.UNALIGNED), CREIGHTON(598, "creighton", TESFaction.UNALIGNED), LEATHER(601, "leather", TESFaction.UNALIGNED), NULL(617, "null", TESFaction.UNALIGNED);

		public static final Collection<BannerType> BANNER_TYPES = new ArrayList<>();
		public static final Map<Integer, BannerType> BANNER_FOR_ID = new HashMap<>();

		static {
			for (BannerType t : values()) {
				BANNER_TYPES.add(t);
				BANNER_FOR_ID.put(t.bannerID, t);
			}
		}

		private final int bannerID;
		private final String bannerName;

		private final TESFaction faction;

		BannerType(int i, String s, TESFaction f) {
			bannerID = i;
			bannerName = s;
			faction = f;
			faction.getFactionBanners().add(this);
		}

		public static BannerType forID(int ID) {
			if (BANNER_FOR_ID.get(ID) == null) {
				return NULL;
			}
			return BANNER_FOR_ID.get(ID);
		}

		public TESFaction getFaction() {
			return faction;
		}

		public int getBannerID() {
			return bannerID;
		}

		public String getBannerName() {
			return bannerName;
		}
	}
}