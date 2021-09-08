## Arith
```c
// Arith.txt
{
    int a; int b; int c; float x; float y;

    x = a * b + c - a * y;

}
```
* ** Parser:  `stmt()` -> `assign()` ->`Set(id, bool())` -> `Arith()` (return Set)**<br>
* ** Code Generator: `Set(id, bool()).gen().toString()` -> `Arith(op, expr1.reduce(), expr2.reduce())`**

### Code Graph
**`x = a * b + c - a * y`**<br>
```
Arith(-, Arith(+, Arith(*, Id(a), Id(b)), Id(c)), term()), Arith(*, Id(a), Id(y))) 
```
1. `Arith(*, Id(a), Id(b)).reduce()` `-emit->` **t1 = a * b** `-return->` **t1**
2. `Id(c).reduce()` `-return->` **c**
3. `Arith(+, t1, c)` `-emit->` **t2 = t1 + c** `-return->` **t2**
4. `Arith(*, a, y)` `-emit->` **t3 = a * y** `-return->` **t3**
5. `Arith(-, t2, t3)` `-emit->` **x = t2 - t3**


```
❯ java main.Main < test/Arith.txt
L1:     t1 = a * b
        t2 = t1 + c
        t3 = a * y
        x = t2 - t3
L2:
```


## Access
```c
// Access.txt
{
    int[3] a;
    
    a[0] = 0;
    a[1] = 1;
    a[2] = 2;

    while (a[2] > 0) {
	a[0] = a[0] + 1;
	a[1] = a[1] + 1;
	a[2] = a[2] - 1;
    }
}
```

```
❯ java main.Main < test/Access.txt
L1:     t1 = 0 * 4
        a[t1] = 0
L3:     t2 = 1 * 4
        a[t2] = 1
L4:     t3 = 2 * 4
        a[t3] = 2
L5:     t4 = 2 * 4
        t5 = a[t4]
        iffalse t5 > 0 goto L2
L6:     t6 = 0 * 4
        t7 = 0 * 4
        t8 = a[t7]
        t9 = t8 + 1
        a[t6] = t9
L7:     t10 = 1 * 4
        t11 = 1 * 4
        t12 = a[t11]
        t13 = t12 + 1
        a[t10] = t13
L8:     t14 = 2 * 4
        t15 = 2 * 4
        t16 = a[t15]
        t17 = t16 - 1
        a[t14] = t17
        goto L5
L2:
```

## IF


```c
// If.txt
{
    int i; 
    i = 2;
    if (i > 3) {
        i = i - 1;
    }
}
```
* **Parser: `stmt()` -> `If(bool(), stmt())`**
* **Code Generator: `If(bool(), stmt()).gen()` -> `bool().jumping(0, after)` -> `stmt.gen()`**

```
❯ java main.Main < test/If.txt
L1:     i = 2
L3:     iffalse i > 3 goto L2
L4:     i = i - 1
L2:
```

## While

```c
// While.txt
{
    int i; 
    i = 1;
    while (i <= 10) {
	i = i + 1;
    }
}
```

* **Parser: `stmt()` -> `While().init(bool(), stmt())`**
* **Code Generator: `While.gen()` -> `bool().jumping(0, after)` -> `stmt.gen()`**

```
❯ java main.Main < test/While.txt
L1:     i = 1
L3:     iffalse i <= 10 goto L2
L4:     i = i + 1
        goto L3
L2:
```

## Else

```c
// Else.txt
{
    int i;
    i = 10;
    if (i > 9) {
        i = i + 1;
    } else {
        i = i - 1;
    }
}

```

* **Parser: `stmt()` -> `Else(bool(), stmt(), stmt())`**
* **Code Generator: Emit two new lables (other just similar to if and while)**

```
❯ java main.Main < test/Else.txt
L1:     i = 10
L3:     iffalse i > 9 goto L5
L4:     i = i + 1
        goto L2
L5:     i = i - 1
L2:

```
