package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.ActivityService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.AuthUserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.UserService
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import spock.lang.Unroll

import javax.xml.crypto.Data

@DataJpaTest
class GetEnrollmentsByActivityTest extends SpockTest{
    def activity
    def activityDto
    def setup() {
        given: "an institution and activity info"
        def institution = institutionService.getDemoInstitution()
        def volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL,User.State.ACTIVE)
        userRepository.save(volunteer)
        activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,1,ACTIVITY_DESCRIPTION_1,
                IN_ONE_DAY,IN_TWO_DAYS,IN_THREE_DAYS,null)

        and: "a theme"
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))

        and: "enrollment info"
        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = MOTIVATION_RIGHT
        enrollmentDto.activity = activityDto
        enrollmentDto.enrollmentDateTime = IN_THREE_DAYS

        and: "an enrollment"
        activity = new Activity(activityDto, institution, themes as List<Theme>)
        activityRepository.save(activity)
        enrollmentDto.setEnrollmentDateTime(DateHandler.toISOString(NOW))
        def enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)

        /*
        and: "another enrollment"
        volunteer = authUserService.loginDemoVolunteerAuth().getUser()
        enrollmentDto = createEnrollmentDto(ENROLLMENT_MOTIVATION_LESS, activity, IN_TWO_DAYS)
        enrollment = new Enrollment(activity, volunteer, enrollmentDto)
        enrollmentRepository.save(enrollment)
         */
    }

    def 'get one enrollment by activity'() {
        when:
        def activity_result = activityService.getActivities().get(0).getId()
        def result = enrollmentService.getEnrollmentsByActivity(activity_result)

        then:
        result.size() == 1
        result.get(0).getActivity().getId() == activity_result
    }

    @Unroll
    def 'get two enrollments by activity'() {
        given: "another enrollment"
        def volunteer2 = new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL,User.State.ACTIVE)
        userRepository.save(volunteer2)

        def enrollmentDto = new EnrollmentDto()
        enrollmentDto.motivation = MOTIVATION_RIGHT
        enrollmentDto.activity = activityDto
        enrollmentDto.enrollmentDateTime = IN_THREE_DAYS

        def enrollment = new Enrollment(activity, volunteer2, enrollmentDto)
        enrollmentRepository.save(enrollment)

        when:
        def activity_result = activityService.getActivities().get(0).getId()
        def List<EnrollmentDto> result = enrollmentService.getEnrollmentsByActivity(activity_result)

        then:
        result.size() == 2
        result.get(0).getActivity().getId() == activity_result
        result.get(1).getActivity().getId() == activity_result
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}