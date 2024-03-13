package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetActivityEnrollmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def institution = institutionService.getDemoInstitution()
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        activityId = activity.getId()

    }
}