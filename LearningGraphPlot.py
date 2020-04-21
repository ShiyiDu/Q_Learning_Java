import pandas as pd
import matplotlib.pyplot as plt

file1 = open('LearningData.txt', 'r') 
Lines = file1.readlines() 
count = 0

averageScores = []
generation = []
highScores = []

for line in Lines: 
	if "average score" in line:
		averageScores.append(float(line.strip()[14:]))
		print(float(line.strip()[14:]))
	elif "current generation" in line:
		generation.append(int(line.strip()[19:]))
		print(int(line.strip()[19:]))
	elif "high score" in line:
		highScores.append(int(line.strip()[11:]))
		print(int(line.strip()[11:]))

plt.rcParams["figure.figsize"] = (12, 6)
plt.plot(generation,averageScores)
plt.xticks(fontsize=16)
plt.yticks(fontsize=16) 
plt.xlabel("Number of Death", fontsize=16)
plt.ylabel("Average Score(Over 100 deaths)", fontsize=16)
plt.title("learning graph by the 70000 death", fontsize=20)
plt.show()

plt.rcParams["figure.figsize"] = (12, 6)
plt.plot(generation,highScores)
plt.xticks(fontsize=16)
plt.yticks(fontsize=16) 
plt.xlabel("Number of Death", fontsize=16)
plt.ylabel("Highest Score so far", fontsize=16)
plt.title("learning graph by the 70000 death", fontsize=20)
plt.show()