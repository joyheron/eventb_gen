gcd: build/libs/eventb_gen.jar
	java -jar build/libs/eventb_gen.jar -name GCD -path models/gcd/ -generate
build/libs/eventb_gen.jar:
	gradle uberjar
ll: build/libs/eventb_gen.jar
	java -jar build/libs/eventb_gen.jar -name LL -path models/ll_parsing/ -generate