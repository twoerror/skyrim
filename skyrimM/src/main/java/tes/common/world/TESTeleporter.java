package tes.common.world;

import tes.TES;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.entity.other.inanimate.TESEntityPortal;
import tes.common.world.map.TESWaypoint;
import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TESTeleporter extends Teleporter {
	private final WorldServer world;
	private final boolean makeRingPortal;

	public TESTeleporter(WorldServer worldserver, boolean flag) {
		super(worldserver);
		world = worldserver;
		makeRingPortal = flag;
	}

	@Override
	public void placeInPortal(Entity entity, double d, double d1, double d2, float f) {
		int k;
		int i;
		int j;
		i = TESLevelData.getOverworldPortalX();
		k = TESLevelData.getOverworldPortalZ();
		j = TESLevelData.getOverworldPortalY();
		
		entity.setLocationAndAngles(i + 0.5, j + 1.0, k + 0.5, entity.rotationYaw, 0.0f);
		if (world.provider.dimensionId == TESDimension.GAME_OF_THRONES.getDimensionID() && TESLevelData.getMadeGameOfThronesPortal() == 0) {
			TESLevelData.setMadeGameOfThronesPortal(1);
			if (makeRingPortal) {
				if (world.provider instanceof TESWorldProvider) {
					TESWorldProvider.setRingPortalLocation(i, j, k);
				}
				TESEntityPortal portal = new TESEntityPortal(world);
				portal.setLocationAndAngles(i + 0.5, j + 3.5, k + 0.5, 0.0f, 0.0f);
				world.spawnEntityInWorld(portal);
			}
		}
	}
}