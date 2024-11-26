import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Step 1: Read the AnomalyDetection output
# Replace '/path/to/output/part-r-00000' with your actual file path
file_path = '../output/part-r-00000'

# Load the data into a DataFrame
data = []
with open(file_path, 'r') as file:
    for line in file:
        parts = line.strip().split('\t')
        if len(parts) == 2 and "Anomaly" in parts[1]:
            timestamp = parts[0]
            anomaly_value = float(parts[1].replace("Anomaly: ", ""))
            data.append({"timestamp": timestamp, "anomaly_distance": anomaly_value})

# Convert the list of dictionaries into a DataFrame
df = pd.DataFrame(data)

# Convert 'timestamp' to a datetime object
df['timestamp'] = pd.to_datetime(df['timestamp'])

# Add a 'date' column for daily aggregation
df['date'] = df['timestamp'].dt.date

# Step 2: Visualization

# 2.1: Time Series Plot of Anomalies
plt.figure(figsize=(12, 6))
plt.plot(df['timestamp'], df['anomaly_distance'], marker='o', linestyle='-', label='Anomaly Distance')
plt.title('Anomalous Distances Over Time', fontsize=14)
plt.xlabel('Timestamp', fontsize=12)
plt.ylabel('Anomalous Distance (miles)', fontsize=12)
plt.xticks(rotation=45)
plt.legend()
plt.grid()
plt.tight_layout()
plt.savefig('anomalies_time_series.png')
plt.show()

# 2.2: Distribution Plot of Anomalous Distances
plt.figure(figsize=(10, 6))
sns.histplot(df['anomaly_distance'], kde=True, bins=20, color='blue')
plt.title('Distribution of Anomalous Distances', fontsize=14)
plt.xlabel('Anomalous Distance (miles)', fontsize=12)
plt.ylabel('Frequency', fontsize=12)
plt.grid()
plt.tight_layout()
plt.savefig('anomalies_distribution.png')
plt.show()

# 2.3: Daily Anomaly Count
daily_counts = df.groupby('date').size().reset_index(name='anomaly_count')

plt.figure(figsize=(12, 6))
plt.plot(daily_counts['date'], daily_counts['anomaly_count'], marker='o', linestyle='-', label='Anomaly Count')
plt.title('Daily Count of Anomalies', fontsize=14)
plt.xlabel('Date', fontsize=12)
plt.ylabel('Anomaly Count', fontsize=12)
plt.xticks(rotation=45)
plt.legend()
plt.grid()
plt.tight_layout()
plt.savefig('daily_anomaly_count.png')
plt.show()
