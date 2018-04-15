package maki325.roles.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static List<String> removeStringFromList(List<String> one, String two) {
		for(int i = 0;i < one.size();i++) {
			if(one.get(i).equals(two)) {
				one.remove(i);
				return one;
			}
		}
		return one;
	}
	
	public static List<String> addStrinToList(List<String> one, String two) {
		one.add(two);
		return one;
	}
	
	public static String listToString(List<String> list, String devider) {
		String ret = "";
		for(int i = 0;i < list.size();i++) {
			ret += devider + list.get(i);
		}
		return ret.substring(1);
	}
	
	public static List<String> addStringListsTogether(List<String> one, List<String> two) {
		List<String> newList = new ArrayList<String>();
		for(String s:one) {
			newList.add(s);
		}
		for(String s:two) {
			newList.add(s);
		}
		return newList;
	}
	
}
