    package pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "enrollment")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String motivation;
    private LocalDateTime enrollmentDateTime;

    @ManyToOne
    private Activity activity;

    @ManyToOne
    private Volunteer volunteer;

    public Enrollment() {
    }

    // Getters and setters...

    public Activity getActivity() {
        return activity;
    }

    public int getId() {
        return this.id;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public String getMotivation(){
        return this.motivation;
    }

    public boolean checkMotivation() {
        return getMotivation().length() >= 10;
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
