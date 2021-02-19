package part_01

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object RDDTest extends App {

  def halt():Unit = {
    println("> ")
    System.in.read
  }


  val spark = SparkSession
    .builder()
    .appName("RDDTest")
    .config("spark.master", "local[*]")
    .getOrCreate()


  val listOfWords = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum pellentesque egestas sapien, sit amet porta metus sollicitudin eu. Nam vel molestie mi. Sed malesuada feugiat metus nec fermentum. In hac habitasse platea dictumst. Sed at nibh enim. Nam sagittis sodales purus ut pulvinar. Mauris pretium justo in ipsum suscipit, ut porta augue varius. Cras sit amet eleifend arcu. Ut porttitor viverra dui, et dignissim tortor fermentum nec. Sed augue est, sagittis vitae mollis quis, aliquet a elit. Donec pellentesque elementum ipsum, a rhoncus lectus sollicitudin ac. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas.\n\nVestibulum id feugiat justo. Curabitur ullamcorper diam leo, non gravida nibh egestas in. In a ipsum eget ex tincidunt volutpat. Donec eu pellentesque nunc. Vivamus sed enim sit amet lectus vehicula interdum. Nunc varius eleifend mi ut hendrerit. Nulla posuere, odio at pharetra sagittis, nunc ante feugiat magna, eget tempus tellus risus ut felis. Aenean sit amet enim auctor, ornare quam at, commodo dui. Pellentesque lacinia at odio et luctus. Nam eu laoreet nunc. Integer vitae felis pellentesque, vulputate massa at, commodo arcu. Suspendisse a tincidunt mauris.\n\nCras viverra, odio quis rutrum fermentum, nulla metus aliquam justo, vel tristique massa tellus eu augue. Nullam euismod commodo metus, ut interdum orci consequat interdum. Fusce non urna lobortis, varius augue in, auctor ligula. Suspendisse aliquam venenatis purus vel ultrices. Nulla semper rhoncus quam. Sed pharetra at enim quis dapibus. Nulla et dui leo. Aenean molestie, sapien in rhoncus lacinia, augue eros dictum enim, eu fermentum quam nibh et risus. Etiam auctor, augue at porttitor aliquet, est lectus suscipit diam, eget ornare est ante et nibh. Proin congue, nulla et venenatis porta, leo nunc lacinia urna, vel vestibulum erat felis non lacus.\n\nIn convallis, nibh at malesuada maximus, elit tortor tincidunt metus, at convallis enim purus nec ante. Vivamus a felis velit. Quisque rhoncus ipsum ac leo gravida iaculis. In hac habitasse platea dictumst. Duis vitae dui at tortor lacinia lobortis eget nec risus. Duis cursus, arcu dignissim ultricies luctus, nulla diam scelerisque augue, in vulputate metus massa quis nulla. Nullam fermentum tincidunt consectetur. Vestibulum volutpat, ipsum eget lobortis dapibus, libero purus scelerisque nulla, ac maximus dolor dui eget lacus. Donec bibendum felis eu ipsum vestibulum blandit. Vivamus at leo tincidunt quam cursus commodo at vulputate lorem.\n\nFusce eros urna, rhoncus sit amet leo sed, placerat sollicitudin neque. Duis eleifend tempor nunc ac rutrum. Curabitur massa lorem, consectetur ut orci et, accumsan tempus ligula. Nunc et pharetra odio. Vivamus fringilla, mi non blandit pretium, velit magna pharetra neque, ac lacinia turpis lectus ac augue. Sed non erat risus. Fusce dapibus molestie lorem, sit amet fringilla lacus ullamcorper in. Curabitur enim purus, varius ac semper eget, consectetur et nisl. Integer eu fringilla odio. Cras facilisis lobortis eros a mollis. Maecenas dignissim diam sed enim efficitur placerat."
    .toLowerCase
    .replaceAll("""[^a-z ]""","")
    .split(" ")
    .toList

  println("Number of words : " + listOfWords.length)

  val rdd:RDD[String] = spark.sparkContext.parallelize(listOfWords)

  val newRdd:RDD[String] = rdd
    .filter(_.length > 2)  // <- transformation added to lineage
    .flatMap{              // <- transformation added to lineage
      case s if s.startsWith("l") => Some(s)
      case _ => None
    }
    .sortBy(_.length)   // <- transformation added to lineage


  newRdd.foreach(println) // <- action

  halt

}
