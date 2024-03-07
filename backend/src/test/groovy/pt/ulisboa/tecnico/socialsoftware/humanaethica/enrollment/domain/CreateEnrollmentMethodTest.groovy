package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CreateEnrollmentMethodTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    def enrollmentDto

    def setup() {
        given: "enrollment info"
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setEnrollmentDateTime(DateHandler.toISOString(NOW))
    }

//    def "create enrollment for volunteer to an activity"() {
//        when:
//        def e = new Enrollment(activity, volunteer, enrollmentDto)
//
//        then:
//        1 * activity.addEnrollment(e)
//        1 * volunteer.addEnrollment(e)
//        activity.getEnrollments().contains(e)
//        volunteer.getEnrollments().contains(e)
//        e.getActivity() == activity
//        e.getVolunteer() == volunteer
//        e.getMotivation() == enrollmentDto.motivation
//        e.getEnrollmentDateTime().equals(enrollmentDto.enrollmentDateTime)
//    }


    def "create enrollment and violate invariants motivation length :  motivation=#motivation"() {
        given:
        enrollmentDto.setMotivation(MOTIVATION)

        when:
        def res = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_MOTIVATION_SHOULD_HAVE_10_CHAR
    }

//    @Unroll
//    def "test creating enrollment twice for a volunteer in the same activity"() {
//        given:
//        Enrollment e1 = new Enrollment(activity, volunteer, enrollmentDto)
//
//        when:
//        Enrollment e2 = new Enrollment(activity, volunteer, enrollmentDto)
//
//        then:
//        // Expect HEException with VOLUNTEER_ALREADY_IN_THIS_ACTIVITY error message
//        def error = thrown(HEException)
//        error.getErrorMessage() == ErrorMessage.VOLUNTEER_ALREADY_IN_THIS_ACTIVITY
//    }

    @Unroll
    def "test applying after deadline should throw exception"() {
        given:
        activity.getApplicationDeadline >> ONE_DAY_AGO
        //enrollmentDto.getEnrollmentDateTime >> DateHandler.toISOString(NOW)

        when:
        def res = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        // Expect HEException with APPLICATION_DEADLINE_PASSED error message
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.APPLICATION_DEADLINE_PASSED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
