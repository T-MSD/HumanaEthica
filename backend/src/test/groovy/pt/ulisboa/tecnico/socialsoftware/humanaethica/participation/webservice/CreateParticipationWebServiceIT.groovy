package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.webservice

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthNormalUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateParticipationWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def activityId
    def participationDto
    def volunteer

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)



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
        given: 'a member'
            demoMemberLogin()

        when:
            def response = webClient.post()
                    .uri('/participations/' + activityId + '/create')
                    .headers(httpHeaders -> httpHeaders.putAll(headers))
                    .bodyValue(participationDto)
                    .retrieve()
                    .bodyToMono(ParticipationDto.class)
                    .block()

        then: "check response"

        activityRepository.findAll().size() == 1
        response.getVolunteer().getId() == volunteer.getId()
        response.getRating() == RATING_1

        cleanup:
        deleteAll()
    }

    def "create participation as a non member"(){
        given: 'a non member'
            demoAdminLogin()

        when:
            def response = webClient.post()
                    .uri('/participations/' + activityId + '/create')
                    .headers(httpHeaders -> httpHeaders.putAll(headers))
                    .bodyValue(participationDto)
                    .retrieve()
                    .bodyToMono(ParticipationDto.class)
                    .block()

        then: "an error is returned"
            def error = thrown(WebClientResponseException)
            error.statusCode == HttpStatus.FORBIDDEN

        cleanup:
        deleteAll()
    }
}