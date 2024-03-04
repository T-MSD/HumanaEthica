package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import java.time.LocalDateTime


@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest {
    Volunteer volunteer = Mock();
    Institution institution = Mock();
    Assessment otherAssessment = Mock();
    def assessmentDto

    def setup() {
        given: "assessment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessmentDto.reviewDate = DateHandler.toISOString(DATE)
        assessmentDto.setVolunteer(volunteer)
        assessmentDto.setInstitution(institution)
    }

    def "create assessment with volunteer and institution has another assessment"() {
        given:
        otherAssessment.getReview() >> ASSESSMENT_REVIEW_2
        institution.getAssessments() >> [otherAssessment]
        volunteer.getId() >> VOLUNTEER_ID_1

        when:
        def result = new Assessment(assessmentDto, institution, volunteer)

        then: "check reuslt"
        result.getReview() == ASSESSMENT_REVIEW_1
        result.getReviewData() == DATE
        result.getInstitution() == institution
        result.getId() == volunteer
        and "invocations"
        1 * institution.addAssessment()
    }



}