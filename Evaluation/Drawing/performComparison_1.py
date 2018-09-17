import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D

n_groups = 10

searchcode = (0, 0, 66.6, 100, 100, 100, 0, 0, 66.6, 0)

S_COACH = (100, 100, 60, 0, 57.1, 10, 100, 100, 0, 80)

Krugle = (0, 0, 0, 80, 0, 0, 0, 0, 0, 0)

index = np.arange(n_groups)
bar_width = 0.2

opacity = 0.4
error_config = {'ecolor': '0.3'}

rects1 = plt.bar(index, searchcode, bar_width, alpha=opacity, color='0.95', error_kw=error_config, label='searchcode')

rects2 = plt.bar(index + bar_width, S_COACH, bar_width, alpha=opacity, color='0.', error_kw=error_config, label='S_COACH')

rects3 = plt.bar(index + bar_width,Krugle, bar_width, alpha=opacity, color='0.674', error_kw=error_config, label='Krugle')


plt.ylabel('(%)')
plt.yticks(fontsize=13)
plt.xticks(index + bar_width, ('Q1', 'Q2', 'Q3', 'Q4', 'Q5', 'Q6', 'Q7', 'Q8', 'Q9', 'Q10'), fontsize=13)
plt.legend(bbox_to_anchor=(0., 1.02, 1., .102), loc=3, ncol=3, mode="expand", borderaxespad=0., frameon=False)
plt.show()