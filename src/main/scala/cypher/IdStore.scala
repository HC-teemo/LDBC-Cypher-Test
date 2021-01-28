package cypher

import java.io.File

import scala.io.Source
import scala.util.Random

/**
 * @ClassName IdStore
 * @Description TODO
 * @Author huchuan
 * @Date 2021/1/25
 * @Version 0.1
 */
class IdStore(file: File){

  val _name: String = file.getName

  val _ids: Array[String] = getIds(file)

  var iter: Iterator[String] = _

  def nextId(): String = {
    if (iter == null)
      iter = _ids.toIterator

    iter.next()
  }

  def randomId(): String = _ids(Random.nextInt(_ids.length))

  def getIds(file: File): Array[String] = {
    val s = Source.fromFile(file, "UTF-8")
    val ids = s.getLines().toArray
    s.close()
    ids
  }

}
