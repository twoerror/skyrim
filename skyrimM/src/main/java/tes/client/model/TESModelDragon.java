package tes.client.model;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class TESModelDragon extends ModelBase {
	public static final int NECK_SIZE = 10;
	public static final int TAIL_SIZE = 10;

	private static final int VERTS_NECK = 7;
	private static final int VERTS_TAIL = 12;
	private static final int HEAD_OFS = -16;

	private final ResourceLocation eggTexture;
	private final ResourceLocation bodyTexture;
	private final ResourceLocation glowTexture;
	private final ResourceLocation saddleTexture;

	private final TESModelDragonPart[] wingFinger = new TESModelDragonPart[4];

	private final TESModelDragonPartProxy[] neckProxy = new TESModelDragonPartProxy[VERTS_NECK];
	private final TESModelDragonPartProxy[] tailProxy = new TESModelDragonPartProxy[VERTS_TAIL];
	private final TESModelDragonPartProxy[] thighProxy = new TESModelDragonPartProxy[4];

	private TESModelDragonPart body;
	private TESModelDragonPart head;
	private TESModelDragonPart neck;
	private TESModelDragonPart neckScale;
	private TESModelDragonPart tail;
	private TESModelDragonPart tailHornLeft;
	private TESModelDragonPart tailHornRight;
	private TESModelDragonPart jaw;
	private TESModelDragonPart back;
	private TESModelDragonPart forethigh;
	private TESModelDragonPart forecrus;
	private TESModelDragonPart forefoot;
	private TESModelDragonPart foretoe;
	private TESModelDragonPart hindthigh;
	private TESModelDragonPart hindcrus;
	private TESModelDragonPart hindfoot;
	private TESModelDragonPart hindtoe;
	private TESModelDragonPart wingArm;
	private TESModelDragonPart wingForearm;

	private float offsetX;
	private float offsetY;
	private float offsetZ;
	private float pitch;
	private float size;
	private int renderPass = -1;

	public TESModelDragon() {
		textureWidth = 256;
		textureHeight = 256;
		bodyTexture = new ResourceLocation("tes:textures/entity/animal/dragon/body.png");
		glowTexture = new ResourceLocation("tes:textures/entity/animal/dragon/glow.png");
		saddleTexture = new ResourceLocation("tes:textures/entity/animal/dragon/saddle.png");
		eggTexture = new ResourceLocation("tes:textures/entity/animal/dragon/egg.png");
		setTextureOffset("body.body", 0, 0);
		setTextureOffset("body.scale", 0, 32);
		setTextureOffset("head.nostril", 48, 0);
		setTextureOffset("head.upperhead", 0, 0);
		setTextureOffset("head.upperjaw", 56, 88);
		setTextureOffset("head.lowerjaw", 0, 88);
		setTextureOffset("head.horn", 28, 32);
		setTextureOffset("forethigh.main", 112, 0);
		setTextureOffset("forecrus.main", 148, 0);
		setTextureOffset("forefoot.main", 210, 0);
		setTextureOffset("foretoe.main", 176, 0);
		setTextureOffset("hindthigh.main", 112, 29);
		setTextureOffset("hindcrus.main", 152, 29);
		setTextureOffset("hindfoot.main", 180, 29);
		setTextureOffset("hindtoe.main", 215, 29);
		setTextureOffset("neck.box", 112, 88);
		setTextureOffset("neck.scale", 0, 0);
		setTextureOffset("tail.box", 152, 88);
		setTextureOffset("tail.scale", 0, 0);
		setTextureOffset("tail.horn", 0, 117);
		setTextureOffset("wingarm.bone", 0, 152);
		setTextureOffset("wingarm.skin", 116, 232);
		setTextureOffset("wingfinger.bone", 0, 172);
		setTextureOffset("wingfinger.shortskin", -32, 224);
		setTextureOffset("wingfinger.skin", -49, 176);
		setTextureOffset("wingforearm.bone", 0, 164);
		buildBody();
		buildNeck();
		buildHead();
		buildTail();
		buildWing();
		buildLegs();
	}

	private static void setLivingAnimations(TESEntityDragon dragon, float partialTicks) {
		TESModelDragonAnimaton animator = dragon.getAnimator();
		animator.setPartialTicks(partialTicks);
	}

	private void buildBody() {
		body = new TESModelDragonPart(this, "body");
		body.setRotationPoint(0, 4, 8);
		body.addBox("body", -12, 0, -16, 24, 24, 64);
		body.addBox("scale", -1, -6, 10, 2, 6, 12);
		body.addBox("scale", -1, -6, 30, 2, 6, 12);
		back = body.addChildBox("scale", -1, -6, -10, 2, 6, 12);
	}

	private void buildHead() {
		head = new TESModelDragonPart(this, "head");
		head.addBox("upperjaw", -6, -1, -8 + HEAD_OFS, 12, 5, 16);
		head.addBox("upperhead", -8, -8, 6 + HEAD_OFS, 16, 16, 16);
		head.addBox("nostril", -5, -3, -6 + HEAD_OFS, 2, 2, 4);
		head.mirror = true;
		head.addBox("nostril", 3, -3, -6 + HEAD_OFS, 2, 2, 4);
		buildHorn(false);
		buildHorn(true);
		jaw = head.addChildBox("lowerjaw", -6, 0, -16, 12, 4, 16);
		jaw.setRotationPoint(0, 4, 8 + HEAD_OFS);
	}

	private void buildHorn(boolean mirror) {
		int hornThick = 3;
		int hornLength = 12;
		float hornOfs = -(hornThick / 2.0f);
		float hornPosX = -5;
		float hornPosY = -8;
		float hornPosZ = 0;
		float hornRotX = TESModelDragonAnimaton.toRadians(30);
		float hornRotY = TESModelDragonAnimaton.toRadians(-30);
		float hornRotZ = 0;
		if (mirror) {
			hornPosX *= -1;
			hornRotY *= -1;
		}
		head.mirror = mirror;
		TESModelDragonPart horn = head.addChildBox("horn", hornOfs, hornOfs, hornOfs, hornThick, hornThick, hornLength);
		horn.setRotationPoint(hornPosX, hornPosY, hornPosZ);
		horn.setAngles(hornRotX, hornRotY, hornRotZ);
	}

	private void buildLeg(boolean hind) {
		float baseLength = 26;
		String baseName = hind ? "hind" : "fore";
		float thighPosX = -11;
		float thighPosY = 18;
		float thighPosZ = 4;
		int thighThick = 9;
		int thighLength = (int) (baseLength * (hind ? 0.9f : 0.77f));
		if (hind) {
			thighThick++;
			thighPosY -= 5;
		}
		float thighOfs = -(thighThick / 2.0f);
		TESModelDragonPart thigh = new TESModelDragonPart(this, baseName + "thigh");
		thigh.setRotationPoint(thighPosX, thighPosY, thighPosZ);
		thigh.addBox("main", thighOfs, thighOfs, thighOfs, thighThick, thighLength, thighThick);
		float crusPosX = 0;
		float crusPosY = thighLength + thighOfs;
		float crusPosZ = 0;
		int crusThick = thighThick - 2;
		int crusLength = (int) (baseLength * (hind ? 0.7f : 0.8f));
		if (hind) {
			crusThick--;
			crusLength -= 2;
		}
		float crusOfs = -(crusThick / 2.0f);
		TESModelDragonPart crus = new TESModelDragonPart(this, baseName + "crus");
		crus.setRotationPoint(crusPosX, crusPosY, crusPosZ);
		crus.addBox("main", crusOfs, crusOfs, crusOfs, crusThick, crusLength, crusThick);
		thigh.addChild(crus);
		float footPosX = 0;
		float footPosY = crusLength + crusOfs / 2.0f;
		float footPosZ = 0;
		int footWidth = crusThick + 2;
		int footHeight = 4;
		int footLength = (int) (baseLength * (hind ? 0.67f : 0.34f));
		float footOfsX = -(footWidth / 2.0f);
		float footOfsY = -(footHeight / 2.0f);
		float footOfsZ = footLength * -0.75f;
		TESModelDragonPart foot = new TESModelDragonPart(this, baseName + "foot");
		foot.setRotationPoint(footPosX, footPosY, footPosZ);
		foot.addBox("main", footOfsX, footOfsY, footOfsZ, footWidth, footHeight, footLength);
		crus.addChild(foot);
		int toeLength = (int) (baseLength * (hind ? 0.27f : 0.33f));
		float toePosX = 0;
		float toePosY = 0;
		float toePosZ = footOfsZ - footOfsY / 2.0f;
		float toeOfsX = -(footWidth / 2.0f);
		float toeOfsY = -(footHeight / 2.0f);
		float toeOfsZ = -toeLength;
		TESModelDragonPart toe = new TESModelDragonPart(this, baseName + "toe");
		toe.setRotationPoint(toePosX, toePosY, toePosZ);
		toe.addBox("main", toeOfsX, toeOfsY, toeOfsZ, footWidth, footHeight, toeLength);
		foot.addChild(toe);
		if (hind) {
			hindthigh = thigh;
			hindcrus = crus;
			hindfoot = foot;
			hindtoe = toe;
		} else {
			forethigh = thigh;
			forecrus = crus;
			forefoot = foot;
			foretoe = toe;
		}
	}

	private void buildLegs() {
		buildLeg(false);
		buildLeg(true);
		for (int i = 0; i < 4; i++) {
			if (i % 2 == 0) {
				thighProxy[i] = new TESModelDragonPartProxy(forethigh);
			} else {
				thighProxy[i] = new TESModelDragonPartProxy(hindthigh);
			}
		}
	}

	private void buildNeck() {
		neck = new TESModelDragonPart(this, "neck");
		neck.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
		neckScale = neck.addChildBox("scale", -1, -7, -3, 2, 4, 6);
		for (int i = 0; i < neckProxy.length; i++) {
			neckProxy[i] = new TESModelDragonPartProxy(neck);
		}
	}

	private void buildTail() {
		tail = new TESModelDragonPart(this, "tail");
		tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
		float scaleRotZ = TESModelDragonAnimaton.toRadians(45);
		TESModelDragonPart tailScaleLeft = tail.addChildBox("scale", -1, -8, -3, 2, 4, 6).setAngles(0, 0, scaleRotZ);
		TESModelDragonPart tailScaleMiddle = tail.addChildBox("scale", -1, -8, -3, 2, 4, 6).setAngles(0, 0, 0);
		TESModelDragonPart tailScaleRight = tail.addChildBox("scale", -1, -8, -3, 2, 4, 6).setAngles(0, 0, -scaleRotZ);
		tailScaleMiddle.showModel = true;
		tailScaleLeft.showModel = false;
		tailScaleRight.showModel = false;
		buildTailHorn(false);
		buildTailHorn(true);
		for (int i = 0; i < tailProxy.length; i++) {
			tailProxy[i] = new TESModelDragonPartProxy(tail);
		}
	}

	private void buildTailHorn(boolean mirror) {
		int hornThick = 3;
		int hornLength = 32;
		float hornOfs = -(hornThick / 2.0f);
		float hornPosX = 0;
		float hornPosZ = TAIL_SIZE / 2.0f;
		float hornRotX = TESModelDragonAnimaton.toRadians(-15);
		float hornRotY = TESModelDragonAnimaton.toRadians(-145);
		float hornRotZ = 0;
		if (mirror) {
			hornPosX *= -1;
			hornRotY *= -1;
		}
		tail.mirror = mirror;
		TESModelDragonPart horn = tail.addChildBox("horn", hornOfs, hornOfs, hornOfs, hornThick, hornThick, hornLength);
		horn.setRotationPoint(hornPosX, hornOfs, hornPosZ);
		horn.setAngles(hornRotX, hornRotY, hornRotZ);
		horn.isHidden = true;
		horn.showModel = false;
		if (mirror) {
			tailHornLeft = horn;
		} else {
			tailHornRight = horn;
		}
	}

	private void buildWing() {
		wingArm = new TESModelDragonPart(this, "wingarm");
		wingArm.setRotationPoint(-10, 5, 4);
		wingArm.setRenderScale(1.1f);
		wingArm.addBox("bone", -28, -3, -3, 28, 6, 6);
		wingArm.addBox("skin", -28, 0, 2, 28, 0, 24);
		wingForearm = new TESModelDragonPart(this, "wingforearm");
		wingForearm.setRotationPoint(-28, 0, 0);
		wingForearm.addBox("bone", -48, -2, -2, 48, 4, 4);
		wingArm.addChild(wingForearm);
		wingFinger[0] = buildWingFinger(false);
		wingFinger[1] = buildWingFinger(false);
		wingFinger[2] = buildWingFinger(false);
		wingFinger[3] = buildWingFinger(true);
	}

	private TESModelDragonPart buildWingFinger(boolean small) {
		TESModelDragonPart finger = new TESModelDragonPart(this, "wingfinger");
		finger.setRotationPoint(-47, 0, 0);
		finger.addBox("bone", -70, -1, -1, 70, 2, 2);
		if (small) {
			finger.addBox("shortskin", -70, 0, 1, 70, 0, 32);
		} else {
			finger.addBox("skin", -70, 0, 1, 70, 0, 48);
		}
		wingForearm.addChild(finger);
		return finger;
	}

	public ResourceLocation getEggTexture() {
		return eggTexture;
	}

	@Override
	public void render(Entity entity, float moveTime, float moveSpeed, float ticksExisted, float lookYaw, float lookPitch, float scale) {
		render((TESEntityDragon) entity, moveTime, moveSpeed, lookYaw, lookPitch, scale);
	}

	public void render(TESEntityDragon dragon, float moveTime, float moveSpeed, float lookYaw, float lookPitch, float scale) {
		TESModelDragonAnimaton animator = dragon.getAnimator();
		animator.setMovement(moveTime, moveSpeed * dragon.getScale());
		animator.setLook(lookYaw, lookPitch);
		animator.animate(this);
		size = dragon.getScale();
		renderModel(scale);
	}

	private void renderBody(float scale) {
		body.render(scale);
	}

	private void renderHead(float scale) {
		float headScale = 1.4f / (size + 0.4f);
		head.setRenderScale(headScale);
		head.render(scale);
	}

	private void renderLegs(float scale) {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		for (int i = 0; i < thighProxy.length; i++) {
			thighProxy[i].render(scale);
			if (i == 1) {
				glScalef(-1, 1, 1);
				glCullFace(GL_FRONT);
			}
		}
		glCullFace(GL_BACK);
		glDisable(GL_CULL_FACE);
	}

	private void renderModel(float scale) {
		glPushMatrix();
		glTranslatef(offsetX, offsetY, offsetZ);
		glRotatef(-pitch, 1, 0, 0);
		if (renderPass == 0) {
			renderBody(scale);
		} else {
			renderHead(scale);
			renderNeck(scale);
			renderBody(scale);
			renderLegs(scale);
			renderTail(scale);
			renderWings(scale);
		}
		glPopMatrix();
	}

	private void renderNeck(float scale) {
		for (TESModelDragonPartProxy proxy : neckProxy) {
			proxy.render(scale);
		}
	}

	private void renderTail(float scale) {
		for (TESModelDragonPartProxy proxy : tailProxy) {
			proxy.render(scale);
		}
	}

	private void renderWings(float scale) {
		glPushMatrix();
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		for (int i = 0; i < 2; i++) {
			wingArm.render(scale);
			if (i == 0) {
				glScalef(-1, 1, 1);
				glCullFace(GL_BACK);
			}
		}
		glDisable(GL_CULL_FACE);
		glPopMatrix();
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float moveTime, float moveSpeed, float partialTicks) {
		setLivingAnimations((TESEntityDragon) entity, partialTicks);
	}

	public ResourceLocation getBodyTexture() {
		return bodyTexture;
	}

	public ResourceLocation getGlowTexture() {
		return glowTexture;
	}

	public ResourceLocation getSaddleTexture() {
		return saddleTexture;
	}

	public TESModelDragonPart getHead() {
		return head;
	}

	public TESModelDragonPart getNeck() {
		return neck;
	}

	public TESModelDragonPart getNeckScale() {
		return neckScale;
	}

	public TESModelDragonPart getTail() {
		return tail;
	}

	public TESModelDragonPart getTailHornLeft() {
		return tailHornLeft;
	}

	public TESModelDragonPart getTailHornRight() {
		return tailHornRight;
	}

	public TESModelDragonPart getJaw() {
		return jaw;
	}

	public TESModelDragonPart getBack() {
		return back;
	}

	public TESModelDragonPart getForethigh() {
		return forethigh;
	}

	public TESModelDragonPart getForecrus() {
		return forecrus;
	}

	public TESModelDragonPart getForefoot() {
		return forefoot;
	}

	public TESModelDragonPart getForetoe() {
		return foretoe;
	}

	public TESModelDragonPart getHindthigh() {
		return hindthigh;
	}

	public TESModelDragonPart getHindcrus() {
		return hindcrus;
	}

	public TESModelDragonPart getHindfoot() {
		return hindfoot;
	}

	public TESModelDragonPart getHindtoe() {
		return hindtoe;
	}

	public TESModelDragonPart getWingArm() {
		return wingArm;
	}

	public TESModelDragonPart getWingForearm() {
		return wingForearm;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setOffsetZ(float offsetZ) {
		this.offsetZ = offsetZ;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public void setRenderPass(int renderPass) {
		this.renderPass = renderPass;
	}

	public TESModelDragonPart getWingFingerItem(int i) {
		return wingFinger[i];
	}

	public TESModelDragonPartProxy getNeckProxyItem(int i) {
		return neckProxy[i];
	}

	public TESModelDragonPartProxy getTailProxyItem(int i) {
		return tailProxy[i];
	}

	public TESModelDragonPartProxy getThighProxyItem(int i) {
		return thighProxy[i];
	}

	public int getThighProxyLength() {
		return thighProxy.length;
	}

	public int getNeckProxyLength() {
		return neckProxy.length;
	}

	public int getTailProxyLength() {
		return tailProxy.length;
	}

	public int getWingFingerLength() {
		return wingFinger.length;
	}
}