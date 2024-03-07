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
    Enrollment otherEnrollment = Mock()
    def enrollmentDto

    def setup() {
        
        enrollmentDto = new EnrollmentDto()

    }

   def "create enrollment for volunteer to an activity"() {
        given:
        enrollmentDto.setMotivation(MOTIVATION_RIGHT)
        enrollmentDto.setEnrollmentDateTime(DateHandler.toISOString(NOW))
        activity.getApplicationDeadline() >> IN_ONE_DAY
        activity.getEnrollments() >> []
        volunteer.getEnrollments() >> []


        
        when:
        def e = new Enrollment(activity, volunteer, enrollmentDto)


        then:
        e.getActivity() == activity
        e.getVolunteer() == volunteer
        e.getMotivation() == enrollmentDto.motivation

        and: "invocations"
        1 * activity.addEnrollment(_)
        1 * volunteer.addEnrollment(_)
    
    }

    @Unroll
    def "create enrollment and violate invariants motivation length"() {
        given:
        enrollmentDto.setMotivation(MOTIVATION_WRONG)

        when:
        def res = new Enrollment(activity, volunteer, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_MOTIVATION_SHOULD_HAVE_10_CHAR
    }

   @Unroll
   def "test creating enrollment twice for a volunteer in the same activity"() {
        given:

        otherEnrollment.getVolunteer() >> volunteer
        otherEnrollment.getActivity() >> activity
        volunteer.getEnrollments() >> [otherEnrollment]
        activity.getEnrollments() >> [otherEnrollment]
        activity.getApplicationDeadline() >> IN_ONE_DAY
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(MOTIVATION_RIGHT)


       when:
       Enrollment e = new Enrollment(activity, volunteer, enrollmentDto)

       then:
       // Expect HEException with VOLUNTEER_ALREADY_IN_THIS_ACTIVITY error message
       def error = thrown(HEException)
       error.getErrorMessage() == ErrorMessage.VOLUNTEER_ALREADY_IN_THIS_ACTIVITY
    }


    @Unroll
    def "test applying after deadline should throw exception"() {

        given:
        activity.getApplicationDeadline() >> ONE_DAY_AGO
        volunteer.getEnrollments() >> []
        enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation(MOTIVATION_RIGHT)
        enrollmentDto.setEnrollmentDateTime(DateHandler.toISOString(NOW))

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
