package tes.client.model;

import java.util.ArrayList;
import java.util.List;

public class TESModelDragonPartProxy {
	private final List<TESModelDragonPartProxy> childs;

	private final TESModelDragonPart part;

	private boolean hidden;
	private boolean showModel;

	private float renderScaleX = 1;
	private float renderScaleY = 1;
	private float renderScaleZ = 1;
	private float rotationPointX;
	private float rotationPointY;
	private float rotationPointZ;
	private float preRotateAngleX;
	private float preRotateAngleY;
	private float preRotateAngleZ;
	private float rotateAngleX;
	private float rotateAngleY;
	private float rotateAngleZ;

	public TESModelDragonPartProxy(TESModelDragonPart part) {
		this.part = part;
		if (part.childModels != null) {
			childs = new ArrayList<>();
			for (Object childModel : part.childModels) {
				childs.add(new TESModelDragonPartProxy((TESModelDragonPart) childModel));
			}
		} else {
			childs = null;
		}
		update();
	}

	private void apply() {
		part.setRenderScaleX(renderScaleX);
		part.setRenderScaleY(renderScaleY);
		part.setRenderScaleZ(renderScaleZ);
		part.rotationPointX = rotationPointX;
		part.rotationPointY = rotationPointY;
		part.rotationPointZ = rotationPointZ;
		part.setPreRotateAngleX(preRotateAngleX);
		part.setPreRotateAngleY(preRotateAngleY);
		part.setPreRotateAngleZ(preRotateAngleZ);
		part.rotateAngleX = rotateAngleX;
		part.rotateAngleY = rotateAngleY;
		part.rotateAngleZ = rotateAngleZ;
		part.isHidden = hidden;
		part.showModel = showModel;
		if (childs != null) {
			for (TESModelDragonPartProxy child : childs) {
				child.apply();
			}
		}
	}

	public void render(float scale) {
		apply();
		part.render(scale);
	}

	public void update() {
		renderScaleX = part.getRenderScaleX();
		renderScaleY = part.getRenderScaleY();
		renderScaleZ = part.getRenderScaleZ();
		rotationPointX = part.rotationPointX;
		rotationPointY = part.rotationPointY;
		rotationPointZ = part.rotationPointZ;
		preRotateAngleX = part.getPreRotateAngleX();
		preRotateAngleY = part.getPreRotateAngleY();
		preRotateAngleZ = part.getPreRotateAngleZ();
		rotateAngleX = part.rotateAngleX;
		rotateAngleY = part.rotateAngleY;
		rotateAngleZ = part.rotateAngleZ;
		hidden = part.isHidden;
		showModel = part.showModel;
		if (childs != null) {
			for (TESModelDragonPartProxy child : childs) {
				child.update();
			}
		}
	}
}