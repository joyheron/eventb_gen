gcd: build/libs/eventb_gen.jar
	java -jar build/libs/eventb_gen.jar -name GCD -path models/gcd/ -generate

build/libs/eventb_gen.jar: src/main/java/de/prob2/gen/*.java
	./gradlew uberjar

ll: build/libs/eventb_gen.jar
	java -jar build/libs/eventb_gen.jar -name LL -path models/ll_parsing/ -generate

term: build/libs/eventb_gen.jar
	java -jar build/libs/eventb_gen.jar -name TermTest -path models/termination_test/ -generate -terminationAnalysis

binary: build/libs/eventb_gen.jar
	java -jar build/libs/eventb_gen.jar -name binary -path models/binary_search/ -generate -mergeBranches -optimize
