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




        assessmentDto = createAssessmentDto(ASSESSMENT_REVIEW_1, IN_THREE_DAYS)



    }

    def "login as volunteer, and create an assessment"() {
        given:
        demoVolunteerLogin()
        def institutionDto = new InstitutionDto()
        institutionDto.setName(INSTITUTION_1_NAME)
        institutionDto.setEmail(INSTITUTION_1_EMAIL)
        institutionDto.setNif(INSTITUTION_1_NIF)
        def institution = institutionService.registerInstitution(institutionDto)

        institutionId = institution.id

        when:
        def response = webClient.post()



                .uri("/institutions/" + institutionId)
                .headers(httpHeaders -> httpHeaders.putAll(headers))
                .bodyValue(AssessmentDto)
                .retrieve()
                .bodyToMono(InstitutionDto.class)
                .block()

        then:
        response.review == ASSESSMENT_REVIEW_1
        response.reviewDate == DateHandler.toISOString(IN_THREE_DAYS)

        and: "check database date"
        assessmentRepository.count() == 1
        def assessment = assessmentRepository.findAll().get(0)
        assessment.getReview() == ASSESSMENT_REVIEW_1
        assessment.getReviewDate() == IN_THREE_DAYS

        cleanup:
        deleteAll()
    }

}
*/