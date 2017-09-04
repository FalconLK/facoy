# Running FaCoY on IJaDataset and evaluating on BigCloneBench (RQ2)

## IJaDataset
IJaDataset is a large java inter-project software repository built from [SeClone project](https://sites.google.com/site/asegsecold/projects/seclone) by Ambient Software Evoluton Group. The current version is IJaDataset-2.0 which is available at the [BigCloneBench page](https://github.com/clonebench/BigCloneBench/blob/master/README.md) otherwise we should ask [SeClone project](https://sites.google.com/site/asegsecold/projects/seclone) site administrator.
IJaDatset is divided into three categories (sample, default, selected) and we used all of them for the experiment.

## BigCloneBench
BigCloneBench is a benchmark for clone detection mined from the IJaDataset-2.0 (25,000 subject systems, 365MLOC). The current version of the benchmark BigCloneBench is available at [BigCloneBench page](https://github.com/clonebench/BigCloneBench/blob/master/README.md). People can see plenty of java snippet clone pairs with various metadata such as functionalities, syntactic types, similarity lines, similarity tokens, projects of each function, etc.

## Funcionalities
BigCloneBnech (IJADataset) has 43 functionalities while it has 8,345,104 clone pairs among the 22,285,855 clones. We also tried to separate the result for each of the function. 
It turns out, FaCoY shows high recall values for "Play Sound (F18)", "Take Screenshot to File (F19)", "XMPP Send Message (F21)", and "Test Palindrome". In particular, within MT3 and WT3/4, our approach works successfully for these functionalities, as well as "Create Encryption Key Files (F17)" and "Create Encryption Key Files". On the other hand, some functionalities such as "Bubble Sort Array (F7)", "Setup SGV (F8)", "Binary Search (F14)", and "Transpose a Matrix (F41)" make FaCoY less successful.
The Funtionality information is able to be checked in the [function_information](https://docs.google.com/spreadsheets/d/1dvUICpQ46BLNrO5oPSxSYihlahC2TeDcUDCHt8potLw/edit?usp=sharing).

The following figure details the distribution.

<p align="center">
<img width="350" alt="t1" src="https://user-images.githubusercontent.com/26062775/30021650-58eba2c2-9168-11e7-9de1-2b5298a77c67.png">
<img width="350" alt="t2" src="https://user-images.githubusercontent.com/26062775/30021661-64fc4fa8-9168-11e7-8b90-874493627e7e.png">
<pre align="center">Type-1                                       Type-2</pre>
</p>

<br /><br />

<p align="center">
<img width="350" alt="t3vst" src="https://user-images.githubusercontent.com/26062775/30021662-6b9f9248-9168-11e7-964f-36945d26c7ed.png">
<img width="350" alt="t3st" src="https://user-images.githubusercontent.com/26062775/30021672-74782d58-9168-11e7-8a77-f40281edc984.png">
<pre align="center">Type-3-VST                                  Type-3-ST</pre>
</p>

<br /><br />

<p align="center">
<img width="350" alt="t3mt" src="https://user-images.githubusercontent.com/26062775/30021681-7b0b3a52-9168-11e7-9da7-3bec357ae4ef.png">
<img width="350" alt="t4" src="https://user-images.githubusercontent.com/26062775/30021699-8a361d3a-9168-11e7-876e-db6681d9983e.png">
<pre align="center">Type-3-MT                                     Type-4</pre>
</p>


We sort the X-axis based on functionality numbers recorded in BigCloneBench. Then, we annotate functionality IDs to top/bottom four recall values. 

## Analysis of false postives
Although it is one of the highest-quality and largest benchmarks available to the research community, BigCloneBench clone information may not be complete. BigCloneBench is built via an incremental additive process (i.e., gradually relaxing search queries) based on keyword and source pattern matching. Thus, it may miss some clones despite the manual verification. 
Therefore, we manually verify the clone pairs that are not associated in BigCloneBench, but FaCoY recommended as code clones, i.e., false positives. Our objective is then to verify to what extent they are indeed false positives and not misses by BigCloneBench. We sample 10 false positives per clone type category for manual check. For 58 out of 60 cases, it turns out that they are actually true positives. Specifically, BigCloneBench missed four Type-1, one Type-2, two Type-3, and 25 Type-4 clones. In some cases, there is a method call dependency between the false positive and the actual clone. In 26 cases, point to the same file but another location than actual clones. Two are actual false positives.

***FaCoY performs better for functionalities requiring external APIs and libraries than those with pure computation tasks. In addition, our approach’s even can detect clones that BigCloneBench missed with a high probability.**

False positive samples are listed and available [here](/evaluation/bigclone/false_positive_samples)



[logo]: https://github.com/facoy/facoy/FaCoY_Logo.png
