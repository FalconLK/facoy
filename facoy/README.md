# FaCoY implementation

## Using VM (VirtualBox)

We provide a virtual machine, which is ready for running the FaCoY service, upon request. Please find the attached VM [here!.](https://drive.google.com/file/d/0B6ONWzofocX_QW01ZWVlZ1lFU3M/view?usp=sharing)

(This is just temporary because of anonymity during double-blind review period.)

## Building and running from Source code
We set up the credential, “user” as the username and “1234” as the password, for the VM. The home directory of FaCoY is /home/user/Desktop/FACoY. The credential for the database is “root” as the username and “1234” as the password.

1. Install the **Virtualbox** and import the virtual system using the "Virtual System Import" button in the file tab.
2. Activate the VM_FaCoY.
3. Type the password "1234" to login as the "user" account.
4. Open a terminal & activate the search engine by typing below.

```
cd /home/user/Desktop/FACoY/GitSearch
jython bootstrap.py ./FrontEnd/server_GitHub.py
```

When you can see the message "* Running on http://0.0.0.0:5000/ (Press Ctrl+C to quit)", it is ready.

5. Open the firefox browser & let's go to http://0.0.0.0:5000
6. Now the search engine may be showed up. You can put any source code snippet into the text box. If you want to use sample code snippets, you can find several samples in the path "/home/user/Desktop/Sample Codes/Git_samples". 

***Each step might take some time since it is running on a virtual machine.**


## Implementation options

(list of token types on the indexing)

|           Type         |     Description              |
|           ---:         |     :---                     |
| used_class             |Name of a used class          |
| class_instance_creation|Class instance creation       |
| extends                |Super class or interface      |
| method_declaration     |Name of method declared       |
| import                 |Name package or class imported|
| typed_method_call      |(Partially) qualified name of called method|
| unresolved_method_call |Non-qualified name of called method|
| str_literal            |String literal used in code   |
| comments               |Comments of the developers    |
| line_number            |Line number of a specific code|
| line_range             |Line range of a specific code snippet|

