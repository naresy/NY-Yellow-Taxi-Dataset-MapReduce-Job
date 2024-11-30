import matplotlib.pyplot as plt
import numpy as np

# Updated execution times for VMs
execution_time_vms = np.array([144, 72, 48, 36, 28.8, 24, 20.6, 18])  # Provided times
vm_counts = np.array([1, 2, 3, 4, 5, 6, 7, 8])  # Number of VMs

# Data for Data Size vs Execution Time (Linear Scaling)
data_sizes = np.array([1, 2, 3, 4, 5, 6, 7, 8]) #Calculate Based On the output
execution_time_data = 18 * data_sizes  # Linear scaling (time proportional to data size)

# Plotting Execution Time vs Datanode
plt.figure(figsize=(10, 6))
plt.plot(vm_counts, execution_time_vms, marker='o', label='Execution Time vs VMs')
plt.title('Workflow Execution Time vs Number of Datanode')
plt.xlabel('Number of Datanode')
plt.ylabel('Execution Time (seconds)')
plt.grid(True)
plt.legend()
plt.show()
plt.savefig('execution_time_vs_datanoe.png')

# Plotting Execution Time vs Data Size
plt.figure(figsize=(10, 6))
plt.plot(data_sizes, execution_time_data, marker='s', label='Execution Time vs Data Size', color='orange')
plt.title('Workflow Execution Time vs Data Size')
plt.xlabel('Data Size (Multiples of Base Dataset)')
plt.ylabel('Execution Time (seconds)')
plt.grid(True)
plt.legend()
plt.show()
plt.savefig('execution_time_vs_datasize.png')
