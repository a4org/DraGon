# A Complete Front End 
##### Based on Chapter1-Chapter2 and Pg.965-987 of "The Dragon Book"

## Compile it

```shell
$ javac lexer/*.java
$ javac symbols/*.java
$ javac inter/*.java
$ javac parser/*.java
$ javac main/*.java
```

## Run it

```shell
$ java main.Main < YOURFILE.txt
```

## Example
### test.txt
```java
// test.txt
{
    int i; int j; float v; float x; float[100] a;
    j = 100;
    v = 0.78;
    while( true ) {
        i = i + 1;
        j = j + v;
        if( i >= j ) break;
    }

    do {i = i - 1;} while ( a[i] < v );

}
```
### Output
```
$ java main.Main < test/test.txt
L1:     j = 100
L3:     v = 0.78
L4:L6:  i = i + 1
L7:     j = j + v
L8:     iffalse i >= j goto L4
L9:     goto L5
        goto L4
L5:     i = i - 1
L10:    t1 = i * 8
        t2 = a[t1]
        if t2 < v goto L5
L2:
```
