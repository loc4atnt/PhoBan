package loc4atnt.phoban.dungeon.reward;

public enum RewardType {
	P, M, E, I, FR, C;

	public static RewardType getFromName(String name) {
		switch (name) {
		case "P":
			return P;
		case "M":
			return M;
		case "E":
			return E;
		case "I":
			return I;
		case "FR":
			return FR;
		case "C":
			return C;
		}
		return null;
	}
}
