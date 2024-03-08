/*package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.repository.InstitutionRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.dto.InstitutionDto




@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateAssessmentWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def assessmentDto
    def institutionId


    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        given:
        assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)



        and:

        def institution = new Institution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)
        institutionRepository.save(institution)



        assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1)

        institutionId = institution.id


    }

    def "login as volunteer, and create an assessment"() {
        given:
        demoVolunteerLogin()

        when:
        def response = webClient.post()



                .uri("/assessments/" + institutionId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(assessmentDto)
                .retrieve()
                .bodyToMono(AssessmentDto.class)
                .block()

        then:
        response.review == ASSESSMENT_REVIEW_1


        and: "check database date"
        assessmentRepository.count() == 1
        def assessment = assessmentRepository.findAll().get(0)
        assessment.getReview() == ASSESSMENT_REVIEW_1


        cleanup:
        deleteAll()
    }

}
*/