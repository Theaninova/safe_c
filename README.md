# Safe Heap

![Build](https://github.com/wulkanat/safe_c/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
Rust, but it's C. Static memory safety with no runtime overhead whatsoever.
This requires an extra step in the compilation process to check heap, once
you fix all errors the code will compile with any C compiler and be safe.
<!-- Plugin description end -->

## Concept

This should not introduce any additional overhead, the source file should also
still compile as-is and still be memory-safe.

A variable that is located on the heap can only have one owner that is responsible
for deallocating it. You can grant usage access to others by borrowing it. A
borrowed variable cannot be deallocated or moved, and cannot be converted
back to an owned variable. You cannot borrow a variable to another thread.

The second part is moving variables, or better said assigning it to something else.
Usually when you allocate a variable in heap, it will be done in a function.
That means that the function will be the owner of the variable, and thus must
deallocate it once it goes out of scope. If it however passes ownership to 
another scope, for example by passing it to a function or assigning it to
a global variable, it is no longer responsible for it, but it also cannot use
it anymore as it could have been deallocated.

## Usage

TODO;

## Implementation

### Borrowing

This part can be solved completely just using C macros.
I will use an `int*` for this example:
```c
#define HEAP_TYPE(type)       \
typedef union {               \
     type *const raw;         \
} type##_borrowed;            \
typedef union {               \
    type *const raw;          \
    type##_borrowed borrowed; \
} type##_owned

HEAP_TYPE(int)
```
This introduces no runtime overhead, after compiling it will be just a pointer.

A function that borrows a variable will use the borrowed type
```c
void print(int_borrowed value) {
    printf("%d", *value.raw)
}
```
You cannot pass an `int*` or `int_owned` to that function. You also can't assingn
a value to the raw pointer directly (if you don't dereference it).
Because of that, it can be passed down functions indefinitely without risking
memory safety.

If you want to borrow an owned variable, it is easy too
```c
int main() {
    int_owned variable = ...;
    print(variable.borrowed);    
}
```
Again, in memory, this is still just a pure `int*`

### Allocation

Here it is important that a borrowed variable cannot be deallocated.

Allocation can be solved by a simple macro:
```c
#define new(type) { .raw = calloc(sizeof(type##_owned), 1) }

int_owned variable = new(int);
```
This even works inline and is very readable!

Deallocation should has to make sure that it doesn't work on borrowed variables.
This is easily done by doing this:
```c
#define delete(variable) free(variable.borrowed.raw)
```
It might seem a little counterintuitive why you would access the borrowed
variable, but it is a simple way to make sure you are dealing with
an owned variable. After all, the borrowed variable doesn't have a
`borrowed` field (or whatever you want to call that).

Of course, never use `malloc()`, `calloc()` or `free()` manually or you break
memory safety.

You can enforce this by using
```c
#define malloc ^^^
#define calloc ^^^
#define free ^^^
```
As this is invalid code, calling any of those, the C compiler will complain.
It should be noted that **I don't recommend doing this**.

### Ownership

Checking ownership can unfortunately not be accomplished just using macros 😐

This is why we need this CLion plugin.

#### What it currently catches

##### Top level borrows
```c
HEAP_TYPE(int)

// error!
int_borrowed top_level_borrow;
```
##### Using moved variable
```c
int_owned bar;

void foo(int_borrowed foo) { }

int main() {
    int_owned foo = new(int); 
    bar = foo;
    // error!
    foo(foo.borrowed);
}
```
##### Using deleted variable
```c
int_owned foo = new(int); 
delete(foo);
// error!
foo(foo.borrowed);
```
##### Missing delete
```c
int main() {
    int_owned foo = new(int);
    // error!
    return 0;
}
```
#### What it doesn't catch (yet)
##### Missing delete at function end
```c
void foo() {
    int_owned foo = new(int);
    // no error :(
}
```
##### Delete out of scope
```c
int main() {
    int_owned foo = new(int);

    if (some_condition) {
        delete(foo);
        // no error :(
    }
}
```
##### Assigning a variable without deleting the old data
```c
int_owned foo = new(int);
// no error :(
foo = new(int);
```
##### Missing delete for global variables
```c
int_owned foo = new(int);

int main() {
    foo = new(int);
   
    // no error :(
    return 0;
}
```
##### Using raw field
```c
int_owned foo = new(int);
// no error :(
foo.raw
```
##### Using raw pointer
```c
// no error :(
int *foo = calloc(sizeof(int), 1);
```
##### Structs
Variables in structs are more of a general problem... later.