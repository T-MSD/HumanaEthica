package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;

import java.time.LocalDateTime;


public class ParticipationDto{

    private Integer id;
    private Integer rating;
    private String acceptanceDate;
    private ActivityDto activity;
    private UserDto volunteer;


    public ParticipationDto(){

    }

    public ParticipationDto(Participation participation){
        setId(participation.getId());
        setRating(participation.getRating());
        setAcceptanceDate(DateHandler.toISOString(participation.getAcceptanceDate()));

        setActivity(new ActivityDto(participation.getActivity(), false));
        setVolunteer(new UserDto(participation.getVolunteer()));
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setAcceptanceDate(String acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public String getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public void setActivity(ActivityDto activity) {
        this.activity = activity;
    }

    public ActivityDto getActivity() {
        return activity;
    }

    public void setVolunteer(UserDto volunteer) {
        this.volunteer = volunteer;
    }

    public UserDto getVolunteer() {
        return volunteer;
    }


    @Override
    public String toString() {
        return "ParticipationDto{" +
                "id=" + getId() +
                ", activity=" + getActivity() +
                ", volunteer=" + getVolunteer() +
                ", rating=" + getRating() +
                ", acceptancedate='" + acceptanceDate + '\''
                + "}";
    }
}