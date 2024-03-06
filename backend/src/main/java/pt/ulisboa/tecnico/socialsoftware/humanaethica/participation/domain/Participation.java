package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;


@Entity
@Table(name = "participation")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    private Integer rating;
    private LocalDateTime acceptanceDate;

    // constructors

    public Participation(){
    }

    public Participation(Activity activity, Volunteer volunteer, ParticipationDto participationDto){

        setRating(participationDto.getRating());

        setAcceptanceDate(DateHandler.now());

        setActivity(activity);
        setVolunteer(volunteer);

        verifyInvariants();
    }

    public void update(ParticipationDto participationDto){
        setRating(participationDto.getRating());
        verifyInvariants();
    }

    // sets and gets

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public void setAcceptanceDate(LocalDateTime acceptanceDate) {
        this.acceptanceDate = acceptanceDate;
    }

    public LocalDateTime getAcceptanceDate() {
        return acceptanceDate;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        activity.addParticipation(this);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
        volunteer.addParticipation(this);
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public Integer getId() {
        return id;
    }

    public Integer getVolunteerId(){
        return getVolunteer().getId();
    }

    // Invariants

    private void verifyInvariants(){
        activityNotFull();
        volunteerNotOnActivity();
        participationDeadlineOver();
    }

    public void activityNotFull(){

        Integer limit = activity.getParticipantsNumberLimit();
        Integer count = activity.getParticipationList().size();

        if(limit > count){
            return;
        }
        else if(limit == count){
            throw new HEException(PARTICIPATION_ACTIVITY_FULL);
        }
        else{
            throw new HEException(PARTICIPATION_ACTIVITY_OVERFLOW);
        }
    }

    public void volunteerNotOnActivity(){

        if(activity.getParticipationList().stream().anyMatch(participation -> participation != this && participation.getVolunteer() == this.getVolunteer())){
            throw new HEException(PARTICIPATION_VOLUNTEER_ALREADY_SET);
        }
    }

    public void participationDeadlineOver(){
        if(!DateHandler.now().isAfter(activity.getApplicationDeadline())){
            throw new HEException(PARTICIPATION_ACTIVITY_ONGOING);
        }
    }

}
