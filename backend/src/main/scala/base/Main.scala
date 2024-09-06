package base

import scala.language.implicitConversions
import scala.reflect.runtime.universe._
import base.di.Provided

object di {
  // Provides a dependency of type A given dependencies encoded by type R
  sealed case class Provider[-R, +A] private[di] (constructor: R => A) {
    // Memoization is necessary so that constructor is run only once per
    // dependency
    // private var memo: A = null.asInstanceOf[A]
    private[di] def apply(value: R): A = {
      // if (memo == null) {
      val result = constructor(value)
      // memo = result
      result
      // } else memo
    }
  }

  // Create a provider from a function
  def provideConstructor[B](f0: Function0[B]): Provider[Any, B] =
    Provider[Any, B]((_: Any) => f0())
  def provideConstructor[A1, B](f1: Function1[A1, B]): Provider[A1, B] =
    Provider[A1, B](f1)
  def provideConstructor[A1, A2, B](
      f2: Function2[A1, A2, B]
  ): Provider[(A1, A2), B] =
    Provider[(A1, A2), B](f2.tupled)

  // A provided dependency of type A
  // type Provided[A] = A
  final case class Provided[A](a: A) extends AnyVal

  // Provide a simple value
  def provide[A](value: A): Provided[A] = Provided(value)

  // Provide a value lazily
  def provideSuspended[A](value: => A): Provider[Any, A] =
    Provider((_: Any) => value)

  // Retrieve a dependency of type A by resolving an implicit Provided[A] instance
  def provided[A](implicit pr: Provided[A]): A = pr.a

  trait LowPriorityProvided {

    implicit def providedFromProvider[R, A](implicit
        lyr: Provider[R, A],
        apr: Provided[R]
    ): Provided[A] =
      Provided(lyr(apr.a))

  }

  object Provided extends LowPriorityProvided {

    implicit def providedNonEmptyTuple[A, T](implicit
        apr: Provided[A],
        npr: Provided[T]
    ): Provided[(A, T)] =
      Provided((apr.a, npr.a))

    implicit val providedEmptyTuple: Provided[Unit] = Provided(())

    implicit def providedFromTrivialProvider[A](implicit
        pr: Provider[Any, A]
    ): Provided[A] =
      Provided(pr(()))
  }
}

// SERVICE DEFINITIONS WITH PROVIDERS

final case class Service1(int: Int, bool: Boolean)

object Service1 {
  // Provider instance that will be used by default because it's at top level in the
  // companion object. Can use Provided here too.
  implicit val default: di.Provider[(Int, Boolean), Service1] =
    di.provideConstructor(Service1.apply _)
}

/*
final case class Service2(str: String)

object Service2 {
  implicit val default: di.Provider[Service1, Service2] =
    di.provideConstructor((service1: Service1) =>
      Service2(s"${service1.int} - ${service1.bool}")
    )

  object providers {
    // Given Provided instance that can be imported explicitly to override
    // default instance. Can use Provider here too.
    implicit val test: di.Provided[Service2] =
      di.provide(Service2(s"TEST (no dependencies!)"))
  }
}

final case class Service3(service1: Service1, service2: Service2)

object Service3 {
  implicit val default: di.Provider[(Service1, Service2), Service3] =
    di.provideConstructor(Service3.apply _)
}
 */

// CONSTRUCT AND USE DEPENDENCIES

object Main {
  // Three ways to provide a zero-dependency type (needed by Service1):

  // 1: Provide directly with a value (eagerly evaluated)
  implicit val providedString: di.Provided[String] = di.provide("hi")

  // 2: Provide with a suspended value (lazily evaluated)
  implicit val providedInt: di.Provider[Any, Int] = di.provideSuspended {
    println("Performing side-effect...") // This should only run once
    23
  }

  // 3: Provide with a Function0 (lazily evaluated)
  implicit val providedBoolean: di.Provider[Any, Boolean] =
    di.provideConstructor(() => false)

  // Uncomment this import to inject a test version of Service2
  // import Service2.providers.test

  def main(args: Array[String]): Unit = {
    import Provided._

    val service1: Service1 = di.provided[Service1]

    /*
      def provided[A](implicit pr: Provided[A]): A = pr
      implicit val default: di.Provider[(Int, Boolean), Service1] =
     */

    // val service1 = di.provided[Service1](
    //   Provided.providedFromProvider2(
    //     Service1.default,
    //     Provided.providedFromTrivialProvider(providedInt),
    //     Provided.providedFromTrivialProvider(providedBoolean)
    //   )
    // )
    println(service1)

    // Resolve Service3 dependency from Provided/Provider instances
//     val service3 = di.provided[Service3]
//     println(service3)
  }
}

// object Main {
//   // implicit val x: Int = 0
//   def main(args: Array[String]): Unit = {
//     println("Hello 2")
//
//     imp
//   }
//
//   def imp(implicit i: Int) = {
//     println(s"i: $i")
//   }
//
// }
