package tes.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.TESDamage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class TESPotionPoisonKilling extends Potion {
	public TESPotionPoisonKilling() {
		super(30, true, poison.getLiquidColor());
		setPotionName("tes.potion.drinkPoison");
		setEffectiveness(poison.getEffectiveness());
		setIconIndex(0, 0);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasStatusIcon() {
		return false;
	}

	@Override
	public boolean isReady(int tick, int level) {
		int freq = 5 >> level;
		return freq == 0 || tick % freq == 0;
	}

	@Override
	public void performEffect(EntityLivingBase entity, int level) {
		entity.attackEntityFrom(TESDamage.POISON_DRINK, 1.0f);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
		TES.proxy.renderCustomPotionEffect(x, y, effect, mc);
	}
}