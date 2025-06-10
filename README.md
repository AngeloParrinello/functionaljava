# Tecniche di programmazione funzionale in Java
_Enrico Sasdelli @ MotorK_


1. Cosa aspettarsi da questo talk
2. Perché la programmazione funzionale
   3. dichiarativo vs procedurale
3. Immutability
4. Pure Function
5. Composability
6. Refactor
7. Wrap Around
8. Function Composition
9. Monad










1. Immutability
   1. what
   2. why?
   3. example
   4. builders -> methods
2. Purity
   1. Move side-effects outside functions
   2. SIDE EFFECT (DB ACCESS) -> PURE FUNCTION -> SIDE EFFECT (DB ACCESS)


1. Why functional?
   1. Declarative
   2. DSL
   3. Immutability & parallelism
2. Partial Application / Currying / Lazy Evaluation
3. Function.andThen, Function.identity, Function.compose
3. Wrap around
4. Exceptions in streams
5. Lazy Evaluation
6. Interfaces with only one method. Not because of FunctionalInterface but to identify Business operations.
   7. publish/unpublish