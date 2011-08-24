package org.springframework.scala.web.client

import java.net.URI
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.FileCopyUtils
import java.io.InputStreamReader

object ScalaDriver {

  def main(args: Array[String]) {
    val template = new RestTemplate()
//    template.delete("http://localhost")

    val result = template.execute(new URI("http://localhost"), HttpMethod.GET) {
      response: ClientHttpResponse => FileCopyUtils.copyToString(new InputStreamReader(response.getBody))
    }
    println(result.getClass)


  }
}