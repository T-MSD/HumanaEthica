package groovy.pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetInstitutionAssessmentsWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        def institution = institutionService.getDemoInstitution()
        def volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        given: "assessment info"
        def assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, IN_THREE_DAYS)

        and: "an assessment"
        def assessment = new Assessment(assessmentDto, institution, volunteer)
        assessmentRepository.save(assessment)


    }

    def "get assessments"() {
        when:
        def response = webClient.get()
                .uri('/assessments')
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .retrieve()
                .bodyToFlux(AssessmentDto.class)
                .collectList()
                .block()

        then: "check response"
        response.size() == 2
        response.get(1).review == ASSESSMENT_REVIEW_1
        DateHandler.toLocalDateTime(response.get(1).reviewDate).withNano(0) == IN_THREE_DAYS.withNano(0)

        cleanup:
        deleteAll()
    }
}


