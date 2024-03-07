package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.*
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import spock.lang.Unroll

@DataJpaTest
class RegisterEnrollmentServiceTest extends SpockTest {
    Activity activity = Mock()
    Volunteer volunteer = Mock()
    def enrollmentDto

    def setup() {
        enrollmentDto = new EnrollmentDto()
    }

    @Unroll
    def 'register enrollment for volunteer in activity'() {
        given:
        activityRepository.findById(activityId) >> Optional.of(activity)
        userRepository.findById(volunteerId) >> Optional.of(volunteer)
        enrollmentDto.motivation = motivation

        when:
        def result = enrollmentService.registerEnrollment(volunteerId, activityId, enrollmentDto)

        then:
        //1 * enrollmentRepository.save(_ as Enrollment)
        result.activity == activity
        result.volunteer == volunteer
        result.motivation == motivation

        where:
        activityId | volunteerId | motivation
        1          | 1           | 'Test motivation'
        2          | 2           | 'Another motivation'
    }

    @Unroll
    def 'register enrollment with invalid arguments: activityId=#activityId, volunteerId=#volunteerId, motivation=#motivation'() {
        given:
        activityRepository.findById(activityId) >> Optional.empty()
        userRepository.findById(volunteerId) >> Optional.empty()
        enrollmentDto.motivation = motivation

        when:
        enrollmentService.registerEnrollment(volunteerId, activityId, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == errorMessage

        where:
        activityId | volunteerId | motivation      || errorMessage
        null       | 1           | 'Test motivation' || ErrorMessage.ACTIVITY_NOT_FOUND
        1          | null        | 'Test motivation' || ErrorMessage.USER_NOT_FOUND
        1          | 1           | null            || ErrorMessage.ENROLLMENT_MOTIVATION_REQUIRED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
