# Generate Event-B Projects using the ProB 2.0 API

This library provides an interface to generate Event-B projects from text machine and context descriptions. 
The format for the machine and context descriptions is determined by the eventbalg parser available from the [ProB Parsers](https://github.com/bendisposto/probparsers).
This is the structural Event-B parser currently used for the Camille text editor which is extended to provide support for algorithm descriptions.

The build tool gradle (http://www.gradle.org/) is required in order to set up the project so that it can be run from within Eclipse.

In order to setup the eclipse project run: gradle eclipse (or ./gradlew eclipse if you do not have grade installed)

### Use the following arguments to run the application:
* **path** *path/to/directory* - This argument is required. The application locates the directory on the local file system and attempts to parse and load all .emch and .ctx files in the specified directory
* **name** *name* - This argument specifies the name of the generated Event-B project. If not specified, a generic name for the project will be chosen (i.e. *modelgen*)
* **debug** - If this flag is set, debug information about the model will be printed
* **generate** - run an algorithm to generate Event-B models based on an algorithm description
* **naive** - naive algorithm for the generate of Event-B models based an algorithm description
* **mergeBranches** - merge branches within the control flow graph during algorithm translation in order to optimize the result
* **termination** - use in connection with 'naive' algorithm to generate specifications including a framework to help with termination proofs

### Example model
An example model can be found in the directory models/gcd/.
Machine description files are expected to have a .emch file extension.
Context description files are expected to have a .ctx file extension.
Files that contain incorrect Event-B models will result in exceptions.

To generate an algorithm from the example, run de.prob2.gen.Main with the following arguments:
```
-name GCD -path models/gcd/ -generate
```

# ProB 2.0 source code
The source code of the current ProB 2.0 development is located at http://github.com/bendisposto/prob2

(c) 2014 Joy Clark et.al. , all rights reserved
