package utils.annotations

import scala.annotation.StaticAnnotation

class Test(val priority: Int = Int.MaxValue) extends StaticAnnotation // Define the @Test annotation with a priority attribute

