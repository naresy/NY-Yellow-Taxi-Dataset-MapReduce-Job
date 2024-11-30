import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;

public class AnomalyDetection {

    public static class AnomalyMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
        private boolean isHeader = true; // Flag to track the header row
        private int lineCount = 0; // Counter for lines processed
        private static final int MAX_LINES = 100000; // Maximum number of lines to process

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (lineCount >= MAX_LINES) {
                return; // Stop processing after reaching the limit
            }

            String line = value.toString();

            // Skip header row (first row of the file)
            if (isHeader) {
                isHeader = false;
                return;
            }

            // Increment line count
            lineCount++;

            // Split the line into fields using comma as a delimiter
            String[] fields = line.split(",");

            try {
                // Ensure there are at least 5 fields in the row
                if (fields.length > 4) {
                    String pickupDateTime = fields[1].trim(); // tpep_pickup_datetime
                    double tripDistance = Double.parseDouble(fields[4].trim()); // trip_distance

                    // Emit key-value pair: (pickupDateTime, tripDistance)
                    context.write(new Text(pickupDateTime), new DoubleWritable(tripDistance));
                } else {
                    System.err.println("Skipping malformed line: " + line);
                }
            } catch (Exception e) {
                // Handle parsing errors gracefully
                System.err.println("Error processing line: " + line);
                e.printStackTrace();
            }
        }
    }

    public static class AnomalyReducer extends Reducer<Text, DoubleWritable, Text, Text> {
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            ArrayList<Double> distances = new ArrayList<>();
            double sum = 0;
            int count = 0;

            // Collect all trip distances and calculate their sum
            for (DoubleWritable val : values) {
                double distance = val.get();
                distances.add(distance);
                sum += distance;
                count++;
            }

            // Skip processing if there are no distances
            if (count == 0) {
                return;
            }

            // Calculate mean
            double mean = sum / count;

            // Calculate standard deviation
            double varianceSum = 0;
            for (double distance : distances) {
                varianceSum += Math.pow(distance - mean, 2);
            }
            double stddev = Math.sqrt(varianceSum / count);

            // Emit anomalies (distances greater than 2 standard deviations from the mean)
            for (double distance : distances) {
                if (Math.abs(distance - mean) > 2 * stddev) {
                    context.write(key, new Text("Anomaly: " + distance));
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Anomaly Detection");
        job.setJarByClass(AnomalyDetection.class);
        job.setMapperClass(AnomalyMapper.class);
        job.setReducerClass(AnomalyReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(DoubleWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // Input file path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output directory path

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
