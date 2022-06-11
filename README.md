# Versus

## Abstract

Comparison table is an efficient tool for comparing a small number of entities for decision making to analyze the main similarities and differences. The manual choice of their comparison features remains a complex and tedious task. This paper presents versus, which is the first automatic method for generating comparison tables from knowledge bases of the Semantic Web. For this purpose, we introduce the contextual reference level to evaluate whether a feature is relevant to compare a set of entities. This measure relies on contexts that are sets of entities similar to the compared entities. Its principle is to favor the features whose values for the compared entities are reference (or frequent) in these contexts. We show how to select these contexts and how to efficiently evaluate the contextual reference level from a public SPARQL endpoint limited by a fair-use policy. Using our publicly available benchmark based on Wikidata, the experiments show the interest of the contextual reference level for identifying the features deemed relevant by users with high precision and recall. In addition, the proposed optimizations significantly reduce the execution time and the number of required queries.

## Publication

Arnaud Giacometti, Béatrice Markhoff and Arnaud Soulet. *Comparison Table Generation from Knowledge Bases.* ESWC 2021.

## Web site

[https://lovelace-vs-turing.com/](https://lovelace-vs-turing.com/)

## How to use Versus?

1. Configure the SPARQL endpoint in versus/Versus (Wikidata by default)
2. Select your entities with the method versus (Ada Lovelace and Alan Turing by default) in versus/VersusTest
3. Select the minimum contextual reference level with teh method generate in versus/VersusTest
4. Run versus/VersusTest as the main class

	```
	cd bin
	java -cp ./*:../lib/*:../lib/jena/* versus.VersusTest
	```
	two compare the Wikidata items Q16766305, Q19841877, Q111967621, and store the output in a file (log output by default goes to stderr)
	
	```
	java -cp ./*:../lib/*:../lib/jena/* versus.VersusTest -f Q16766305 Q19841877 Q111967621 
	```
	or if compiled as jar:
	```
	java -jar versus.jar -f Q16766305 Q19841877 Q111967621 
	```
	try option ’-h’ to see more possibillities.
