package pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;


import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;


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

    public Participation(Integer rating, Activity activity, Integer userId){
        setRating(rating);
        setAcceptanceDate(DateHandler.now());
        setActivity(activity);
        setVolunteer(volunteer);

        verifyInvariants();
    }

    public Participation(Activity activity, Integer userId){
        setAcceptanceDate(DateHandler.now());
        setActivity(activity);
        setVolunteer(volunteer);

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
    }

    public Activity getActivity() {
        return activity;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
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

        if(activity.getParticipationList().contains(volunteer)){
            throw new HEException(PARTICIPATION_VOLUNTEER_ALREADY_SET);
        }
    }

    public void participationDeadlineOver(){
        if(DateHandler.now().isAfter(activity.getApplicationDeadline())){
            throw new HEException(PARTICIPATION_ACTIVITY_ONGOING);
        }
    }

}
