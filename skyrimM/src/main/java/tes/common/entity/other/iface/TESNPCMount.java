package tes.common.entity.other.iface;

public interface TESNPCMount {
	boolean getBelongsToNPC();

	void setBelongsToNPC(boolean var1);

	String getMountArmorTexture();

	boolean isMountSaddled();

	void super_moveEntityWithHeading(float var1, float var2);
}