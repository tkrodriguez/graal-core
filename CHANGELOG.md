# Graal Changelog

This changelog summarizes major changes between Graal versions relevant to developers building technology on top of Graal. The main focus is on APIs exported by Graal but other significant performance/stability changes are mentioned as well.

## `tip`
* CompileTheWorld now includes class initializers.

## Version 0.12
* Added initial code for AArch64 port.
* Moved @ServiceProvider mechanism from JVMCI to Graal.

## Version 0.11
23-Dec-2015, [Repository Revision](http://hg.openjdk.java.net/graal/graal-compiler/shortlog/graal-0.11)
* Moved support for command line options from JVMCI to Graal.
* Made invocation plugin initialization lazy: plugins for a class are initialized first time compiler parses a method in the class.
* Removed method handle special case logic for 8u60 and later.
* Generate graph builder plugins for @NodeIntrinsic and @Fold methods instead of using reflection.
* Converted LoadHubNode into normal FloatingNode from FloatingGuardedNode.
* Enabled CRC32 intrinsics on SPARC.
* Added log methods to Debug with 9 and 10 arguments.

## Version 0.10
17-Nov-2015, [Repository Revision](http://hg.openjdk.java.net/graal/graal-compiler/shortlog/graal-0.10)
* Added experimental Trace Register Allocator.
* All JVMCI (and HotSpot) code has been moved into a [separate repository](http://hg.openjdk.java.net/graal/graal-jvmci-8/).
* JVMCI code is now in jdk.vm.ci.* name space.
* Graal now passes the gate on top of a JVMCI enabled JDK9 binary.
* Separate Graal compiler can be specified for Truffle compilation with new `-G:TruffleCompiler` option.
* Initialization of the Truffle compiler is delayed until first Truffle compilation request.

## Version 0.8
15-Jul-2015, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.8)
### Graal
* Add support for constructing low-level IR in SSA form (default behavior).
* Add support for SSA linear scan register allocation (default behavior).
* Remove dummy parameter `includeAbstract` from `ResolvedJavaType#resolveMethod()`; The behavior is now the `includeAbstract==true` case. The `includeAbstract==false` variant is available via `resolveConcreteMethod()`.
* HotSpot modifications have been renamed to JVMCI in preparation for [JEP 243](http://openjdk.java.net/jeps/243). As a result HotSpot options containing "Graal" have been changed to "JVMCI" (e.g., -XX:+BootstrapJVMCI).
* All the APIs used to interface with the VM (`api.meta`, `api.code` etc.) have been moved to `jdk.internal.jvmci` packages (e.g., `jdk.internal.jvmci.meta`).
* Fast JVMCI services do not need to implement an interface anymore, implementations simply need to be annotated with `jdk.internal.jvmci.service.ServiceProvider`.

### Truffle
* Moved Truffle to it own [repository](http://lafo.ssw.uni-linz.ac.at/hg/truffle/)

## Version 0.7
29-Apr-2015, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.7)
### Graal
* By default the Graal code is now only compiled by C1 which should improve application start-up.
* Merged with jdk8u40-b25.
* The Graal class loader now loads all lib/graal/graal*.jar jars.
* Fast Graal services (see com.oracle.graal.api.runtime.Service) are now looked up using service files in lib/graal/services.
* Add utilities ModifiersProvider#isConcrete, ResolvedJavaMethod#hasBytecodes, ResolvedJavaMethod#hasReceiver to Graal API.
* Add `GraalDirectives` API, containing methods to influence compiler behavior for unittests and microbenchmarks.
* Introduce `LIRSuites`, an extensible configuration for the low-level compiler pipeline.

### Truffle
* New, faster partial evaluation (no more TruffleCache).
* If a method is annotated with @ExplodeLoop and contains a loop that can not be exploded, partial evaluation will fail.
* Truffle background compilation is now multi-threaded.
* Experimental merge=true flag for @ExplodeLoop allows building bytecode-based interpreters (see BytecodeInterpreterPartialEvaluationTest).
* Added Node#deepCopy as primary method to copy ASTs.
* Disable inlining across Truffle boundary by default. New option TruffleInlineAcrossTruffleBoundary default false.
* Node.replace(Node) now guards against non-assignable replacement, and Node.isReplacementSafe(Node) checks in advance.
* Instrumentation:  AST "probing" is now safe and implemented by Node.probe(); language implementors need only implement Node.isInstrumentable() and Node.createWrapperNode().
* Instrumentation:  A new framework defines a category of  simple "instrumentation tools" that can be created, configured, and installed, after which they autonomously collect execution data of some kind.
* Instrumentation:  A new example "instrumentation tool" is a language-agnostic collector of code coverage information (CoverageTracker); there are two other examples.
* Removed unsafe compiler directives; use `sun.misc.Unsafe` instead.
* Removed `Node#onAdopt()`.

### Truffle-DSL
* Implemented a new generated code layout that reduces the code size.
* Changed all methods enclosed in a @TypeSystem must now be static. 
* Changed all methods enclosed in generated type system classes are now static.
* Deprecated the type system constant used in the generated type system classes. 
* Changed NodeFactory implementations are no longer generated by default. Use {Node}Gen#create instead of {Node}Factory#create to create new instances of nodes.
* Added @GenerateNodeFactory to generate NodeFactory implementations for this node and its subclasses.
* Deprecated @NodeAssumptions for removal in the next release.
* Deprecated experimental @Implies for removal in the next release.
* Added new package c.o.t.api.dsl.examples to the c.o.t.api.dsl project containing documented and debug-able Truffle-DSL use cases.
* Changed "typed execute methods" are no longer required for use as specialization return type or parameter. It is now sufficient to declare them in the @TypeSystem.
* Added @Cached annotation to express specialization local state.
* Added Specialization#limit to declare a limit expression for the maximum number of specialization instantiations.
* Changed syntax and semantics of Specialization#assumptions and Specialization#guards. They now use a Java like expression syntax.
* Changed guard expressions that do not bind any dynamic parameter are invoked just once per specialization instantiation. They are now asserted to be true on the fast path.
* Renamed @ImportGuards to @ImportStatic.
* Changed declaring a @TypeSystemReference for a node that contains specializations is not mandatory anymore.
* Changed types used in specializations are not restricted on types declared in the type system anymore.
* Changed nodes that declare all execute methods with the same number of evaluated arguments as specialization arguments do not require @NodeChild annotations anymore.
* Changed types used in checks and casts are not mandatory to be declared in the type system.

## Version 0.6
19-Dec-2014, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.6)
### Graal
* Merged with jdk8u25-b17.
* Added `com.oracle.graal.api.meta.Remote` interface for future support of remote and replay compilation.
* Changed name suite specification from `mx/projects.py` to `mx/suite.py`.
* Changed semantics (and signature) of `ResolvedJavaType#resolveMethod()` (old behavior available via `resolveConcreteMethod()`).
* Moved `ResolvedJavaField#read[Constant]Value` and `getMethodHandleAccess()` to `ConstantReflectionProvider`.

### Truffle
* Instrumentation: add Instrumentable API for language implementors, with most details automated (see package `com.oracle.truffle.api.instrument`).
* The BranchProfile constructor is now private. Use BranchProfile#create() instead.
* Renamed @CompilerDirectives.SlowPath to @CompilerDirectives.TruffleBoundary
* Renamed RootNode#isSplittable to RootNode#isCloningAllowed
* Removed RootNode#split. Cloning ASTs for splitting is now an implementation detail of the Truffle runtime implementation. 
* Renamed DirectCallNode#isSplittable to DirectCallNode#isCallTargetCloningAllowed
* Renamed DirectCallNode#split to DirectCallNode#cloneCallTarget
* Renamed DirectCallNode#isSplit to DirectCallNode#isCallTargetCloned
* Added PrimitiveValueProfile.
* Added -G:TruffleTimeThreshold=5000 option to defer compilation for call targets
* Added RootNode#getExecutionContext to identify nodes with languages
* Removed `FrameTypeConversion` interface and changed the corresponding `FrameDescriptor` constructor to have a default value parameter instead.
* Removed `CompilerDirectives.unsafeFrameCast` (equivalent to a `(MaterializedFrame)` cast).
* Added `TruffleRuntime#getCapability` API method.
* Added `NodeInterface` and allowed child field to be declared with interfaces that extend it.
* Added `CompilerOptions` and allowed it to be set for `ExecutionContext` and `RootNode`.
* Added experimental object API (see new project `com.oracle.truffle.api.object`).

## Version 0.5
23-Sep-2014, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.5)
### Graal
* New register allocator optimization: `-G:+ConstantLoadOptimization`.
* SPARC backend is able to run benchmark and passing most of the JTTs.
* Fix: Stamp: interface types can not be trusted except after explicit runtime checks.
* Changed format of suite specification from a properties file (`mx/projects`) to a Python file (`mx/projects.py`).


### Truffle
* Added `TruffleRuntime#getCallTargets()` to get all call targets that were created and are still referenced.
* Added `NeverValidAssumption` to complement `AlwaysValidAssumption`.
* Fixed a bug in `AssumedValue` that may not invalidate correctly.
* New option, `-G:+/-TruffleCompilationExceptionsAreThrown`, that will throw an `OptimizationFailedException` for compiler errors.

## Version 0.4
19-Aug-2014, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.4)
### Graal
* Made initialization of Graal runtime lazy in hosted mode.
* Added supported for new `jrelibrary` dependency type in `mx/projects`.
* Java projects with compliance level higher than the JDKs specified by `JAVA_HOME` and `EXTRA_JAVA_HOMES` are ignored once `mx/projects` has been processed.
* `ResolvedJavaType.resolveMethod` now takes a context type used to perform access checks. It now works correctly regarding default methods.
* Removed Java based compilation queue (`CompilationQueue.java`).
* Enabled use of separate class loader (via `-XX:+UseGraalClassLoader`) for classes loaded from `graal.jar` to hide them from application classes.

