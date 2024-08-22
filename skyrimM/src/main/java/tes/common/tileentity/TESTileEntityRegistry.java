package tes.common.tileentity;

import cpw.mods.fml.common.registry.GameRegistry;

public class TESTileEntityRegistry {
	private TESTileEntityRegistry() {
	}

	public static void onInit() {
		GameRegistry.registerTileEntity(TESTileEntityBeacon.class, "TESBeacon");
		GameRegistry.registerTileEntity(TESTileEntityOven.class, "TESOven");
		GameRegistry.registerTileEntity(TESTileEntityPlate.class, "TESPlate");
		GameRegistry.registerTileEntity(TESTileEntityFlowerPot.class, "TESFlowerPot");
		GameRegistry.registerTileEntity(TESTileEntitySpawnerChest.class, "TESSpawnerChest");
		GameRegistry.registerTileEntity(TESTileEntityBarrel.class, "TESBarrel");
		GameRegistry.registerTileEntity(TESTileEntityArmorStand.class, "TESArmorStand");
		GameRegistry.registerTileEntity(TESTileEntityMug.class, "TESMug");
		GameRegistry.registerTileEntity(TESTileEntityCommandTable.class, "TESCommandTable");
		GameRegistry.registerTileEntity(TESTileEntityAnimalJar.class, "TESButterflyJar");
		GameRegistry.registerTileEntity(TESTileEntityUnsmeltery.class, "TESUnsmeltery");
		GameRegistry.registerTileEntity(TESTileEntityAlloyForge.class, "TESAlloyForge");
		GameRegistry.registerTileEntity(TESTileEntitySarbacaneTrap.class, "TESSarbacaneTrap");
		GameRegistry.registerTileEntity(TESTileEntityChest.class, "TESChest");
		GameRegistry.registerTileEntity(TESTileEntityWeaponRack.class, "TESWeaponRack");
		GameRegistry.registerTileEntity(TESTileEntityKebabStand.class, "TESKebabStand");
		GameRegistry.registerTileEntity(TESTileEntitySignCarved.class, "TESSignCarved");
		GameRegistry.registerTileEntity(TESTileEntitySignCarvedValyrian.class, "TESSignCarvedValyrian");
		GameRegistry.registerTileEntity(TESTileEntityMillstone.class, "TESMillstone");
		GameRegistry.registerTileEntity(TESTileEntityBookshelf.class, "TESBookshelf");
	}
}