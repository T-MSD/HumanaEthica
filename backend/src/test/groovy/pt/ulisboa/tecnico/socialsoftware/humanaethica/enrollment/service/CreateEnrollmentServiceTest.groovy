package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.EnrollmentService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import spock.lang.Unroll

@DataJpaTest
class RegisterEnrollmentServiceTest extends SpockTest {
    public static final Integer EXISTING_VOLUNTEER_ID = 1
    public static final Integer EXISTING_ACTIVITY_ID = 1

    def userRepository
    def activityRepository
    def enrollmentRepository
    def enrollmentService

    def setup() {
        userRepository = Mock()
        activityRepository = Mock()
        enrollmentRepository = Mock()
        enrollmentService = new EnrollmentService(userRepository, activityRepository, enrollmentRepository)

    }

    @Unroll
    def 'test get enrollments by activity for activity id #activityId'() {
        given:
        // Create some enrollments for the given activity
        def activity = new Activity()
        activity.setId(EXISTING_ACTIVITY_ID)
        def volunteer = new Volunteer()
        volunteer.setId(EXISTING_VOLUNTEER_ID)
        def enrollment1 = new Enrollment(activity, volunteer, new EnrollmentDto())
        def enrollment2 = new Enrollment(activity, volunteer, new EnrollmentDto())
        enrollmentRepository.saveAll([enrollment1, enrollment2])

        when:
        def enrollments = enrollmentService.getEnrollmentsByActivity(activityId)

        then:
        enrollments.size() == expectedEnrollmentsSize

        where:
        activityId           | expectedEnrollmentsSize
        EXISTING_ACTIVITY_ID | 2
        2                    | 0 // Non-existing activity id
    }

    @Unroll
    def 'test register enrollment for user id #userId and activity id #activityId'() {
        given:
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.setMotivation("Motivation")

        when:
        def result = enrollmentService.registerEnrollment(userId, activityId, enrollmentDto)

        then:
        result != null

        where:
        userId                | activityId
        EXISTING_VOLUNTEER_ID | EXISTING_ACTIVITY_ID
        EXISTING_VOLUNTEER_ID | 2 // Non-existing activity id
        2                     | EXISTING_ACTIVITY_ID // Non-existing user id
    }
}
