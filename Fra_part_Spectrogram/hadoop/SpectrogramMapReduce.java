package hadoop;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import audiofinger.STFT;
import audiofinger.Spectrogram;

public class SpectrogramMapReduce {
	
	public static int[] sig; 
	public static int framesize;
	public static float samplerate;
	public static float h;
	public static float endtime;

	
	public static class Map extends Mapper<Object, Text, Text, Text>{
		public void map(Object key, Text value, Context context)throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				String[] values = itr.nextToken().trim().split(",");
				IntWritable outputKey = new IntWritable(Integer.parseInt(values[0]));
				IntWritable outputValue = new IntWritable(Integer.parseInt(values[1]));
				context.write(new Text(""+outputKey), new Text(""+outputValue));
			}
		}
	}

	
	public static class Reduce extends Reducer<Text,Text,Text,Text> {				
		public void reduce(Text key, Iterable<Text> values,Context context)throws IOException, InterruptedException {
			STFT stft = new STFT();
			int val = 0;
			for (Text value : values){
				val = Integer.parseInt(value.toString());
			}
			LinkedHashMap<Double,Double> plop = stft.writeFreqMagnHadoop(sig, framesize, val, samplerate, h, endtime);
			Object[] freq = plop.keySet().toArray();//get frequencies
			Object[] magn = plop.values().toArray();//get magnitudes
			Spectrogram spectro = new Spectrogram();
			spectro.setAccuracy(4);
			spectro.calculateZones(stft, freq, magn);
			Object[] valzones = spectro.getZones().values().toArray();
			for(int v=0;v<valzones.length;v++){
				context.write(new Text(""+v), new Text(""+key.toString()+"\t"+valzones[v].toString()));
			}
		}
	}

	
	public void doMapReduce(String file, String folder) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
	 
		job.setJarByClass(SpectrogramMapReduce.class);
		job.setMapperClass(Map.class);
		//job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);
		
		//job.setMapOutputKeyClass(Text.class);
		//job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
	 
		FileInputFormat.addInputPath(job, new Path(file));
		FileOutputFormat.setOutputPath(job, new Path(folder));
	 
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}//end of class