package com.samples.ie

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.insightedge.spark.context.InsightEdgeConfig
import org.insightedge.spark.implicits.basic._

/**
  * Loads Product RDD from Data Grid and prints objects count.
  */
object LoadRdd {

  def main(args: Array[String]): Unit = {
    val initConfig = InsightEdgeConfig.fromSparkConf(new SparkConf())

    //args: <spark master url> <space name>
    val settings =  if (args.length > 0) args
    else Array( new SparkConf().get("spark.master", InsightEdgeConfig.SPARK_MASTER_LOCAL_URL_DEFAULT),
      initConfig.spaceName)

    if (settings.length != 2) {
      System.err.println("Usage: LoadRdd <spark master url> <space name>")
      System.exit(1)
    }
    val Array(master, space) = settings
    val ieConfig = initConfig.copy(spaceName = space)
    val spark = SparkSession.builder
      .appName("example-load-rdd")
      .master(master)
      .insightEdgeConfig(ieConfig)
      .getOrCreate()
    val sc = spark.sparkContext

    val rdd = sc.gridRdd[Product]()
    println(s"Products RDD count: ${rdd.count()}")
    spark.stopInsightEdgeContext()
  }

}
