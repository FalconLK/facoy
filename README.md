![facoy_logo](https://user-images.githubusercontent.com/26062775/29922353-86fa005c-8e55-11e7-88ef-42a735cf42c4.png)

# FaCoY
This repository shares data and code used for the FaCoY project.

## What it is?
FaCoY is a code search engine that receives code fragments from users and retrieve the related code examples. While other code search engines directly match user free-form queries and source code, FaCoY translates and augments user queries into code-friendly queries. Further more, it considers semantic similarity for code examples.

## How it works?
FaCoY works based on open source java code from a super repository, GitHub and a Q & A posts from a Q & A forum, Stackoverflow. If you want to find code examples which are similar to yours, you can input your code fragment to the system, then you will see the symantically or syntactically similar code examples.

## Virtual Machine
https://github.com/FalconLK/facoy/tree/release-1.0/VM

## Finding/Evaluation
### RQ1
https://github.com/FalconLK/FaCoY/tree/release-1.0/evaluation/onlinequery

### RQ2
https://github.com/FalconLK/FaCoY/tree/release-1.0/evaluation/bigclone

### RQ3
https://github.com/FalconLK/FaCoY/tree/release-1.0/evaluation/dyclink

### RQ4
https://github.com/FalconLK/FaCoY/tree/release-1.0/evaluation/defects4J

## Maintance
@FalconLK Kisub Kim
@darksw Dongsun Kim
@Deadlyelder Sankalp

## Citation
```@inproceedings{kim2018facoy,
  title={FaCoY--A Code-to-Code Search Engine},
  author={Kim, Kisub and Kim, Dongsun and Bissyande, Tegawende F and Choi, Eunjong and Li, Li and Klein, Jacques and Le Traon, Yves},
  booktitle={Proceedings of the 40th International Conference on Software Engineering (ICSE)},
  year={2018}
}```
