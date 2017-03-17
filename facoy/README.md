# FaCoY implementation

## Using VM (VirtualBox)

We provide a virtual machine, which is ready for running the FaCoY service, upon request. Please send an email here: facoyexp@gmail.com 

(This is just temporary because of anonymity during double-blind review period.)

## Building and running from Source code
We set up the credential, “user” as the username and “1234” as the password, for the VM. The home directory of FaCoY is /home/user/Desktop/FACoY. The credential for the database is “root” as the username and “1234” as the password.

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


## Implementation options

(list of token types on the indexing)

| Type  | Description |
| ---:  | :---        |
|hi|hi|
