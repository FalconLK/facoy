# Searching patch candidates using FaCoY with buggy codes as the input queries (RQ4).

We put 395 buggy codes from [Defects4J benchmark](https://github.com/rjust/defects4j).

Since we have a indexed github data, FaCoY can also find the exact same file. 

The goal was to find similar code snippets that can help correct bugs while the candidates were not from the exact same file. 

We found 21 patches among 395 bugs, project-specific results are Lang: 6/65, Mockito: 3/38, Chart: 3/26, Closure: 2/133, Time: 2/27, and Math: 5/106.

## All the queries we put into the FaCoY.

[Lang_11](/evaluation/defects4J/snippets/Lang_11) candidate ranked 2nd
80452c7a42777513c35fd30c4e12bcd7ee438fb9
