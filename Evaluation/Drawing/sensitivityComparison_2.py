import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D

searchcode = np.array([100, 100, 20, 0, 0])

S_COACH = np.array([60, 80, 60, 80, 100])

Krugle = np.array([20, 0, 0, 0, 0])

x = np.array([0,1,2,3,4])

xaixsname = ['LOC 1', 'LOC 2', 'LOC 3', 'LOC 5', 'Entire Code']

plt.xticks(x, xaixsname, fontsize=13)

line1, = plt.plot(x, searchcode, linewidth=2.5, marker='o', markersize=8, linestyle='-', color='0.', label='searchcode')
line2, = plt.plot(x, S_COACH, linewidth=2.5, marker='s', markersize=8, linestyle='--', color='0.', label='S_COACH')
line3, = plt.plot(x, Krugle, linewidth=2.5, marker='d', markersize=8, linestyle='-.', color='0.', label='Krugle')

plt.yticks(fontsize=13)
plt.xlim(-0.2*1.1, 3.8*1.1)
plt.ylim(-5*1.1, 95*1.1)
plt.ylabel('( % )')
plt.legend(bbox_to_anchor=(0., 1.02, 1., .102), loc=3, ncol=3, mode="expand", borderaxespad=0., handler_map={line1: HandlerLine2D(numpoints=1), line2: HandlerLine2D(numpoints=1), line3: HandlerLine2D(numpoints=1)}, frameon=False)
plt.grid(True)
plt.show()