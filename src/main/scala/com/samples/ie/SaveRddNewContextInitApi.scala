package com.samples.ie

import com.samples.common.{Product, Product_v0}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.insightedge.spark.context.InsightEdgeConfig
//import org.insightedge.spark.implicits.basic._
import org.apache.spark.sql.DataFrame
import org.insightedge.spark.implicits.all._

import scala.util.Random

/**
  * Generates 100000 Products, converts to Spark RDD and saves to Data Grid. Products have fixed IDs.
  */
object SaveRddNewContextInitApi {

  def main(args: Array[String]): Unit = {
    val settings = if (args.length > 0)
      args
    else
      Array(new SparkConf().get("spark.master", InsightEdgeConfig.SPARK_MASTER_LOCAL_URL_DEFAULT),
        sys.env.getOrElse(InsightEdgeConfig.INSIGHTEDGE_SPACE_NAME, InsightEdgeConfig.INSIGHTEDGE_SPACE_NAME_DEFAULT))

    if (settings.length != 2) {
      System.err.println("Usage: SaveRdd <spark master url> <space name>")
      System.exit(1)
    }

    val Array(master, space) = settings
    println(s"master is: $master")
    println(s"space is: $space")
    val config = InsightEdgeConfig(space)
    val spark = SparkSession.builder
      .appName("example-save-rdd-new-context-init-api")
      .master(master)
      .getOrCreate()

    //initializing the insightedge context via the spark context
    spark.sparkContext.initializeInsightEdgeContext(config)

    val sc = spark.sparkContext

    val productsNum = 1000000
    println(s"Saving $productsNum products RDD to the space")
    val rdd = sc.parallelize(1 to productsNum /*, 4 */).map { i =>
      Product(i, "Description of product " + i, Random.nextInt(10), Random.nextBoolean())
    }

    rdd.saveToGrid()

    // read it back as dataframe
    val df: DataFrame = spark.read.option("splitCount", "4").grid[Product]
    df.show()

    df.write.mode(SaveMode.Overwrite).grid("Product")

    Thread.sleep(120000)

    sc.stopInsightEdgeContext()
  }

}

