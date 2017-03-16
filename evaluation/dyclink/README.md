# Validating with DyCLINK

DyCLINK is a system for detecting methods having similar runtime behavior at instruction level. DyCLINK constructs dynamic instruction graphs based on execution traces for methods and then conducts inexact (sub)graph matching between each execution of each method. The methods having similar (sub)graphs are called "code relatives", because they have relevant runtime behavior.

## Find semantic clones also detected by DyCLINK
