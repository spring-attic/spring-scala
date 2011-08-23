package org.springframework.scala.web.client

object ScalaDriver {

  def main(args: Array[String]) {
    val template = RestTemplate()
    template.delete("http://{bla}xample.com", 'e')


  }
}