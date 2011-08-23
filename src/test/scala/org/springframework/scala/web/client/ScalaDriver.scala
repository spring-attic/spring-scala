package org.springframework.scala.web.client

object ScalaDriver {

  def main(args: Array[String]) {
    val template = new RestTemplate()
    template.delete("http://localhost")


  }
}