package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation


import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User

@DataJpaTest
class CreateParticipationServiceTest extends SpockTest {
    
    def activityId
    def participationDto
    def volunteer

    def setup() {
        
        
        given: "activity create"
        def activityDto = createActivityDto(ACTIVITY_NAME_1,ACTIVITY_REGION_1,3,ACTIVITY_DESCRIPTION_1,
                ONE_DAY_AGO,IN_TWO_DAYS,IN_THREE_DAYS,null)
        def themes = new ArrayList<>()
        themes.add(createTheme(THEME_NAME_1, Theme.State.APPROVED,null))
        def institution = institutionService.getDemoInstitution()
        def activity = new Activity(activityDto, institution, themes)
        activityRepository.save(activity)
        activityId = activity.getId()
        
        
        given: "volunteer create"
        volunteer = new Volunteer(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.SUBMITTED)
        ((AuthNormalUser) volunteer.authUser).setActive(true)
        userRepository.save(volunteer)


        given: "participation create"
        participationDto = new ParticipationDto()
        participationDto.setRating(RATING_1)
        participationDto.setVolunteer(new UserDto(volunteer))

    }

    def "create participation"() {
        when:
        def result = participationService.createParticipation(activityId, participationDto)


        then: "check result"
        activityRepository.findAll().size() == 1
        result.getVolunteer().getId() == volunteer.getId()
        result.getRating() == RATING_1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}