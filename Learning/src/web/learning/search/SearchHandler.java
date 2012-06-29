package web.learning.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

import android.content.Context;
import web.learning.database.SearchDatabase;

public class SearchHandler {

	private SearchDatabase searchDBase;
	private Map<String, Integer> SearchBase;
	private int MaxID = 0;
	private Iterator<Entry<String, Integer>> name_iterator, id_iterator;
	
	public SearchHandler(Context _context){
		searchDBase = new SearchDatabase(_context);
		SearchBase = searchDBase.GetSearchBase();
		MaxID = searchDBase.GetMaxID();
		SortBase();
		name_iterator = SearchBase.entrySet().iterator();
		id_iterator = SearchBase.entrySet().iterator();
	}
	
	public int GetNEXTSortID(){
		if (id_iterator.hasNext()){
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)id_iterator.next();
			return (Integer) pair.getValue();
		}
		return 0;
	}
	
	public String GetNEXTSortName(){
		if (name_iterator.hasNext()){
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)name_iterator.next();
			return (String) pair.getKey();
		}
		return "";
	}
	
	public String GetSortName(int num){
		Iterator<Entry<String, Integer>> it = SearchBase.entrySet().iterator();
		int i = 1;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
			if (i == num) return (String) pairs.getKey();
			i++;
		}
		return "";
	}
	
	public int GetSortID(int num){
		Iterator<Entry<String, Integer>> it = SearchBase.entrySet().iterator();
		int i = 1;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>)it.next();
			if (i == num){
				return (Integer) pairs.getValue();
			}
			i++;
		}
		return 0;
	}
	
	public int GetIDfromName(String key){
		return SearchBase.get(key);
	}
	
	public int GetMaxID(){
		return MaxID;
	}
	
	private void SortBase(){
		SearchBase = new TreeMap<String, Integer>(SearchBase);
	}
	
	public Map<String,Integer> GetSearchMap(String query){
		Map<String,Integer> names = new HashMap<String,Integer>();
		Vector<Integer> N = GetSearchIterNum(query);
		int firstN = N.elementAt(0), lastN = N.elementAt(1);
		Iterator<Entry<String, Integer>> it = SearchBase.entrySet().iterator();
		for (int i = 1; i < firstN; i++)
			it.next();
		for (int i = firstN; i < lastN; i++){
			Map.Entry<String, Integer> pairs = it.next();
			names.put((String) pairs.getKey(), (Integer) pairs.getValue());
		}
		return names;
	}
	
	private int GetFirstSearchIterNum(String query){
		String key = "";
		boolean equal = false;
		Iterator<Entry<String, Integer>> it = SearchBase.entrySet().iterator();
		int num = 1;
		if (query.equals("")) return 1;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			key = (String) pairs.getKey();
			equal = (key.length() >= query.length());
			for (int i=0; (i < query.length()); i++){
				equal = (equal && (key.charAt(i) == query.charAt(i)) );
				if (!equal) break;
			}
			if (equal) return num;
			num++;
		}
		return 1;
	}
	
	private Vector<Integer> GetSearchIterNum(String query){
		Vector<Integer> N = new Vector<Integer>();
		int lastN = GetFirstSearchIterNum(query);
		N.add(lastN);
		Iterator<Entry<String, Integer>> it = SearchBase.entrySet().iterator();
		for (int i = 1; i < lastN; i++)
			it.next();
		
		String key = "";
		boolean equal = false;
		int num = lastN;
		N.add(num);
		if (query.equals("")) return N;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			key = (String) pairs.getKey();
			equal = (key.length() >= query.length());
			for (int i=0; (i < query.length()); i++){
				equal = (equal && (key.charAt(i) == query.charAt(i)) );
				if (!equal) break;
			}
			if (!equal) {
				N.setElementAt(num, 1);
				return N;
			}
			num++;
		}
		N.setElementAt(num, 1);
		return N;
	}
}
