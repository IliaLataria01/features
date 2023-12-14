package utils.features

import utils.annotations._

import scala.reflect.runtime.universe

object MethodRunner {
  def runMethods(target: AnyRef): Unit = {
    val mirror = universe.runtimeMirror(target.getClass.getClassLoader)
    val instanceMirror = mirror.reflect(target)
    val methods = instanceMirror.symbol.typeSignature.members

    // Partition methods based on @RunFirst, @RunLast, and @Test annotations
    val (testMethods, nonTestMethods) = methods
      .filter(m => m.isMethod && !m.asMethod.isConstructor)
      .partition(m => m.annotations.exists(_.tree.tpe =:= universe.typeOf[Test]))

    val (annotatedMethods, otherMethods) = nonTestMethods
      .partition(m => m.annotations.exists(ann =>
        ann.tree.tpe =:= universe.typeOf[RunFirst] || ann.tree.tpe =:= universe.typeOf[RunLast]))

    val (firstMethods, lastMethods) = annotatedMethods.partition(m =>
      m.annotations.exists(_.tree.tpe =:= universe.typeOf[RunFirst]))

    // Invoking @RunFirst methods
    firstMethods
      .filter(methodFilter)
      .map(_.asMethod)
      .foreach(invokeMethod(instanceMirror, _))

    // Invoking @Test methods sorted by priority
    testMethods
      .filter(methodFilter)
      .toSeq
      .map(_.asMethod)
      .sortBy(getPriority)
      .foreach(invokeMethod(instanceMirror, _))

    // Invoking other methods
    otherMethods
      .filter(methodFilter)
      .map(_.asMethod)
      .foreach(invokeMethod(instanceMirror, _))

    // Invoking @RunLast methods
    lastMethods
      .filter(methodFilter)
      .map(_.asMethod)
      .foreach(invokeMethod(instanceMirror, _))
  }

  private def getPriority(method: universe.MethodSymbol): Int = {
    method.annotations
      .find(_.tree.tpe =:= universe.typeOf[Test])
      .flatMap(_.tree.children.tail.collectFirst { case universe.Literal(universe.Constant(priority: Int)) => priority })
      .getOrElse(Int.MaxValue)
  }

  private def methodFilter(m: universe.Symbol): Boolean = {
    val method = m.asMethod
    !method.isAbstract &&
      method.paramLists.flatten.isEmpty &&
      !method.isJava &&
      method.owner != universe.typeOf[Object].typeSymbol &&
      method.owner != universe.typeOf[Any].typeSymbol &&
      !m.annotations.exists(_.tree.tpe =:= universe.typeOf[Ignore])
  }

  private def invokeMethod(instanceMirror: universe.InstanceMirror, method: universe.MethodSymbol): Unit = {
    try {
      val methodMirror = instanceMirror.reflectMethod(method)
      methodMirror.apply()
    } catch {
      case e: Exception => println(s"Error invoking method ${method.name}: ${e.getMessage}")
    }
  }
}



// Example
object Main extends App {
  val myClassInstance = new MyClass
  MethodRunner.runMethods(myClassInstance)
}