package tes;

import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.Collection;

public class TESInfo {
	private static final Collection<String> AUTHORS = new ArrayList<String>();

	private static final String[] DESCRIPTION;

	static {
		AUTHORS.add("Hummel009");
		AUTHORS.add("Mon1tor");
		AUTHORS.add("Octobrine");
		AUTHORS.add("安北都护府大都护-至高无上的Xiang SiMa");
		AUTHORS.add("贾昆·赫加尔01");
		AUTHORS.add("RoobimWu");
		AUTHORS.add("DAndMaster");
		AUTHORS.add("VVVIP2");
		AUTHORS.add("Dimagic");
		AUTHORS.add("sashar2000r");
		AUTHORS.add("Danvintius Bookix");
		AUTHORS.add("iliamakar");
		AUTHORS.add("Tabula");
		AUTHORS.add("ToCraft");
		AUTHORS.add("beautifulrobloxgirl01");
		AUTHORS.add("Agripas");
		AUTHORS.add("Ness");
		AUTHORS.add("Axel Snow");
		AUTHORS.add("Sword of the Morning");
		AUTHORS.add("Valence");
		AUTHORS.add("Alqualindë");
		AUTHORS.add("Jaehaerys");
		AUTHORS.add("TIGASA");
		AUTHORS.add("Alex.Tollar");
		AUTHORS.add("Maglor");
		AUTHORS.add("StalkerKir");
		AUTHORS.add("Coolaga/GualaBoy");
		AUTHORS.add("Amandil");
		AUTHORS.add("Arbeit");

		DESCRIPTION = new String[]{"§b" + StatCollector.translateToLocal("tes.gui.authors") + ' ' + String.join(", ", AUTHORS) + ' ' + StatCollector.translateToLocal("tes.gui.authors.others")};
	}

	private TESInfo() {
	}

	public static String concatenateDescription(int startIndex) {
		StringBuilder sb = new StringBuilder();
		for (int i = startIndex; i < DESCRIPTION.length; ++i) {
			sb.append(DESCRIPTION[i]).append("\n\n");
		}
		return sb.toString();
	}
}