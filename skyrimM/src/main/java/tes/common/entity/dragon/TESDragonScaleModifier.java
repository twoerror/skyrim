package tes.common.entity.dragon;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class TESDragonScaleModifier extends AttributeModifier {
	private static final UUID ID = UUID.fromString("856d4ba4-9ffe-4a52-8606-890bb9be538b");

	public TESDragonScaleModifier() {
		super(ID, "Dragon size modifier", 0, 1);
		setSaved(false);
	}
}