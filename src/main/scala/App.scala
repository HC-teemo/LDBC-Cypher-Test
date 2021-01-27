import java.io.{File, FileFilter, FilenameFilter}

import cypher.CypherQueryStore
import org.apache.commons.cli.{BasicParser, HelpFormatter, Option, Options, PosixParser}
import org.apache.log4j.Logger
import org.neo4j.driver.v1.{AuthTokens, GraphDatabase, Session}

/**
 * @ClassName TestMain
 * @Description TODO
 * @Author huchuan
 * @Date 2021/1/25
 * @Version 0.1
 */
object App{
  private val log:Logger = Logger.getRootLogger

  /**
   * java -jar LDBC-Cypher-Test.jar --cypher ./cypher --id ./id --url bolt://10.0.82.217:7687 --username neo4j --password 123456 --times 10
   * java -jar LDBC-Cypher-Test.jar --cypher ./cypher --id ./id --url panda://10.0.82.217:9989 --username pandadb --password pandadb --times 10
   *
   * @param args
   */

  def main(args: Array[String]): Unit = {

    var cypherPath:String = ""
    var idPath:String = ""
    var url:String = ""
    var username:String = ""
    var password:String = ""
    var random: Boolean = false
    var time:Int = 1

    // create Options object
    val options = new Options()
    options.addOption(new Option("c", "cypher", true, "path of cypher files(String)"))
    options.addOption(new Option("i", "id", true, "path of id files(String)"))
    options.addOption(new Option("r", "random",false, "replace id randomly"))
    options.addOption(new Option("u", "url", true, "url of database(String)"))
    options.addOption(new Option("n", "username", true, "username of database(String)"))
    options.addOption(new Option("p", "password", true, "password of database(String)"))
    options.addOption(new Option("t", "times", true, "run each cypher times(Int)"))
    options.addOption(new Option("h", "help", false, "show help"))
    val formatter = new HelpFormatter()

    try{
      // create the command line parser
      val parser = new PosixParser()
      val commandLine = parser.parse(options, args)
      if (commandLine.hasOption('h')){
        // print usage
        formatter.printHelp( "", options)
        System.exit(0)
      }
      if (!(commandLine.hasOption('c')&&
        commandLine.hasOption('i')&&
        commandLine.hasOption('u')&&
        commandLine.hasOption('n'))){
        println("arguments -c(--cypher), -i(--id), -u(--url), -n(--username) is required!")
        formatter.printHelp( "-c ./cyphers -i ./ids -u bolt://localhost:7687 -n neo4j -p neo4j", options)
        System.exit(0)
      }
      cypherPath = commandLine.getOptionValue('c')
      idPath = commandLine.getOptionValue('i')
      url = commandLine.getOptionValue('u')
      username = commandLine.getOptionValue('n')
      if (commandLine.hasOption('p')) password = commandLine.getOptionValue('p')
      random = commandLine.hasOption('r')
      if (commandLine.hasOption('t')) time = commandLine.getOptionValue('t').toInt
    } catch {
      case ex: Exception =>
        println( "Unexpected exception:" + ex.getMessage )
        formatter.printHelp( "-c ./cyphers -i ./ids -u bolt://localhost:7687 -n neo4j -p neo4j", options)
    }

    val cypherPathFile = new File(cypherPath)
    val idPathFile = new File(idPath)

    if (!(cypherPathFile.exists() && idPathFile.exists())){
      println( "invalid path!" )
    }

    val cypherFiles = cypherPathFile.listFiles(new FilenameFilter {
      override def accept(file: File, s: String): Boolean = s.contains(".cypher")
    })

    val idFiles = idPathFile.listFiles().sortBy(_.getName)

    val cypherQueryStore = new CypherQueryStore(cypherFiles, idFiles)

    val cyphers = cypherQueryStore.getCypher(time, random)

    val neo4jDriver =
      GraphDatabase.driver(url, AuthTokens.basic(username, password))
    val session = neo4jDriver.session()
    cyphers.foreach(cy => cypherTest(cy._1, session, cy._2))
    session.close()
    neo4jDriver.close()

  }

  def cypherTest(cypherName: String, session: Session, cyphers: Array[String]): Unit = {
    val length = cyphers.length
    log.info(s"Start test: ${cypherName} with ${length} times.")
    var time: Long = 0
    var times = 0
    cyphers.foreach{
     c=>
//       val res0 = session.run(c)
//       res0.hasNext
       val singleStartTime = System.currentTimeMillis()
       var count = 0
       val res = session.run(c)
       while(res.hasNext){
         val record = res.next().toString
         count += 1
       }
       val singleEndTime = System.currentTimeMillis()
       time += (singleEndTime-singleStartTime)
       times +=1
       log.info(s"[${times}/${length}]  records: ${count}, time: ${singleEndTime - singleStartTime}ms, cypher:${c.split(')')(0)}")
    }
    val endTime = System.currentTimeMillis()
    log.info(s"finish ${cypherName}with ${length} times. time: ${time}ms, avg time: ${time/length}ms")
  }

}
