define nlist as int|[nat]

nlist f(int i, [nlist] xs):
    if i < 0 || i >= |xs|:
        return 0
    else:
        return xs[i]

void System::main([string] args):
    nlist x = f(2, [2,3,4])    
    print str(x)