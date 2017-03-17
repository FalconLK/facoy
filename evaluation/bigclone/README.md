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
