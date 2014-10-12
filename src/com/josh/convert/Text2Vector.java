package com.josh.convert;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

	public class Text2Vector {
		
		public static void main(String[] args) throws Exception{
	 
			long StartTime = System.currentTimeMillis();
			System.out.print("=== go! ===\n\n");
			run(args);
			double timeSpent=(System.currentTimeMillis() - StartTime)/1000.0;
			System.out.printf("\n=== done! ( %.2f s)\n", timeSpent);
		}
		
		public static void run(String[] args)  throws Exception{
			String input;
			String output;
			if (args.length==0){
				input="hdfs:///tmp/canopy/raw/vector.csv";
				output="hdfs:///tmp/canopy/vector";
			}
			else{
				input=args[0];
				output=args[1];
				
			}
			System.setProperty("HADOOP_USER_NAME","root");
			Configuration conf=new Configuration();
			conf.set("fs.defaultFS", "hdfs://192.168.31.200:8020");
		    //conf.set("mapreduce.framework.name", "yarn");  
		    //conf.set("yarn.resourcemanager.address", "192.168.31.200:8032");
		
			FileSystem fs = FileSystem.get(conf);
			if(fs.delete(new Path(output), true))
				System.out.printf("DELETE: %s\n", output);
			
			fs.close();
			
			Job job=new Job(conf, "Text2Vector");

			job.setJarByClass(Text2Vector.class);
			
			job.setOutputFormatClass(SequenceFileOutputFormat.class);
			
			job.setMapperClass(Text2VectorMapper.class);
			job.setReducerClass(Text2VectorReducer.class);
			//job.setCombinerClass(Text2VectorReducer.class);
			
			job.setOutputKeyClass(LongWritable.class);
			job.setOutputValueClass(VectorWritable.class);

			FileInputFormat.addInputPath(job, new Path(input));
			//FileOutputFormat.setOutputPath(job, new Path(output));
			SequenceFileOutputFormat.setOutputPath(job, new Path(output));
			if(job.waitForCompletion(true)){
				System.out.printf("--> %s\n", output);	
			}

		}
		
		public static class Text2VectorMapper extends Mapper<LongWritable, Text, LongWritable, VectorWritable>{

			public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
				
				String[] str=value.toString().split(",");
				Vector vector = new RandomAccessSparseVector(str.length);
				for (int i=0;i<str.length;i++){
					vector.set(i, Double.parseDouble(str[i]));
				}
				VectorWritable va = new VectorWritable(vector);
				context.write(key, va);
			}
		}

		public static class Text2VectorReducer extends Reducer<LongWritable, VectorWritable, LongWritable, VectorWritable>{
			public void reduce(LongWritable key, Iterable<VectorWritable> values, Context context) throws IOException, InterruptedException{
				for (VectorWritable v:values){
					context.write(key, v);
				}
			}
		}
	}

