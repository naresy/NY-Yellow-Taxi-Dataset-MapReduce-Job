import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Classification {

    public static class ClassificationMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private boolean isHeader = true; // Skip header row
        private final static IntWritable one = new IntWritable(1);
        private int lineCount = 0; // Counter for lines processed
        private static final int MAX_LINES = 3000; // Maximum number of lines to process

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (lineCount >= MAX_LINES) {
                return; // Stop processing after reaching the limit
            }

            String line = value.toString();

            // Skip the header row
            if (isHeader) {
                isHeader = false;
                return;
            }

            // Increment line count
            lineCount++;

            // Split the row into fields using a comma delimiter
            String[] fields = line.split(",");

            try {
                // Ensure there are enough fields
                if (fields.length > 10) {
                    String pickupDateTime = fields[1].trim(); // tpep_pickup_datetime
                    String paymentType = fields[10].trim();  // payment_type

                    // Extract year and month from the pickup datetime (e.g., "2016-03")
                    String yearMonth = pickupDateTime.split(" ")[0].substring(0, 7); // Extract "YYYY-MM"

                    // Combine year-month and payment type as the key
                    String keyOut = yearMonth + "_" + paymentType;

                    // Emit the key-value pair (keyOut, 1)
                    context.write(new Text(keyOut), one);
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            } catch (Exception e) {
                // Handle invalid rows gracefully
                System.err.println("Error processing line: " + line);
                e.printStackTrace();
            }
        }
    }

    public static class ClassificationReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;

            // Aggregate the counts for each key
            for (IntWritable val : values) {
                sum += val.get();
            }

            // Emit the final result (key, total count)
            context.write(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Classification");
        job.setJarByClass(Classification.class);
        job.setMapperClass(ClassificationMapper.class);
        job.setReducerClass(ClassificationReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // Input path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
