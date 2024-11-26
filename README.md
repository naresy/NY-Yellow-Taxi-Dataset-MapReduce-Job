<body>
<h1>NY Yellow Taxi Dataset Analysis with MapReduce</h1>
<p>This repository contains MapReduce jobs and visualizations for analyzing the NY Yellow Taxi Dataset. The three MapReduce jobs include:</p>
<ul>
<li><strong>Anomaly Detection</strong>: Detect anomalies in trip distances.</li>
<li><strong>Classification</strong>: Analyze payment trends over time.</li>
<li><strong>Regression</strong>: Build a predictive model for fare estimation.</li>
</ul>
<p>Additionally, visualization scripts are provided to generate insights from the output of the MapReduce jobs.</p>
<h2>Prerequisites</h2>
<p>Ensure you have the following installed:</p>
<ul>
<li><strong>Hadoop</strong> (Version 3.x or later)</li>
<li><strong>Java Development Kit (JDK)</strong> (Version 11 or later)</li>
<li><strong>Python</strong> (Version 3.x with <code>matplotlib</code>, <code>seaborn</code>, and <code>pandas</code> libraries installed)</li>
</ul>
<h2>Project Structure</h2>
<pre>
.
├── src/                  # Source code for MapReduce jobs
│   ├── AnomalyDetection.java
│   ├── Classification.java
│   ├── Regression.java
├── lib/                  # JAR files for compiled MapReduce jobs
│   ├── AnomalyDetection.jar
│   ├── Classification.jar
│   ├── Regression.jar
├── Visualization/        # Python scripts for visualization
│   ├── AnomalyDetection.py
│   ├── Classification.py
│   └── Regression.py
├── input/                # Input dataset
│   ├── yellow_tripdata_2016-03.csv
├── output/               # Output of AnomalyDetection job
├── Classification_output/ # Output of Classification job
├── Regression_output/    # Output of Regression job
└── README.md             # Project documentation
    </pre>

<h2>Commands and Execution Steps</h2>

<h3>1. Anomaly Detection</h3>
<p>This job detects anomalies in trip distances.</p>
<h4>Compile and Build JAR</h4>
<pre><code>cd src/
javac -classpath `hadoop classpath` -source 11 -target 11 AnomalyDetection.java
jar -cvf AnomalyDetection.jar AnomalyDetection*.class</code></pre>

<h4>Run the Job</h4>
<pre><code>hadoop jar AnomalyDetection.jar AnomalyDetection \
  /Users/drpadhaya/desktop/big_data/input/yellow_tripdata_2016-03.csv \
  /Users/drpadhaya/desktop/big_data/output</code></pre>

<h3>2. Classification</h3>
<p>This job analyzes payment trends over time.</p>
<h4>Compile and Build JAR</h4>
<pre><code>cd src/
javac -classpath `hadoop classpath` -source 11 -target 11 Classification.java
jar -cvf Classification.jar Classification*.class</code></pre>

<h4>Run the Job</h4>
<pre><code>hadoop jar Classification.jar Classification \
  /Users/drpadhaya/desktop/big_data/input/yellow_tripdata_2016-03.csv \
  /Users/drpadhaya/desktop/big_data/Classification_output</code></pre>

<h3>3. Regression</h3>
<p>This job predicts fare amounts based on trip attributes.</p>
<h4>Compile and Build JAR</h4>
<pre><code>cd src/
javac -classpath `hadoop classpath` -source 11 -target 11 Regression.java
jar -cvf Regression.jar Regression*.class</code></pre>

<h4>Run the Job</h4>
<pre><code>hadoop jar Regression.jar Regression \
  /Users/drpadhaya/desktop/big_data/input/yellow_tripdata_2016-03.csv \
  /Users/drpadhaya/desktop/big_data/Regression_output</code></pre>

<h3>4. Visualization</h3>
<p>Use the provided Python scripts to generate visualizations from the MapReduce job outputs.</p>

<h4>Anomaly Detection Visualization</h4>
<pre><code>python Visualization/AnomalyDetection.py</code></pre>

<h4>Classification Visualization</h4>
<pre><code>python Visualization/classification.py</code></pre>

<h4>Regression Visualization</h4>
<pre><code>python Visualization/Regression.py</code></pre>

<h2>Example Outputs</h2>
<p>Sample visualizations and output data are included in the <code>output/</code> directory.</p>
</body>
</html>
