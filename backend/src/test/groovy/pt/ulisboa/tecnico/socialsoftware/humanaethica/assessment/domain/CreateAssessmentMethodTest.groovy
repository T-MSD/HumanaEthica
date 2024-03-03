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
    def assessmentDto

    def setup() {
        given: "assessment info"
        assessmentDto = new AssessmentDto()
        assessmentDto.review = ASSESSMENT_REVIEW_1
        assessmentDto.reviewDate = DateHandler.toISOString(DATE)
        assessmentDto.setVolunteer(volunteer)
        assessmentDto.setInstitution(institution)
    }
}