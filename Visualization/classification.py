import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Step 1: Load the classification output data
# Replace '/path/to/output/part-r-00000' with your actual file path
file_path = 'Classification_Output/part-r-00000'

# Load the data into a DataFrame
data = []
with open(file_path, 'r') as file:
    for line in file:
        parts = line.strip().split('\t')
        if len(parts) == 2:
            # Extract year-month, feature value, and count
            key = parts[0]
            count = int(parts[1])
            
            year_month, feature_value = key.split('_')
            feature_value = float(feature_value)
            
            data.append({"year_month": year_month, "feature_value": feature_value, "count": count})

# Convert the list of dictionaries into a DataFrame
df = pd.DataFrame(data)

# Step 2: Visualization

# 2.1: Total Classification Counts Per Month (Bar Chart)
monthly_counts = df.groupby('year_month')['count'].sum().reset_index()

plt.figure(figsize=(10, 6))
plt.bar(monthly_counts['year_month'], monthly_counts['count'], color='skyblue')
plt.title('Total Classification Counts Per Month', fontsize=14)
plt.xlabel('Year-Month', fontsize=12)
plt.ylabel('Total Count', fontsize=12)
plt.xticks(rotation=45)
plt.grid(axis='y', linestyle='--')
plt.tight_layout()
plt.savefig('classification_total_counts.png')
plt.show()

# 2.2: Scatter Plot of Feature Values and Counts
plt.figure(figsize=(12, 6))
sns.scatterplot(data=df, x='feature_value', y='count', hue='year_month', palette='tab10', s=100)
plt.title('Feature Value vs. Count by Year-Month', fontsize=14)
plt.xlabel('Feature Value', fontsize=12)
plt.ylabel('Count', fontsize=12)
plt.legend(title='Year-Month', bbox_to_anchor=(1.05, 1), loc='upper left')
plt.grid()
plt.tight_layout()
plt.savefig('classification_feature_scatter.png')
plt.show()

# 2.3: Heatmap of Feature Values and Year-Month
# Pivot the data for heatmap visualization
heatmap_data = df.pivot_table(index='feature_value', columns='year_month', values='count', fill_value=0)

plt.figure(figsize=(12, 8))
sns.heatmap(heatmap_data, cmap='YlGnBu', annot=False, cbar_kws={'label': 'Count'})
plt.title('Heatmap of Feature Values and Year-Month', fontsize=14)
plt.xlabel('Year-Month', fontsize=12)
plt.ylabel('Feature Value', fontsize=12)
plt.tight_layout()
plt.savefig('classification_heatmap.png')
plt.show()
