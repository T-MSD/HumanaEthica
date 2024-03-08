package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import spock.lang.Unroll



@DataJpaTest
class CreateEnrollmentServiceTest extends SpockTest{
    def volunteer
    def activity
    def activityDto
    def institution
    def themes
    def setup() {
        given: 'a volunteer and an institution'
        institution = institutionService.getDemoInstitution()
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()

        and: "a theme"
        themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))

    }

    def "create an enrollment" () {
        given:
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        def enrollmentDto = createEnrollmentDto(MOTIVATION_RIGHT, NOW, activityDto)

        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto)

        then:
        result.activityDto.getId() ==  activity.getId()
        result.getId() == volunteer.getId()
        result.getMotivation() == MOTIVATION_RIGHT
        result.getEnrollmentDateTime() == DateHandler.toISOString(NOW)
    }


    @Unroll
    def "create enrollment and violate invariants motivation length"() {
        given:
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        def enrollmentDto1 = createEnrollmentDto(MOTIVATION_WRONG, NOW, activityDto)

        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto1)
        
        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_MOTIVATION_SHOULD_HAVE_10_CHAR
    }

    @Unroll
    def "test creating enrollment twice for a volunteer in the same activity"() {
        given:
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        def enrollmentDto = createEnrollmentDto(MOTIVATION_RIGHT, NOW, activityDto)

        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto)
        def result2 = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_ALREADY_EXISTS
    }

    @Unroll
    def "invalid argument: volunteerId=#volunteerId"() {

        given:
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        def enrollmentDto = createEnrollmentDto(MOTIVATION_RIGHT, NOW, activityDto)

        when:
        def volunteerId = null
        def result = enrollmentService.createEnrollment(volunteerId, activity.getId(), enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_FOUND
    }

    @Unroll
    def "invalid argument: activityId=#activityId"() {

        given:
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        def enrollmentDto = createEnrollmentDto(MOTIVATION_RIGHT, NOW, activityDto)

        when:
        def activityId = null
        def result = enrollmentService.createEnrollment(volunteer.getId(), activityId, enrollmentDto)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ACTIVITY_NOT_FOUND
    }

    @Unroll
    def "invalid argument: enrollmentDto=#enrollmentDto"() {

        given:
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        def enrollmentDto = createEnrollmentDto(MOTIVATION_RIGHT, NOW, activityDto)

        when:
        def result = enrollmentService.createEnrollment(volunteer.getId(), activity.getId(), null)

        then:
        def error = thrown(HEException)
        error.getErrorMessage() == ErrorMessage.ENROLLMENT_DOES_NOT_EXIST
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}