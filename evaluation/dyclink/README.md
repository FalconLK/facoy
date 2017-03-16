# Validating with DyCLINK

DyCLINK is a system for detecting methods having similar runtime behavior at instruction level. DyCLINK constructs dynamic instruction graphs based on execution traces for methods and then conducts inexact (sub)graph matching between each execution of each method. The methods having similar (sub)graphs are called "code relatives", because they have relevant runtime behavior. The information about how DyCLINK works can be found in a [FSE 2016 paper](http://dl.acm.org/citation.cfm?doid=2950290.2950321).

## Find semantic clones also detected by DyCLINK
