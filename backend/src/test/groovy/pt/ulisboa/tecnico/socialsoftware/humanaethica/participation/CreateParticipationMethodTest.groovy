package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler


import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

@DataJpaTest
class CreateParticipationMethodTest extends SpockTest {

    Activity activity = Mock();
    Volunteer volunteer = Mock();
    def participationDto

    def setup(){
        given: "participation info"
        participationDto = new ParticipationDto()
        participationDto.rating = RATING_1
    }

    def "create participation"(){
        given:
            activity.getParticipationList() >> new ArrayList<>();
            activity.getParticipantsNumberLimit() >> 2;
            activity.getApplicationDeadline() >> ONE_DAY_AGO;

        when:
            def result = new Participation(activity, volunteer, participationDto)

        then: "check result"
            result.getActivity() == activity;
            result.getVolunteer() == volunteer;
            result.getRating() == RATING_1;
        and: "invocations"
            1 * activity.addParticipation(_)
            1 * volunteer.addParticipation(_)

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}