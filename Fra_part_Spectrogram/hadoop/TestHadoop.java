package hadoop;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import audiofinger.AudioWave;
import audiofinger.STFT;
import audiofinger.Spectrogram;
import audiofinger.Utils;


public class TestHadoop {
	
	/*public static int[] sig; 
	public static int framesize;
	public static float samplerate;
	public static float h;
	public static float endtime;*/

	
	public static class Map extends Mapper<Object, Text, Text, Text>{
		public void map(Object key, Text value, Context context)throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				String[] values = itr.nextToken().trim().split(",");
				Text outputKey = new Text(values[0]);
				Text outputValue = new Text(values[1]);
				context.write(outputKey, outputValue);
			}
		}
	}

	
	public static class Reduce extends Reducer<Text,Text,Text,Text> {				
		public void reduce(Text key, Iterable<Text> values,Context context)throws IOException, InterruptedException {
			String tt = "";
			for (Text value : values){
				tt = value.toString();
			}
			
			context.write(key, new Text(""+tt));
		}
	}

	
	public static void main(String[] args) throws Exception {
		//hadoop
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
	 
		job.setJarByClass(TestHadoop.class);
		job.setMapperClass(Map.class);
		//job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		
		//job.setMapOutputKeyClass(Text.class);
		//job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
	 
		FileInputFormat.addInputPath(job, new Path("audio.txt"));
		FileOutputFormat.setOutputPath(job, new Path("OUTPUTBis"));
	 
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}//end of class