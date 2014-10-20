import play.api.test._


class ControllerSpec extends PlaySpecification {

  import controllers.{ Application => A }

  val google = FakeRequest("GET", "/shortener?url=http%3A%2F%2Fgoogle.com")
  val pellucid = FakeRequest("GET", "/shortener?url=http%3A%2F%2Fpellucid.com")

  "Application" should {
    "/ return a form" in {
      val res = A.index()(FakeRequest())
      status(res) mustEqual OK
      contentAsString(res) must contain("<form")
    }
    "invalid url" in new WithApplication {
      val res = route(FakeRequest("GET", "/shortener?url=xxxx")).get
      status(res) mustEqual OK
      contentAsString(res) must contain("Invalid URL")
    }
    "valid url" in new WithApplication {
      val res = route(google).get
      status(res) mustEqual OK
      contentAsString(res) must contain("http://localhost:9000/go/0")
    }
    "repeating urls" in new WithApplication {
      val res = route(google).get
      contentAsString(res) must contain("http://localhost:9000/go/0")
      val res2 = route(google).get
      contentAsString(res2) must contain("http://localhost:9000/go/0")
      val res3 = route(pellucid).get
      contentAsString(res3) must contain("http://localhost:9000/go/1")
      val res4 = route(pellucid).get
      contentAsString(res4) must contain("http://localhost:9000/go/1")
    }
    "redirect" in new WithApplication {
      val res = route(google).get
      status(res) mustEqual OK
      val res2 = route(FakeRequest("GET", "/go/0")).get
      header(LOCATION, res2) mustEqual Option("http://google.com")
    }
  }
}
