# Running FaCoY on IJaDataset and evaluating on BigCloneBench (RQ2)

## Virtual Machine
We have created a virtual machine to allow researchers and developers to use FaCoY easily. Everyone can download the virtual machine [here](https://) including FaCoY and all the required software. 

We set up the credential, “user” as the username and “1234” as the password, for the VM. The home directory of FaCoY is /home/user/Desktop/FACoY. The credential for the database is “root” as the username and “1234” as the password.

## How to import & use the search engine FaCoY?
1. Install the **Virtualbox** and import the virtual system using the "Virtual System Import" button in the file tab.
2. Activate the VM_FaCoY.
3. Type the password "1234" to login as the "user" account.
4. Open a terminal & activate the search engine by typing below.

```
cd /home/user/Desktop/FACoY/GitSearch
jython bootstrap.py ./FrontEnd/server_IJaData.py
```

When you can see the message "* Running on http://0.0.0.0:5000/ (Press Ctrl+C to quit)", it is ready.

5. Open the firefox browser & let's go to http://0.0.0.0:5000
6. Now the search engine may be showed up. You can put any source code snippet into the text box. If you want to use sample code snippets, you can find several samples in the path "/home/user/Desktop/FACoY/Sample Codes/IJa_samples". 

***Each step might take some time since it is running on a virtual machine.**


## IJaDataset
IJaDataset is a large java inter-project software repository built from [SeClone project](https://sites.google.com/site/asegsecold/projects/seclone) by Ambient Software Evoluton Group. The current version is IJaDataset-2.0 which is available at the [BigCloneBench page](https://github.com/clonebench/BigCloneBench/blob/master/README.md) otherwise we should ask [SeClone project](https://sites.google.com/site/asegsecold/projects/seclone) site administrator.

## BigCloneBench
BigCloneBench is a clone detection benchmark of known true and false clones mined from the big data inter-project repository IJaDataset-2.0 (25,000 subject systems, 365MLOC).. The current version of the benchmark BigCloneBench is available at [BigCloneBench page](https://github.com/clonebench/BigCloneBench/blob/master/README.md).

## Analysis of false postives
Although it is one of the highest-quality and largest benchmarks available to the research community, BigCloneBench clone information may not be complete. BigCloneBench is built via an incremental additive process (i.e., gradually relaxing search queries) based on keyword and source pattern matching. Thus, it may miss some clones despite the manual verification. 
Therefore, we manually verify the clone pairs that are not associated in BigCloneBench, but FaCoY recommended as code clones, i.e., false positives. Our objective is then to verify to what extent they are indeed false positives and not misses by BigCloneBench. We sample 10 false positives per clone type category for manual check. For 58 out of 60 cases, it turns out that they are actually true positives. Specifically, BigCloneBench missed four Type-1, one Type-2, two Type-3, and 25 Type-4 clones. In some cases, there is a method call dependency between the false positive and the actual clone. In 26 cases, point to the same file but another location than actual clones. Two are actual false positives.

***FaCoY performs better for functionalities requiring external APIs and libraries than those with pure computation tasks. In addition, our approach’s even can detect clones that BigCloneBench missed with a high probability.**
