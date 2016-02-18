import java.util.ArrayList;
import java.util.List;


public class Cluster {
	
	List<Tweet> tweets;
	Tweet centroid;
	int cId;
	
	public Cluster(int id, Tweet tweet) {
		this.cId = id;
		this.tweets = new ArrayList<Tweet>();
		this.centroid = tweet;
	}
}
