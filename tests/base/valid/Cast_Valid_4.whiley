define R1 as { real x, int y }
define R2 as { int x, real y }
define R3 as { int x, int y }

R1|R2 f(R3 i):
    return (R1) i

void System::main([string] args):
    out.println(str(f({x:123542, y:123})))
    