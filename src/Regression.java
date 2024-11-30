import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Regression {

    public static class RegressionMapper extends Mapper<LongWritable, Text, Text, Text> {
        private boolean isHeader = true;
        private int lineCount = 0; // Counter for lines processed
        private static final int MAX_LINES = 100000; // Maximum number of lines to process

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            if (lineCount >= MAX_LINES) {
                return; // Stop processing after reaching the limit
            }

            String line = value.toString();

            // Skip header row
            if (isHeader) {
                isHeader = false;
                return;
            }

            // Increment line count
            lineCount++;

            // Split the row into fields
            String[] fields = line.split(",");

            try {
                if (fields.length > 18) {
                    // Extract relevant features
                    String pickupDateTime = fields[1].trim(); // tpep_pickup_datetime
                    String yearMonth = pickupDateTime.split(" ")[0].substring(0, 7); // Extract year-month

                    double tripDistance = Double.parseDouble(fields[4].trim()); // trip_distance
                    int passengerCount = Integer.parseInt(fields[3].trim()); // passenger_count
                    int rateCodeID = Integer.parseInt(fields[7].trim()); // RateCodeID
                    double tolls = Double.parseDouble(fields[16].trim()); // tolls_amount
                    double fareAmount = Double.parseDouble(fields[12].trim()); // fare_amount

                    // Combine the features into a single value
                    String features = tripDistance + "," + passengerCount + "," + rateCodeID + "," + tolls + "," + fareAmount;

                    // Emit key-value pair: (yearMonth, features)
                    context.write(new Text(yearMonth), new Text(features));
                }
            } catch (Exception e) {
                System.err.println("Error processing line: " + line);
                e.printStackTrace();
            }
        }
    }

    public static class RegressionReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            double sumX1 = 0, sumX2 = 0, sumX3 = 0, sumX4 = 0; // Sum of features
            double sumY = 0, sumX1Y = 0, sumX2Y = 0, sumX3Y = 0, sumX4Y = 0; // Sum for regression
            double sumX1Sq = 0, sumX2Sq = 0, sumX3Sq = 0, sumX4Sq = 0; // Sum of squared features
            int count = 0;

            for (Text value : values) {
                String[] features = value.toString().split(",");
                try {
                    double x1 = Double.parseDouble(features[0]); // trip_distance
                    double x2 = Double.parseDouble(features[1]); // passenger_count
                    double x3 = Double.parseDouble(features[2]); // rateCodeID
                    double x4 = Double.parseDouble(features[3]); // tolls_amount
                    double y = Double.parseDouble(features[4]);  // fare_amount

                    // Update sums
                    sumX1 += x1;
                    sumX2 += x2;
                    sumX3 += x3;
                    sumX4 += x4;
                    sumY += y;

                    sumX1Y += x1 * y;
                    sumX2Y += x2 * y;
                    sumX3Y += x3 * y;
                    sumX4Y += x4 * y;

                    sumX1Sq += x1 * x1;
                    sumX2Sq += x2 * x2;
                    sumX3Sq += x3 * x3;
                    sumX4Sq += x4 * x4;

                    count++;
                } catch (Exception e) {
                    System.err.println("Error processing features: " + value.toString());
                    e.printStackTrace();
                }
            }

            if (count == 0) return; // Avoid division by zero

            // Calculate regression coefficients for a simple linear regression model
            double beta1 = (count * sumX1Y - sumX1 * sumY) / (count * sumX1Sq - sumX1 * sumX1);
            double beta2 = (count * sumX2Y - sumX2 * sumY) / (count * sumX2Sq - sumX2 * sumX2);
            double beta3 = (count * sumX3Y - sumX3 * sumY) / (count * sumX3Sq - sumX3 * sumX3);
            double beta4 = (count * sumX4Y - sumX4 * sumY) / (count * sumX4Sq - sumX4 * sumX4);
            double intercept = (sumY - beta1 * sumX1 - beta2 * sumX2 - beta3 * sumX3 - beta4 * sumX4) / count;

            // Output the regression equation for the given year-month
            String regressionEquation = "Fare = " + intercept + " + " + beta1 + "*trip_distance + " + beta2 + "*passenger_count + "
                    + beta3 + "*rateCodeID + " + beta4 + "*tolls_amount";
            context.write(key, new Text(regressionEquation));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Regression");
        job.setJarByClass(Regression.class);
        job.setMapperClass(RegressionMapper.class);
        job.setReducerClass(RegressionReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));  // Input path
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Output path

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
