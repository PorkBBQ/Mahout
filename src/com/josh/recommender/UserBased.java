package com.josh.recommender;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class UserBased {
	
	public static void main(String[] args) throws Exception{
		
		long AverageTime =0; 
		long StartTime = System.currentTimeMillis();
		System.out.print("=== go! ===\n\n");

		run();
		
		System.out.print("\n=== done! (");
		System.out.print((System.currentTimeMillis() - StartTime)/1000.0);
		System.out.print(" s) ===");

	}
	
	public static void run () throws Exception{

		
//		DataModel model = new FileDataModel(new File("D:\\Josh\\ratings2.dat"));
		DataModel model = new FileDataModel(new File("data/ratings2.csv"));
//		DataModel model = new FileDataModel(new File("hdfs://10.24.100.136:9000/josh/data/rating2.dat"));

		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
		
		for(int i=0;i<100;i++){
			List<RecommendedItem> recommendations=recommender.recommend(i+1, 10);

			for(RecommendedItem recommendation:recommendations){
				System.out.print(i+1);
				System.out.print("\t");
				System.out.print(recommendation);
				System.out.print("\n");
			}
		}
	}
}