### Truffle
* Change API for stack walking to a visitor: `TruffleRuntime#iterateFrames` replaces `TruffleRuntime#getStackTrace`
* New flag `-G:+TraceTruffleCompilationCallTree` to print the tree of inlined calls before compilation.
* `truffle.jar`: strip out build-time only dependency into a seperated JAR file (`truffle-dsl-processor.jar`)
* New flag `-G:+TraceTruffleCompilationAST` to print the AST before compilation.
* New experimental `TypedObject` interface added.
* Renamed flag `-G:+TruffleSplittingEnabled` to `-G:+TruffleSplitting`
* New flag `-G:+TruffleSplittingNew` to enable the experimental splitting mode based on function arguments.
* New flag `-G:+TruffleSplittingTypedInstanceStamps` to enable splitting for `TypedObject` instances.
* New flag `-G:+TruffleSplittingClassInstanceStamps` to enable splitting for Java object instances except `TypedObject`.
* New flag `-G:TruffleSplittingStartCallCount=3` which sets the number of minimal calls until splitting is performed.
* New flag `-G:-TruffleSplittingAggressive` if enabled splits every function call.
* Added `isVisited` method for `BranchProfile`.
* Added new `ConditionProfile`, `BinaryConditionProfile` and `CountingConditionProfile` utility classes to profile if conditions.

