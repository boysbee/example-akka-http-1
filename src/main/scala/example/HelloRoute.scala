// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package example

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import example.HelloService.{Hello, SayHello}
import kamon.annotation.{EnableKamon, Trace}

import scala.concurrent.ExecutionContext

@EnableKamon
trait HelloRoute extends LazyLogging {

  import scala.concurrent.duration._

  private implicit val internalTimeout = Timeout(10.seconds)

  @Trace("hello")
  def hello(helloService: ActorRef)(implicit ctx: ExecutionContext): Route =
    path("hello" / IntNumber) { number =>
      get {
        onSuccess(helloService ? SayHello(number)) {
          case Hello(msg) => complete(msg)
          case _ => complete(StatusCodes.InternalServerError)
        }
      }
    }
}
