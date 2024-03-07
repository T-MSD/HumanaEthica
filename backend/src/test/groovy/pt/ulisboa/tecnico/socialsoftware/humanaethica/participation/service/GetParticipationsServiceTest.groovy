package pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class GetParticipationsServiceTest extends SpockTest {

    def activityId

    def setup() {
        given: "create activity"
            deleteAll()
            def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                    ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
            def themes = new ArrayList<>()
            themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
            def institution = institutionService.getDemoInstitution()
            def activity = new Activity(activityDto, institution, themes)
            activityRepository.save(activity)
            activityId = activity.getId()

        and: "create volunteers"
            def volunteer1 = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
            userRepository.save(volunteer1)
            ((AuthNormalUser) volunteer1.authUser).setActive(true)

            def volunteer2 = new Volunteer(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
            userRepository.save(volunteer2)
            ((AuthNormalUser) volunteer2.authUser).setActive(true)

        and: "create participation dtos"

            def participationDto1 = new ParticipationDto()
            participationDto1.setRating(RATING_1)
            def participationDto2 = new ParticipationDto()
            participationDto2.setRating(RATING_1)

        and: "set participations"

            def participation1 = new Participation(activity, volunteer1, participationDto1)
            participationRepository.save(participation1)
            def participation2 = new Participation(activity, volunteer2, participationDto2)
            participationRepository.save(participation2)

    }

    def 'get two participations'() {
        when:
        def result = participationService.getParticipationsByActivity(activityId)
        then:
        result.size() == 2
        result.get(0).getVolunteer().getName() == USER_1_NAME
        result.get(1).getVolunteer().getName() == USER_2_NAME
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
