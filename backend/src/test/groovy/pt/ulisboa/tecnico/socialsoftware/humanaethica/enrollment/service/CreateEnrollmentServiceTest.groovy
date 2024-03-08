package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.repository.ActivityRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.repository.EnrollmentRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.UserRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import spock.lang.Unroll

@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest {

    def institution
    def theme
    def volunteer
    def activity
    def enrollmentRepository

    def setup() {
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        institution = institutionService.getDemoInstitution()
        theme = new Theme(THEME_NAME_1, Theme.State.APPROVED,null)
        themeRepository.save(theme)
        
    }

    def 'test createEnrollment for user id #userId and activity id #activityId'() {
        given:"an enrollment dto"

        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,themesDto)

        activity = new Activity(activityDto, institution, theme)
        activityRepository.save(activity)

        def enrollmentDto = createEnrollmentDto(MOTIVATION_RIGHT, NOW)

        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto)

        then: "the returned data is correct"

        result.motivation == MOTIVATION_RIGHT
        result.enrollmentDateTime == DateHandler.toISOString(NOW)
        result.activity.id == activity.getId()
        result.volunteer.id == volunteer.getId()    

        and: "the enrollment is saved in the database"
        enrollmentRepository.findAll().size() == 1

        and: "the stored data is correct"
        def storedEnrollment = enrollmentRepository.findById(result.id).get()
        storedEnrollment.motivation == MOTIVATION_RIGHT
        storedEnrollment.enrollmentDateTime == NOW
        storedEnrollment.activity.id == activity.getId()
        storedEnrollment.volunteer.id == volunteer.getId()

    }


/*
    @Unroll
    def 'test getEnrollmentsByActivity for activity id #activityId'() {
        given:
        // Create some enrollments for the given activity
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
        activityId | expectedEnrollmentsSize
        EXISTING_ACTIVITY_ID | 2
        NON_EXISTING_ACTIVITY_ID | 0 // Non-existing activity ID
    }
    */
    
}
