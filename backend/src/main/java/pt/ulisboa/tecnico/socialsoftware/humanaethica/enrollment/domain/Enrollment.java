    package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler;
import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;

    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDateTime;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Enrollment() {
    }

    public Enrollment(Activity activity, Volunteer volunteer, EnrollmentDto enrollmentDto) {
        setActivity(activity);
        setVolunteer(volunteer);
        setMotivation(enrollmentDto.getMotivation()); 
        setEnrollmentDateTime(DateHandler.toLocalDateTime(enrollmentDto.getEnrollmentDateTime()));

        verifyInvariants();
    }

    // Getters and setters...

    public Activity getActivity() {
        return activity;
    }

    public int getId() {
        return this.id;
    }

    public LocalDateTime getDateTime() {
        return  this.enrollmentDateTime;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public String getMotivation(){
        return motivation;
    }

    public LocalDateTime getEnrollmentDateTime() {
        return enrollmentDateTime;
    }

    public void setEnrollmentDateTime(LocalDateTime enrollmentDateTime) {
        this.enrollmentDateTime = enrollmentDateTime;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    private void verifyInvariants() {
        checkMotivation();
        checkVolunteer();
    }

    private void checkMotivation() {
        if (this.motivation.length() < 10) {
            throw new HEException(ENROLLMENT_MOTIVATION_SHOULD_HAVE_10_CHAR);
        }
    }

    private void checkVolunteer() {
        if (isVolunteerEnrolledInAnotherActivity()) {
            throw new HEException(VOLUNTEER_ALREADY_IN_ANOTHER_ACTIVITY);
        }
    }

    // Custom method to check if the volunteer is already enrolled in another activity
    // (1.2 A volunteer can only enrol in one activity.)
    public boolean isVolunteerEnrolledInAnotherActivity() {
        List<Enrollment> enrollments = volunteer.getEnrollments();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getId() != this.id && enrollment.getActivity() != null) {
                // Volunteer is already enrolled in another activity
                return true;
            }
        }
        return false;
    }
}
