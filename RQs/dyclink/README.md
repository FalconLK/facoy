# Validating with DyCLINK

We build on DyCLINK, a dynamic approach that computes similarity of execution traces to detect that two code fragments are relatives (i.e., that they behave (functionally) similarly). 

DyCLINK has been applied to programs written for Google Code Jam competitions to identify code relatives at the granularity of methods. We carefully reproduced their results with the publicly available version of DyCLINK. Among the 642 methods in the code base, DyCLINK considers 411 pairs as code relatives.

The information about how DyCLINK works can be found in a [FSE 2016 paper](http://dl.acm.org/citation.cfm?doid=2950290.2950321).

## Find semantic clones also detected by DyCLINK
We consider all methods for which DyCLINK finds a relative and use FaCoY to search for its clones in the Google Code Jam dataset, and we check that the found clones are relatives of the input. 

As a result, FaCoY can identify 278 out of 411 code relatives and the hit ratio is 68%. On the other hand, MRR is 0.18, which means FaCoY recommends the code relatives into lower rankings.

- The used Google Code Jam data is available [here](/RQs/dyclink/GoogleCodeJam_data) 
- The basic name mapping information is available [here](https://drive.google.com/open?id=0B2btZBiPsouGNmx6Y2xCVXUwbUk) 
- The used queries are also available [here](/RQs/dyclink/Used_queries).
- The result can be checked in [here](https://drive.google.com/open?id=0B2btZBiPsouGT2o3eVU2c2txdmM).

Since most codes in Google Code Jam often use variables with no meaning (such as void s(int a){}), FaCoY cannot find related code in StackOverflow and thus cannot build alternate queries, limiting the hit ratio. On the other hand, since DyCLINK also uses a similarity metric to decide on code relativeness, the MRR score of FaCoY could be higher with a more relaxed threashold (currently set at 82%) in DyCLINK.

***FaCoY can indeed find semantic clones that exhibit similar runtime behavior.**

