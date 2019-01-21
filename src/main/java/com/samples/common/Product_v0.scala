package com.samples.common

import org.insightedge.scala.annotation._
import org.insightedge.spark.model.BucketedGridModel

import scala.beans.{BeanProperty, BooleanBeanProperty}


case class Product_v0(
                    @BeanProperty @SpaceId var id: Long,
                    @BeanProperty var description: String,
                    @BeanProperty var quantity: Int,
                    @BooleanBeanProperty var featuredProduct: Boolean
                  )  {
  def this() = this(-1, null, -1, false)
}