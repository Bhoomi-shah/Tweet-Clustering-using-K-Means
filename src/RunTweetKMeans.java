import java.io.IOException;
import java.util.HashSet;

import org.json.simple.parser.ParseException;


public class RunTweetKMeans {

	public static void main(String[] args) throws IOException, ParseException{
		
		TweetCluster tc = new TweetCluster(Integer.parseInt(args[0]));
		tc.readTweets(args[1]);
		tc.createFirstClusters(args[2]);
		tc.classifyTweets();
		
		int i;
		for(i=1; i<=25; i++){			
			if(tc.createNewClusters()){
				
				break;
			}
			tc.writeOutput(args[3]);
			tc.clearPoints();
			tc.classifyTweets();
			
		}
		
		Double sse= tc.findSSE();
		System.out.println("SSE : " + sse);
		
	}

}
