/*
  ******************************************************************************
  * Copyright 2018, Oath Inc.
  * Licensed under the terms of the Apache Version 2.0 license.
  * See LICENSE file in project root directory for terms.
  ******************************************************************************
*/

package com.aol.one.dwh.bandarlog.providers

import com.aol.one.dwh.bandarlog.connectors.KafkaConnector
import com.aol.one.dwh.infra.config.Topic
import kafka.common.TopicAndPartition
import org.mockito.Mockito.when
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar


class KafkaOutMessagesProviderTest extends FunSuite with MockitoSugar {

  private val kafkaConnector = mock[KafkaConnector]
  private val topic = Topic("topic_id", Set("topic_1", "topic_2"), "group_id")

  test("check count of out messages/offsets over all topic partitions") {
    val offsets = Option(Map(
      TopicAndPartition("topic_1", 1) -> 1L,
      TopicAndPartition("topic_2", 2) -> 2L,
      TopicAndPartition("topic_3", 3) -> 3L
    ))
    when(kafkaConnector.getOffsets(topic)).thenReturn(offsets)

    val result = new KafkaOutMessagesProvider(kafkaConnector, topic).provide()

    assert(result.getValue.nonEmpty)
    assert(result.getValue.get == 6) // 1 + 2 + 3
  }

  test("check count of out messages/offsets for empty offsets result") {
    when(kafkaConnector.getOffsets(topic)).thenReturn(Some(Map[TopicAndPartition, Long]()))

    val result = new KafkaOutMessagesProvider(kafkaConnector, topic).provide()

    assert(result.getValue.nonEmpty)
    assert(result.getValue.get == 0)
  }

  test("return none if can't retrieve offsets") {
    when(kafkaConnector.getOffsets(topic)).thenReturn(None)

    val result = new KafkaOutMessagesProvider(kafkaConnector, topic).provide()

    assert(result.getValue.isEmpty)
  }
}
