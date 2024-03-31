package ru.olejka.gamezonesplugin;

import java.util.HashMap;
import java.util.Map;

public class Coordinates {
	public final Map<String, Integer> first = new HashMap<>();
	public final Map<String, Integer> second = new HashMap<>();

	public void setFirst(int x, int z) {
		first.put("x", x);
		first.put("z", z);
	}

	public void setSecond(int x, int z) {
		second.put("x", x);
		second.put("z", z);
	}

	public Map<String, Integer> getFirst() {
		return first;
	}

	public Map<String, Integer> getSecond() {
		return second;
	}
}
