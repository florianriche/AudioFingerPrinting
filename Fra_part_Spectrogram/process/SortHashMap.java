package process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class SortHashMap {

	public ArrayList<String> specialSortedByValue(HashMap hashmap, ArrayList<String> file){
		ArrayList<String> res = new ArrayList<String>();
		Set<Entry<Integer, Integer>> set = hashmap.entrySet();
	    ArrayList<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer, Integer>>(set);
	    Collections.sort( list, new Comparator<Map.Entry<Integer, Integer>>()
	    {
	        public int compare( Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2 )
	        {
	            return (o1.getValue()).compareTo( o2.getValue() );
	        }
	    } );
	    for(Map.Entry<Integer, Integer> entry:list){
	       //System.out.println(entry.getKey()+" ==== "+entry.getValue());
	    	//System.out.println(file.get(entry.getKey()));
	    	res.add(file.get(entry.getKey()));
	    }
	    return res;
	}

}//end of class