## Version 0.3
9-May-2014, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.3)

### Graal
* Explicit support for oop compression/uncompression in high level graph.
* LIRGenerator refactoring.
* Explicit types for inputs (InputType enum).
* Added graal.version system property to Graal enabled VM builds.
* Transitioned to JDK 8 as minimum JDK level for Graal.
* Added support for stack introspection.
* New MatchRule facility to convert multiple HIR nodes into specialized LIR

### Truffle
* The method `CallTarget#call` takes now a variable number of Object arguments.
* Support for collecting stack traces and for accessing the current frame in slow paths (see `TruffleRuntime#getStackTrace`).
* Renamed `CallNode` to `DirectCallNode`.
* Renamed `TruffleRuntime#createCallNode` to `TruffleRuntime#createDirectCallNode`.
* Added `IndirectCallNode` for calls with a changing `CallTarget`.
* Added `TruffleRuntime#createIndirectCallNode` to create an `IndirectCallNode`.
* `DirectCallNode#inline` was renamed to `DirectCallNode#forceInlining()`.
* Removed deprecated `Node#adoptChild`.

## Version 0.2
25-Mar-2014, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.2)

### Graal
* Use HotSpot stubs for certain array copy operations.
* New methods for querying memory usage of individual objects and object graphs in Graal API (`MetaAccessProvider#getMemorySize`, `MetaUtil#getMemorySizeRecursive`).
* Added tiered configuration (C1 + Graal).
* Initial security model for Graal [GRAAL-22](https://bugs.openjdk.java.net/browse/GRAAL-22).
* New (tested) invariant that equality comparisons for `JavaType`/`JavaMethod`/`JavaField` values use `.equals()` instead of `==`.
* Made graph caching compilation-local.
* Added AllocSpy tool for analyzing allocation in Graal using the [Java Allocation Instrumenter](https://code.google.com/p/java-allocation-instrumenter/).
* Initial support for memory arithmetic operations on x86.
* Expanded Debug logging/dumping API to avoid allocation when this Debug facilities are not enabled.

### Truffle
* New API `TruffleRuntime#createCallNode` to create call nodes and to give the runtime system control over its implementation.
* New API `RootNode#getCachedCallNodes` to get a weak set of `CallNode`s that have registered to call the `RootNode`.
* New API to split the AST of a call-site context sensitively. `CallNode#split`, `CallNode#isSplittable`, `CallNode#getSplitCallTarget`, `CallNode#getCurrentCallTarget`, `RootNode#isSplittable`, `RootNode#split`.
* New API to inline a call-site into the call-graph. `CallNode#isInlinable`, `CallNode#inline`, `CallNode#isInlined`.
* New API for the runtime environment to register `CallTarget`s as caller to the `RootNode`. `CallNode#registerCallTarget`.
* Improved API for counting nodes in Truffle ASTs. `NodeUtil#countNodes` can be used with a `NodeFilter`.
* New API to declare the cost of a Node for use in runtime environment specific heuristics. See `NodeCost`, `Node#getCost` and `NodeInfo#cost`.
* Removed old API for `NodeInfo#Kind` and `NodeInfo#kind`. As a replacement the new `NodeCost` API can be used.
* Changed `Node#replace` reason parameter type to `CharSequence` (to enable lazy string building)
* Deprecated `Node#adoptChild` and `Node#adoptChildren`, no longer needed in node constructor
* New `Node#insert` method for inserting new nodes into the tree (formerly `adoptChild`)
* New `Node#adoptChildren` helper method that adopts all (direct and indirect) children of a node
* New API `Node#atomic` for atomic tree operations
* Made `Node#replace` thread-safe


## Version 0.1
5-Feb-2014, [Repository Revision](http://hg.openjdk.java.net/graal/graal/shortlog/graal-0.1)

### Graal

* Initial version of a dynamic Java compiler written in Java.
* Support for multiple co-existing GPU backends ([GRAAL-1](https://bugs.openjdk.java.net/browse/GRAAL-1)).
* Fixed a compiler bug when running RuneScape ([GRAAL-7](https://bugs.openjdk.java.net/browse/GRAAL-7)).
* Bug fixes ([GRAAL-4](https://bugs.openjdk.java.net/browse/GRAAL-4), [GRAAL-5](https://bugs.openjdk.java.net/browse/GRAAL-5)).

### Truffle

* Initial version of a multi-language framework on top of Graal.
* Update of the [Truffle Inlining API](http://mail.openjdk.java.net/pipermail/graal-dev/2014-January/001516.html).
