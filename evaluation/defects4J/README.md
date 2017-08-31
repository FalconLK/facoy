# Searching patch candidates using FaCoY with buggy codes from Defects4J (RQ4).

We put 395 buggy codes from [Defects4J benchmark](https://github.com/rjust/defects4j).

Since we have a indexed github data, FaCoY can also find the exact same file. The goal was to find similar code snippets that can help correct bugs while the candidates were not from the exact same file. 

We found 21 patches among 395 bugs, project-specific results are below.<br />
Lang: 6/65<br />
Math: 5/106<br />
Mockito: 3/38<br />
Time: 2/27<br />
Closure: 2/133<br />
Chart: 3/26<br />

## All the queries we put into the FaCoY.
[]: Ranking for the similar code snippet (highest rank)
*: GitHub commit hash
^: SVN revision number

[Lang_11](/evaluation/defects4J/snippets/Lang_11) [2nd]
*80452c7a42777513c35fd30c4e12bcd7ee438fb9

[Lang_14](/evaluation/defects4J/snippets/Lang_14) [8th]
*cf7211f9d7d70d56501d8c4c827bf9ce3cac5f0b

[Lang_32](/evaluation/defects4J/snippets/Lang_32) [1st]
*006fca88e86bd6f650d4d021d2ff3573a572827d

[Lang_37](/evaluation/defects4J/snippets/Lang_37) [1st]
*ea140fb5c327e2b58f6c5bf1057c7dede909a50c

[Lang_40](/evaluation/defects4J/snippets/Lang_40) [5th]
*8b1a8e178abb46cc048a3982636b8ec4e6ffc8dc

[Lang_48](/evaluation/defects4J/snippets/Lang_48) [12nd]
*eb8f74efb75e71fc91e515a38becc2aac203e339 

[Math_13](/evaluation/defects4J/snippets/Math_13) [3rd]
*8079ea5b8d1366445da532906e43afa9291473cf

[Math_87](/evaluation/defects4J/snippets/Math_87) [2nd]
*75f5c92aeb47e264c196a8c38a495adac89f493c 

[Math_89](/evaluation/defects4J/snippets/Math_89) [2nd]
*62b3877f953dd47c4d301be35c77446e2cf55311 

[Math_103](/evaluation/defects4J/snippets/Math_103) [1st]
*4ce05bcd51ec956d789d20b59c743603d24a8ab7

[Math_106](/evaluation/defects4J/snippets/Math_106) [1st]
*41ba9e00e3bbde990f6821f67f0da2a5575b9ac3 

[Mockito_2](/evaluation/defects4J/snippets/Mockito_2) [1st]
*80452c7a42777513c35fd30c4e12bcd7ee438fb9 

[Mockito_9](/evaluation/defects4J/snippets/Mockito_9) [2nd]
*31f908029b8cd9b1f32bed3a41651b30ebb47b9f 

[Mockito_29](/evaluation/defects4J/snippets/Mockito_29) [2nd]
*918f0a5aed6454b307004b6c9c86afc8e96353ff

[Time_3](/evaluation/defects4J/snippets/Time_3) [10th]
*8d109fe1a999a11b4557536dd96f9210460a5936 

[Time_15](/evaluation/defects4J/snippets/Time_15) [11st]
*0cefc4c212e92e5dccdfa658785c419483317558 

[Closure_20](/evaluation/defects4J/snippets/Closure_20) [1st]
*ebf229b05f4ee71bc05d70830f5dd5683271d661 

[Closure_42](/evaluation/defects4J/snippets/Closure_42) [14th]
*fb01427a445ebfeb3af5e220a98ccaced339b73e 

[Chart_15](/evaluation/defects4J/snippets/Chart_15) [15th]
^749

[Chart_18](/evaluation/defects4J/snippets/Chart_18) [1st]
^621

[Chart_25](/evaluation/defects4J/snippets/Chart_25) [2nd]
^164
