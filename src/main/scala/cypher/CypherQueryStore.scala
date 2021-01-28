package cypher

import java.io.File
import java.util.regex.Matcher


import scala.io.Source

/**
 * @ClassName CypherGenerator
 * @Description TODO
 * @Author huchuan
 * @Date 2021/1/25
 * @Version 0.1
 */
class CypherQueryStore(cypherFiles: Array[File], idFiles: Array[File]){

  val _cyphers: Array[(String, String)] = cypherFiles.map(getCypherFromFile).sortBy(_._1)

  val _ids: Map[String, IdStore] = idFiles.map(f => f.getName -> new IdStore(f)).toMap

  def getCypherFromFile(file: File): (String, String) ={
    val s = Source.fromFile(file, "UTF-8")
    val c = s.mkString
    s.close()
    (file.getName, c)
  }

  def getCypher(num: Int, random:Boolean = false): Array[(String, Array[String])] = {
    _cyphers.map{
      cypher =>
        (cypher._1, (0 until num).map(i => cypherConverter(cypher._2, _ids, random)).toArray)
    }
  }

  def cypherConverter(cypher: String, ids: Map[String, IdStore], random:Boolean):String = {
    var res = cypher
    while (res.contains("$")){
      ids.foreach{
        kv =>
          if (res.contains(kv._1)){
            val id = if(random) kv._2.randomId() else kv._2.nextId()
            res = res.replaceFirst(Matcher.quoteReplacement("$"+kv._1), id)
          }
      }
    }
    res
  }
}
