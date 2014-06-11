package ctd.test.maps;

public class ItemKey implements Comparable<ItemKey>{
	private String key;
	private int index;
	
	public ItemKey(int index, String key){
		this.key = key;
		this.index = index;
	}

	public int getIndex(){
		return index;
	}
	
	@Override
	public int hashCode(){
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		return key.equals(o);
	}

	@Override
	public int compareTo(ItemKey o) {
		if(index == o.getIndex()){
			return 0;
		}
		else if(index > o.getIndex()){
			return 1;
		}
		else if(index < o.getIndex()){
			return -1;
		}
		return 0;
	}
	

}
