package ctd.test.maps;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConMapTester {
	private static String[] keys = new String[]{"sean","sophie","main","java","swift","hash"};
	
	private static void setupData(Map<String,String> map){
		for(String k : keys){
			map.put(k, "val=" + k);
		}
	}
	
	private static void setupData2(Map<ItemKey,String> map){
		int i = 0;
		for(String k : keys){
			
			map.put(new ItemKey(i, k), "val=" + k);
			i ++;
		}
	}
	
	private static void print(Map<String,String> map){
		Set<String> keys = map.keySet();
		for(String k : keys){
			System.out.println(map.get(k));
		}
	}
	
	private static void print2(Map<ItemKey,String> map){
		Set<ItemKey> keys = map.keySet();
		for(ItemKey k : keys){
			System.out.println(map.get(k));
		}
	}
	
	public static void testMap1(){
		Map<String,String> map1 = new ConcurrentHashMap<>();
		setupData(map1);
		print(map1);
	}
	
	public static void testMap2(){
		Map<String,String> map1 = new ConcurrentSkipListMap<>();
		setupData(map1);
		print(map1);
	}
	
	public static void testMap3(){
		Map<ItemKey,String> map1 = new ConcurrentSkipListMap<>();
		setupData2(map1);
		print2(map1);
	}
	
	public static void main(String[] args) {
		testMap3();

	}

}
