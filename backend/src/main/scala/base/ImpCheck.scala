package base

//object ImpCheck {
//
//  case class A(i: Int) {
//    def toB = B(i)
//  }
//  object A {
//    implicit def orderingA(implicit ord: Ordering[B]): Ordering[A] = Ordering.by(a => a.i)
//  }
//
//  case class B(i: Int) {
//    def toA = A(i)
//  }
//  object B {
//    implicit def orderingB(implicit ord: Ordering[A]): Ordering[B] = Ordering.by(_.toA)
//  }
//
//  def check[A](i: A)(implicit s: Ordering[A]): Unit = {
//    println(s"Checking $s")
//  }
//
//  def main(args: Array[String]): Unit = {
//    check[Int](1)
//    implicitly[Ordering[A]]
//  }
//
//}
