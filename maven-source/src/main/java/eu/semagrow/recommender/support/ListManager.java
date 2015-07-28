package eu.semagrow.recommender.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListManager {

	public static List<Set<String>> split(Set<String> original, int maxSize) {
		List<Set<String>> result = new ArrayList<Set<String>>();
		//generate the ArrayList
		List<String> arrayList = new ArrayList<String>(original);
		if(maxSize<original.size()){
			for(int i=0; i<original.size(); i+=maxSize){
				Set<String> sub = new HashSet<String>();
				//check limits
				if((i+maxSize)<=original.size())
					sub.addAll(arrayList.subList(i, i+maxSize));
				else
					sub.addAll(arrayList.subList(i, original.size()));
				result.add(sub);
			}
		} else
			//only one set
			result.add(original);
		return result;
	}
	
	public static void main(String[] args){
		Set<String> sub = new HashSet<String>();
		sub.add("1");
		sub.add("2");
		sub.add("3");
		sub.add("4");
		sub.add("5");
		sub.add("6");
		sub.add("7");
		sub.add("8");
		sub.add("9");
		sub.add("10");
		sub.add("11");
		System.out.println(ListManager.split(sub, 10));
	}

}
