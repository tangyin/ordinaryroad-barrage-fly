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
package tech.ordinaryroad.barrage.fly.constant

import tech.ordinaryroad.live.chat.client.commons.base.msg.IDanmuMsg
import tech.ordinaryroad.live.chat.client.commons.base.msg.IGiftMsg
import tech.ordinaryroad.live.chat.client.commons.base.msg.IMsg

/**
 * @author mjz
 * @date 2023/9/14
 */
enum class MsgTypeEnum {
    DANMU,
    GIFT, ;

    companion object {
        fun getByMsg(msg: IMsg?): MsgTypeEnum? {
            return when (msg) {
                is IDanmuMsg -> {
                    DANMU
                }

                is IGiftMsg -> {
                    GIFT
                }

                else -> {
                    null
                }
            }
        }
    }
}
