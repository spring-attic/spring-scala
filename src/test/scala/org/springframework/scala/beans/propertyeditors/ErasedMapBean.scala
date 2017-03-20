package org.springframework.scala.beans.propertyeditors

/**
 * Created with IntelliJ IDEA.
 * Author: Edmondo Porcu
 * Date: 08/05/13
 * Time: 11:00
 *
 */
case class ErasedMapBean(data:Map[JavaEnum,String]) {

}

case class WrongMapBean(data:Map[Double, Int])
