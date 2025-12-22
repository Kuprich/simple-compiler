import type { SourceCode } from "@/types/compiler"

const mainCode = `public class Main {
  public static void main(String[] args) {
    Foo foo = new Foo("World");
    for (int i = 0; i < 3; i++) {
      foo.sayHello();
    }
  }
}`

const fooCode = `public class Foo {
  private final String name;

  public Foo(String name) {
    this.name = name;
  }

  public void sayHello() {
    System.out.println("Hello, " + name + "!");
  }
}`

export const DEFAULT_FILES: SourceCode[] = [
  {
    filename: 'Main.java',
    code: mainCode,
  },
  {
    filename: 'Foo.java',
    code: fooCode,
  },
]
