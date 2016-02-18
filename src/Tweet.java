import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Tweet {
	
	String id;
	HashSet<String> words;
	int clusterId = 0;
	
	public Tweet(String id, HashSet<String> wordList){
		this.id = id;
		words = wordList;
		
	}
	
	Double findJaccardDistance(Tweet centroid){
		Set union = new HashSet(words);
		union.addAll(centroid.words);
		int u = union.size();
		
		Set intersect = new HashSet(words);
		intersect.retainAll(centroid.words);
		int i = intersect.size();
		
		Double disDouble= (u-i)/(double)u;
		Double dis = Math.round((disDouble)*100.00)/100.00;
		return dis;
	}
}
