import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Step 1: Read the AnomalyDetection output
# Replace '/path/to/output/part-r-00000' with your actual file path
file_path = 'AnomalyDection_Output/part-r-00000'

try:
    data = []
    with open(file_path, 'r') as file:
        for line in file:
            parts = line.strip().split('\t')
            if len(parts) == 2 and "Anomaly" in parts[1]:
                try:
                    timestamp = parts[0]
                    anomaly_value = float(parts[1].replace("Anomaly: ", ""))
                    data.append({"timestamp": timestamp, "anomaly_distance": anomaly_value})
                except ValueError:
                    # Skip lines with non-numeric anomaly values
                    continue

    # Convert the list of dictionaries into a DataFrame
    df = pd.DataFrame(data)

    # Convert 'timestamp' to a datetime object
    df['timestamp'] = pd.to_datetime(df['timestamp'], errors='coerce')
    df.dropna(subset=['timestamp'], inplace=True)  # Remove rows with invalid timestamps

    # Add a 'date' column for daily aggregation
    df['date'] = df['timestamp'].dt.date

    # Step 2: Visualization

    # 2.1: Time Series Plot of Anomalies
    plt.figure(figsize=(14, 7))
    plt.plot(df['timestamp'], df['anomaly_distance'], marker='o', linestyle='-', label='Anomaly Distance')
    plt.title('Anomalous Distances Over Time', fontsize=16)
    plt.xlabel('Timestamp', fontsize=14)
    plt.ylabel('Anomalous Distance (miles)', fontsize=14)
    plt.xticks(rotation=45)
    plt.legend()
    plt.grid()
    plt.tight_layout()
    plt.savefig('anomalies_time_series.png')
    plt.show()

    # 2.2: Distribution Plot of Anomalous Distances
    plt.figure(figsize=(12, 6))
    sns.histplot(df['anomaly_distance'], kde=True, bins=20, color='blue')
    plt.title('Distribution of Anomalous Distances', fontsize=16)
    plt.xlabel('Anomalous Distance (miles)', fontsize=14)
    plt.ylabel('Frequency', fontsize=14)
    plt.grid()
    plt.tight_layout()
    plt.savefig('anomalies_distribution.png')
    plt.show()

    # 2.3: Daily Anomaly Count
    daily_counts = df.groupby('date').size().reset_index(name='anomaly_count')

    plt.figure(figsize=(14, 7))
    plt.plot(daily_counts['date'], daily_counts['anomaly_count'], marker='o', linestyle='-', label='Anomaly Count')
    plt.title('Daily Count of Anomalies', fontsize=16)
    plt.xlabel('Date', fontsize=14)
    plt.ylabel('Anomaly Count', fontsize=14)
    plt.xticks(rotation=45)
    plt.legend()
    plt.grid()
    plt.tight_layout()
    plt.savefig('daily_anomaly_count.png')
    plt.show()

except FileNotFoundError:
    print(f"Error: File not found at {file_path}. Please check the path and try again.")
except Exception as e:
    print(f"An error occurred: {e}")
