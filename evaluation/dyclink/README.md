# Validating with DyCLINK

DyCLINK is a system for detecting methods having similar runtime behavior at instruction level. DyCLINK constructs dynamic instruction graphs based on execution traces for methods and then conducts inexact (sub)graph matching between each execution of each method. The methods having similar (sub)graphs are called "code relatives", because they have relevant runtime behavior. The information about how DyCLINK works can be found in a [FSE 2016 paper](http://dl.acm.org/citation.cfm?doid=2950290.2950321).

## Find semantic clones also detected by DyCLINK
DyCLINK has been applied to programs written for Google Code Jam competitions to identify code relatives at the granularity of methods. We carefully reproduced their results with the publicly available version of DyCLINK. Among the 642 methods in the code base, DyCLINK considers 411 pairs as code relatives. We consider all methods for which DyCLINK finds a relative and use FaCoY to search for its clones in the Google Code Jam dataset, and we check that the found clones are relatives of the input. 

As a result, FaCoY can identify 278 out of 411 code relatives and the hit ratio is 68%. On the other hand, MRR is 0.18, which means FaCoY recommends the code relatives into lower rankings.

Since many most programs in Google Code Jam often use variables with no meaning (such as void s(int a){}), FaCoY cannot find related code in StackOverflow and thus cannot build alternate queries, limiting the hit ratio. On the other hand, since DyCLINK also uses a similarity metric to decide on code relativeness, the MRR score of FaCoY could be higher with a more relaxed threashold (currently set at 82%) in DyCLINK.

***FaCoY can indeed find semantic clones that exhibit similar runtime behavior.**
