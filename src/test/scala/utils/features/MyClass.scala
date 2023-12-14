package utils.features

import utils.annotations.{Ignore, RunFirst, RunLast, Test}

class MyClass {
  @RunFirst def firstMethod(): Unit = println("First method")

  @Test(priority = 2) def secondPriority(): Unit = println("Second priority test")

  @RunLast def lastMethod(): Unit = println("Run last method")

  @Ignore def importantMethod(): Unit = println("Very Important method()")

  @Test(priority = 1) def firstPriority(): Unit = println("First priority test")
}

