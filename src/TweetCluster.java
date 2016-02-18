import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class TweetCluster {
	
	int numberOfClusters;
	HashMap<String,Tweet> inputTweets;
	ArrayList<Cluster> clusters;
	ArrayList<Tweet> oldCentroids;
	
	public TweetCluster(int num){
		
		inputTweets = new HashMap<String,Tweet>(); 
		clusters = new ArrayList<Cluster>();
		oldCentroids = new ArrayList<Tweet>(); 
		numberOfClusters = num;
	}
	
	void readTweets(String inputFile) throws IOException, ParseException{
		JSONParser parser = new JSONParser();
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line;
		
		while ((line = br.readLine()) != null) {
			JSONObject jsonObj = (JSONObject) parser.parse(line);
			String text = (String)jsonObj.get("text");
			String id =  jsonObj.get("id").toString();
			
			String[] words = text.split(" ");
			HashSet<String> wordSet = new HashSet<String>();
			for(String w : words){
				w = w.replaceAll("[^a-zA-Z0-9]", "");
				if(w.startsWith("RT") || w.startsWith("@")){
					continue;
				}
				else{
					if(!w.isEmpty()){
						wordSet.add(w);
					}
				}
			}
			inputTweets.put(id,new Tweet(id, wordSet));
		}
		br.close();
	}
	
	void createFirstClusters(String initialFile) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(initialFile));
		String line;
		int clusterNumber=0;
		while ((line = br.readLine()) != null) {
			String seedId = line.replaceAll(",", "");
			Tweet t = inputTweets.get(seedId);
			Cluster c = new Cluster(clusterNumber,t);
			clusters.add(c);
			//System.out.println("Custer num:" + clusterNumber + " Seeds : "+seedId +" words:" + t.words );
			clusterNumber++;
		}
	}
	
	void classifyTweets(){
		
		for(String j: inputTweets.keySet()){
			Tweet t= inputTweets.get(j);
			Double minDistance = Double.MAX_VALUE;
			int minDisClusterNum = 0;
			//System.out.println("Tweet id:"  +t.id);
			for(int i=0; i < numberOfClusters; i++){
				
				Double d = t.findJaccardDistance(clusters.get(i).centroid);
				//System.out.println(d);
				if(d<minDistance){
					 minDistance = d;
					 minDisClusterNum = i;
				}
						 
			}
			//System.out.println("cluster id:"  +clusters.get(minDisClusterNum).centroid.id);
			//System.out.println("Min : " + minDistance + " ClusterNumber : " + minDisClusterNum);
			t.clusterId =minDisClusterNum;
			(clusters.get(minDisClusterNum)).tweets.add(t);
		}
		
		/*System.out.println("#############");
		for(String j: inputTweets.keySet()){
			Tweet t= inputTweets.get(j);
			System.out.println(t.id + "\t" + t.clusterId );
		}*/
		
	}
	
	boolean createNewClusters(){
		boolean flag=true;
		for(int i=0; i<numberOfClusters; i++){
			
			Cluster c = clusters.get(i);
			Double minDis = Double.MAX_VALUE;
			String minDisId="";
			
			for(Tweet t1 : c.tweets ){
				Double sum=0.0;
				for(Tweet t2 : c.tweets){
					sum += t2.findJaccardDistance(t1);
				}
				if(sum<minDis){
					minDis = sum;
					minDisId = t1.id;
				}
			}
			//System.out.println("old Cluster numb:" + c.cId + " tweet" + c.centroid.id);
			if(!minDisId.equals(c.centroid.id)){
				c.centroid = inputTweets.get(minDisId);
				//System.out.println("Cluster numb:" + c.cId + " tweet" + c.centroid.id);
				flag=false;
			}
			else{
				//System.out.println("Cluster numb:" + c.cId + " tweet" + c.centroid.id);	
			}
		}
		
		return flag;	
	}
	
	void writeOutput(String outputFile) throws IOException {
		
		FileWriter writer;
	    BufferedWriter bufferedWriter;
	    writer = new FileWriter(outputFile, false);
        bufferedWriter = new BufferedWriter(writer);
        bufferedWriter.write("Cluster ID" + "\t" + "Points");
        bufferedWriter.newLine();
       
        for(Cluster c: clusters){
        	bufferedWriter.write(String.valueOf(c.cId));
        	//System.out.println(c.centroid.id);
        	for(Tweet t : c.tweets){
        		bufferedWriter.write("\t" + t.id + ",");
        	}
        	bufferedWriter.newLine();
        }
        bufferedWriter.close(); 
	}
	
	public void clearPoints() {
		for(Cluster c: clusters){
			c.tweets.clear();
		}
		
	}
	
Double findSSE() {
		
		Double sse=0.0;
		for(Cluster c:clusters){
			for(Tweet t:c.tweets){
				sse += Math.pow(t.findJaccardDistance(c.centroid), 2);
			}
		}
		
		return Math.round((sse)*100.00)/100.00;
		
	}
	
	
	
}
