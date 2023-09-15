/*
 * Copyright 2023 OrdinaryRoad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ordinaryroad.barrage.fly.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import tech.ordinaryroad.barrage.fly.constant.BarrageFlyTaskStatusEnum
import tech.ordinaryroad.barrage.fly.context.BarrageFlyTaskContext
import tech.ordinaryroad.barrage.fly.service.BarrageFlyTaskService
import tech.ordinaryroad.live.chat.client.commons.base.msg.BaseMsg.OBJECT_MAPPER

@Component
class BarrageFlyStatsHandler(private val barrageFlyTaskService: BarrageFlyTaskService) {

    private val log = LoggerFactory.getLogger(BarrageFlyStatsHandler::class.java)

    fun counts(request: ServerRequest): Mono<ServerResponse> {
        val ids = barrageFlyTaskService.findAllIds()
        val taskContexts = BarrageFlyTaskContext.taskContexts

        // 已连接的客户端总数
        var clientsCount = 0
        taskContexts.forEach { (_, v) -> clientsCount += v.rSocketClientMsgPublishers.size }

        // 已创建不同状态的任务个数
        val taskStatuses = taskContexts.map {
            val taskStatus = it.value.status
            OBJECT_MAPPER.createObjectNode().apply {
                put("status", taskStatus.name)
                put("platform", it.value.platform.name)
                put("roomId", it.value.roomId)
                put("taskId", it.value.taskId)
            }
        }.groupBy {
            it.get("status")
        }

        // 每个运行中的任务连接的客户端数
        val runningTasks = taskContexts
            .filter {
                it.value.status == BarrageFlyTaskStatusEnum.RUNNING
            }
        val taskClients = barrageFlyTaskService.findIds(runningTasks.map { it.key })
            .map {
                OBJECT_MAPPER.createObjectNode().apply {
                    putPOJO("task", HashMap<String, String>(3).apply {
                        put("platform", it.platform.name)
                        put("roomId", it.roomId)
                        put("id", it.uuid)
                    })
                    put("clientCount", runningTasks[it.uuid]?.rSocketClientMsgPublishers?.size ?: 0)
                }
            }

        return ServerResponse.ok().bodyValue(
            OBJECT_MAPPER.createObjectNode().apply {
                put("tasksCount", ids.size)
                put("clientsCount", clientsCount)
                putPOJO("taskStatuses", taskStatuses)
                putPOJO("taskClients", taskClients)
            }
        )
    }
}