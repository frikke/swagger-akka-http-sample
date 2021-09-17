package com.example.akka.addoption

import javax.ws.rs.Path

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import io.swagger.annotations._

import com.example.akka.DefaultJsonFormats
import com.example.akka.addoption.AddOptionActor._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

@Api(value = "/add", produces = "application/json")
@Path("/addOption")
class AddOptionService(addActor: ActorRef)(implicit executionContext: ExecutionContext)
  extends Directives with DefaultJsonFormats {

  implicit val timeout = Timeout(2.seconds)

  implicit val requestFormat = jsonFormat2(AddOptionRequest)
  implicit val responseFormat = jsonFormat1(AddOptionResponse)

  val route = addOption

  @ApiOperation(value = "Add optional integers", nickname = "addOptionalIntegers", httpMethod = "POST", response = classOf[AddOptionResponse])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "\"numbers\" to sum", required = true,
        dataTypeClass = classOf[AddOptionRequest], paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 500, message = "Internal server error")
  ))
  def addOption =
    path("addOption") {
      post {
        entity(as[AddOptionRequest]) { request =>
          complete { (addActor ? request).mapTo[AddOptionResponse] }
        }
      }
    }

}
