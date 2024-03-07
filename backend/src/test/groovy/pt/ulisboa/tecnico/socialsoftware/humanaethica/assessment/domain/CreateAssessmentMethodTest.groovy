package pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import spock.lang.Unroll

import java.time.LocalDateTime


@DataJpaTest
class CreateAssessmentMethodTest extends SpockTest {
    Volunteer volunteer = Mock()
    Institution institution = Mock()
    Assessment otherAssessment = Mock()
    Activity activity = Mock()

    def assessmentDto

    def setup() {
        given: "assessment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessmentDto.reviewDate = DateHandler.toISOString(IN_TWO_DAYS)
        activity.endingDate = IN_THREE_DAYS


    }

    def "create assessment with volunteer and institution has another assessment"() {
        given:
        otherAssessment.getReview() >> ASSESSMENT_REVIEW_2
        institution.getAssessments() >> [otherAssessment]
        institution.checkForCompletedActivity() >> true



        when:
        def result = new Assessment(assessmentDto, institution, volunteer)

        then: "check result"
        result.getReview() == ASSESSMENT_REVIEW_1
        result.getReviewDate() == IN_TWO_DAYS
        result.getInstitution() == institution

    }

    def "review with at least 10 characters"() {
        given:
        institution.checkForCompletedActivity() >> true

        and:
        assessmentDto = new AssessmentDto()
        assessmentDto.review = "123456789"

        when:
        new Assessment(assessmentDto, institution, volunteer)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ASSESSMENT_INVALID_REVIEW_LENGTH
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}

    
}