package hadoop;

import java.io.IOException;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import audiofinger.HaarWavelet2D;

/**
 * 
 * @author Paco
 *
 */
public class HaarWaveletMapReduce {
	
	public static class Map extends Mapper<Object, Text, Text, Text>{
		public void map(Object key, Text value, Context context)throws IOException, InterruptedException {
				String[] values = value.toString().trim().split("\t");
				//val0 is key<=>freq zone
				//val1 is value <=>time zone
				//val2 is value <=> magn value
				context.write(new Text(""+values[0]), new Text(""+values[1]+","+values[2]));
		}
	}

	
	public static class Reduce extends Reducer<Text,Text,Text,Text> {				
		public void reduce(Text key, Iterable<Text> values,Context context)throws IOException, InterruptedException {
			
			TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
			
			for (Text value : values){
				String[] valsplit = value.toString().split(",");
				map.put(Integer.parseInt(valsplit[0]), Double.parseDouble(valsplit[1]));
			}
			//matrix for row decomposition
			double[] matrix = new double[map.values().size()]; 
			for(int i=0;i<matrix.length;i++){
				matrix[i]=map.get(i);
				//System.out.println(i+" "+matrix[i]);
			}
			
			//process row
			matrix = new HaarWavelet2D().decompositionRow(matrix);
			for(int j=0;j<matrix.length;j++){
				context.write(key, new Text(""+j+"\t"+matrix[j]));
			}
			
		}
		
	}

	
	public void doMapReduce(String file, String folder) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
	 
		job.setJarByClass(HaarWaveletMapReduce.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
	 
		FileInputFormat.addInputPath(job, new Path(file));
		FileOutputFormat.setOutputPath(job, new Path(folder));
	 
		job.waitForCompletion(true);
	}

}
