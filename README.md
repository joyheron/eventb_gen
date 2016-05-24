# Generate Event-B Projects using the ProB 2.0 API

[![Build Status](https://travis-ci.org/joyclark/eventb_gen.svg)](https://travis-ci.org/joyclark/eventb_gen/)

This library provides an interface to generate Event-B projects from text machine and context descriptions. 
The format for the machine and context descriptions is determined by the eventbalg parser available from the [ProB Parsers](https://github.com/bendisposto/probparsers).
This is the structural Event-B parser currently used for the Camille text editor which is extended to provide support for algorithm descriptions.

The build tool gradle (http://www.gradle.org/) is required in order to set up the project so that it can be run from within Eclipse.

In order to setup the eclipse project run: gradle eclipse (or ./gradlew eclipse if you do not have grade installed)

### Use the following arguments to run the application:
* **path** *path/to/directory* - This argument is required. The application locates the directory on the local file system and attempts to parse and load all .emch and .ctx files in the specified directory
* **name** *name* - This argument specifies the name of the generated Event-B project. If not specified, a generic name for the project will be chosen (i.e. *modelgen*)
* **debug** - If this flag is set, debug information about the model will be printed
* **generate** - run the algorithm translation to generate sequential models based on an algorithm description
* **mergeBranches** - merge branches within the control flow graph during algorithm translation in order to optimize the result
* **optimize** - optimize the translation algorithm to merge branches with succeeding assignments and decrease the number of events in the generated model
* **propagateAssertions** - propagate loop invariants and assertions within a specified algorithm to generate inductive invariants
* **terminationAnalysis** - generate a variant and helpful assertions to prove algorithm termination
* **loopEvent** - add extra event to end of a while loop in the translation
* **default** - run generate Event-B specification with default options for algorithm translatio

### Example model
An example model can be found in the directory models/gcd/.
Machine description files are expected to have a .emch file extension.
Context description files are expected to have a .ctx file extension.
Files that contain incorrect Event-B models will result in exceptions.

To generate an algorithm from the example, run de.prob2.gen.Main with the following arguments:
```
-name GCD -path models/gcd/ -generate [OPTIONS]
```

or to generate an algorithm using the default options, run with the arguments:
```
-name GCD -path models/gcd/ -default
```

# Download EventBGen

A deployed version of the EventBGen tool can be downloaded as a JAR from [here](https://www3.hhu.de/stups/downloads/eventb_gen/).

# ProB 2.0 source code
The source code of the current ProB 2.0 development is located at http://github.com/bendisposto/prob2

(c) 2014 Joy Clark et.al. , all rights reserved
